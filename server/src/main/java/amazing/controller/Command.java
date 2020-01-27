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

package amazing.controller;

import amazing.controller.Error.ErrorFactory;
import amazing.controller.Result.ResultFactory;
import amazing.model.Rotation;
import amazing.model.User;
import org.jetbrains.annotations.Contract;

public abstract class Command {

    private final Type type;
    private final User user;

    @Contract(pure = true)
    Command(final Type type, final User user) {
        this.type = type;
        this.user = user;
    }

    /**
     * Executes the command on the given game, returns an error if the game is over or the execution
     * fails.
     *
     * @param game the game to modify
     * @return result to state whether game was executed correctly or game over
     */
    public Result<?, Error> execute(final Game game) {
        if (game.isGameOver(this.user)) {
            return game.damage(this.user, ErrorFactory.createGameOver(this.user));
        }
        return ResultFactory.createOk();
    }

    User getUser() {
        return this.user;
    }

    @Override
    public String toString() {
        return String.format("%s: %s", this.user, this.type);
    }

    enum CommandType implements Type {
        MOVE,
        TURN
    }

    interface Type {

    }

    public static class CommandFactory {

        private final User user;

        @Contract(pure = true)
        public CommandFactory(final User user) {
            this.user = user;
        }

        public Command createMove() {
            return new Move(this.user);
        }

        public Command createTurnLeft() {
            return new Turn(this.user, Rotation.LEFT);
        }

        public Command createTurnRight() {
            return new Turn(this.user, Rotation.RIGHT);
        }
    }

    private static class Move extends Command {

        private Move(final User user) {
            super(CommandType.MOVE, user);
        }

        @Override
        public Result<?, Error> execute(final Game game) {
            final Result<?, Error> result = super.execute(game);
            return result.isError() ? result : game.move(this.getUser());
        }
    }

    private static class Turn extends Command {

        private final Rotation rotation;

        private Turn(final User user, final Rotation rotation) {
            super(CommandType.TURN, user);
            this.rotation = rotation;
        }

        @Override
        public Result<?, Error> execute(final Game game) {
            final Result<?, Error> result = super.execute(game);
            return result.isError() ? result : game.turn(this.getUser(), this.rotation);
        }

        @Override
        public String toString() {
            return String.format("%s %s", super.toString(), this.rotation);
        }
    }
}
