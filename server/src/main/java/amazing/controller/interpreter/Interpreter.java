/*
 * MIT License
 *
 * Copyright (c) 2020 aMAZEing-Team
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */

package amazing.controller.interpreter;

import amazing.controller.*;
import amazing.controller.Error;
import amazing.controller.Command.CommandFactory;
import amazing.controller.Error.ErrorFactory;
import amazing.controller.Response.TaskResponse.Builder;
import amazing.controller.Result.ResultFactory;
import amazing.model.Direction;
import amazing.model.Rotation;
import amazing.model.User;
import amazing.model.statements.Statement;
import amazing.model.statements.player.Move;
import amazing.model.statements.player.Turn;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayDeque;
import java.util.List;
import java.util.Queue;

import static amazing.Config.MAX_STACK;

/**
 * Interpreter for the aMAZEing DSL: Simulates the semantics of a given program on the Game {@link
 * Game}.
 */
@SuppressWarnings("PMD.ExcessiveClassLength")
public final class Interpreter implements Visitor<Result<?, Error>>, Runnable {

    private static final Logger LOGGER = LoggerFactory.getLogger(Interpreter.class);
    private final Command.CommandFactory cmdfactory;
    private final Game game;
    private final String str;
    private final User user;
    private final Builder responseBuilder;
    private final Player player;
    private List<Statement> program;
    private Direction dir;
    private boolean done;
    private int idx;
    private final Queue<String> log;
    private int stepCount;

    /**
     * Constructor for the Interpreter, call
     *
     * @param game            The Board the program is build for
     * @param user            The program writter
     * @param str             The pure program string, fed through to {@link Lexer}, {@link Parser}
     * @param responseBuilder Response{@link Builder} for server-client communication
     * @see #init() before to set up
     */
    public Interpreter(@NotNull final Game game, final User user, final String str,
                       final Builder responseBuilder) {
        this.game = game;
        this.responseBuilder = responseBuilder;
        this.str = str;
        this.player = game.getPlayers().get(user);
        this.user = user;
        this.cmdfactory = new CommandFactory(user);
        this.idx = 0;
        this.log = new ArrayDeque<>();
    }


    /**
     * initialize the program and Interpreter, do not call any other method before Calls Lexer, Parser
     * and TypeChecker
     *
     * @return Result for potential statically detected error
     */
    @NotNull
    public Result<?, Error> init() {
        LOGGER.trace(this.str);
        final List<Token> lex;
        try {
            lex = new Lexer(this.str).lex();
        } catch (final LexingException le) {
            return ResultFactory
                    .createError(ErrorFactory.createInterpreterError("Lexing Error: " + le.getMessage()));
        }
        final Parser parser = new Parser(lex);
        this.program = parser.parse();
        if (this.program.isEmpty()) {
            return ResultFactory.createError(ErrorFactory.createInterpreterError("Empty Program"));
        }

        for (final Statement s : program) {
            if (s.isError()) {
                return ResultFactory
                        .createError(ErrorFactory.createInterpreterError("Parsing Error: " + s.toString()));
            }
        }

        this.dir = this.player.getDirection();
        return ResultFactory.createOk();
    }

    //Run

    /**
     * Run the Interpreter to the end, calls step() until end flag is set or an error occurs
     *
     * @see #step()
     */
    @Override
    public void run() {
        while (!this.done) {
            final Result<?, Error> result = this.step();
            if (result.isError()) {
                result.error().ifPresent(error -> this.responseBuilder.setError(error.toString()));
                return;
            }
        }
        this.responseBuilder.setWin(game.getWinners().contains(user));
    }

    /**
     * Runs a command on the Game
     *
     * @param cmd Command to run (e.g. Command move)
     * @return Result for success
     * @see Command move
     */
    @NotNull
    private Result<?, Error> run(final Command cmd) {
        final Result<?, Error> result = this.game.run(cmd);
        if (result.isError()) {
            return ResultFactory.createError(ErrorFactory.createInterpreterError(
                    String.format("Error Command execution: %s", result.error().get().toString())));
        }
        return ResultFactory.createOk();
    }


    //OneStep

    /**
     * Applies to effect of the current statement
     *
     * @return Result for success
     */
    @NotNull
    @SuppressWarnings("PMD.AvoidCatchingThrowable")
    private Result<?, Error> step() {
        try {
            final Statement currecntS = this.program.get(this.idx);
            if (!(currecntS.isLabelDefinition())) {
                final Result<?, Error> result = currecntS.accept(this);
                if (result.isError()) {
                    this.done = true;
                    return result;
                }
            }
        } catch (final Throwable e) {
            return ResultFactory.createError(ErrorFactory
                    .createInterpreterError(String.format("Unknown Exception in step: %s", e.toString())));
        }
        this.stepCount++;
        this.idx++;
        this.done = this.done || this.idx >= this.program.size();
        if (this.stepCount > MAX_STACK) {
            return ResultFactory.createError(ErrorFactory.createStackError());
        }
        this.responseBuilder.addFrame(this.game.buildFrame(user, log));
        return ResultFactory.createOk();
    }

    /**
     * Sends turn command to the Game
     *
     * @param turn Statement
     * @return Result for success
     */
    @NotNull
    @Override
    public Result<?, Error> visit(@NotNull final Turn turn) {
        log.add(String.format("turn %s", turn.turnLeft() ? "left" : "right"));
        LOGGER.trace("{}", turn);
        this.dir = this.dir.rotate(turn.turnLeft() ? Rotation.LEFT : Rotation.RIGHT);
        final Result<?, Error> result = this.run(
                turn.turnLeft() ? this.cmdfactory.createTurnLeft() : this.cmdfactory.createTurnRight());
        if (result.isError()) {
            return ResultFactory.createError(ErrorFactory
                    .createInterpreterError("Game - Turn error: " + result.error().get().toString()));
        }
        return ResultFactory.createOk();
    }

    /**
     * "Charge forward!!!" - moves the player forward
     *
     * @param move Statement
     * @return Result for success
     */
    @NotNull
    @Override
    public Result<?, Error> visit(final Move move) {
        log.add("move");
        LOGGER.trace("{}", "move");
        final Result<?, Error> runCmd = this.run(this.cmdfactory.createMove());
        if (this.game.isGameOver(this.user)) {
            this.done = true;
        }
        if (runCmd.isError()) {
            return ResultFactory.createError(ErrorFactory
                    .createInterpreterError("Game-Move error: " + runCmd.error().get().toString()));
        }
        return ResultFactory.createOk();
    }

    /**
     * called for unknown Statements
     *
     * @param statement Statement
     * @throws UnsupportedOperationException Always thrown as no interpretation for unknown
     *                                       statements
     */
    @Override
    public Result<?, Error> visit(final Statement statement) {
        throw new UnsupportedOperationException();
    }

}
