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

import amazing.model.statements.Error;
import amazing.model.statements.Statement;
import amazing.model.statements.player.Move;
import amazing.model.statements.player.Turn;
import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;

/**
 * Parser for the user's program: parses Tokens (build by Lexer {@link Lexer}) to Statements.
 */
@SuppressFBWarnings({
        "CC_CYCLOMATIC_COMPLEXITY",
        "PRMC_POSSIBLY_REDUNDANT_METHOD_CALLS"
})
public class Parser {

    private final Iterator<Token> tokenIt;
    private Token statement;
    private Token token;

    /**
     * Constructor for Parser.
     *
     * @param tokenList list of Tokens build by Lexer {@link Lexer}
     */
    public Parser(final Iterable<Token> tokenList) {
        this.tokenIt = tokenList.iterator();
        this.nextToken();
    }

    private void nextToken() {
        if (this.tokenIt.hasNext()) {
            this.token = this.tokenIt.next();
        } else {
            this.token = null;
        }
    }

    private void assertTokenNotNull() throws ParsingException {
        if (this.token == null) {
            final String msg = String.format("Statement %s is not a complete statement.%n%s.",
                    statement.getTokenKind().toString().toLowerCase(Locale.US),
                    statement.getTokenKind().getGrammar());
            throw new ParsingException(statement, msg);
        }
    }

    private void generateException(final String excepted) throws ParsingException {
        assertTokenNotNull();
        final String str = this.token.getTokenKind().toString().toLowerCase(Locale.US);
        final String msg = String.format("Expected '%s' but was '%s'.%nGRAMMAR:%n\t%s.", excepted, str,
                statement.getTokenKind().getGrammar());
        throw new ParsingException(this.token, msg);
    }


    /**
     * Starts the parsing process for the Token-List the parser has been initialized with.
     *
     * @return The list of Statements the parser created.
     */
    public List<Statement> parse() {
        final List<Statement> statements = new ArrayList<>();

        // iterate over every token
        while (this.token != null) {
            try {
                this.statement = token;
                this.nextToken();
                // check which token is given
                switch (this.statement.getTokenKind()) {
                    case MOVE:
                        statements.add(Parser.parseMove());
                        break;
                    case TURN:
                        statements.add(parseTurn());
                        break;
                    default:
                        this.generateException("Unknown statement!");
                        break;
                }
            } catch (final ParsingException ex) {
                // error occurred while parsing, token was wrong
                // add Error-statement with error message and position of wrong token
                statements.add(new Error(this.statement, ex.getMessage(), ex.getErrorPosition()));
                // stop parsing
                break;
            }
        }
        return statements;
    }

    private static Statement parseMove() {
        return new Move();
    }

    private Statement parseTurn() throws ParsingException {
        this.assertTokenNotNull();
        if (this.token.getTokenKind() == TokenKind.LEFT) {
            this.nextToken();
            return new Turn(true);
        } else if (this.token.getTokenKind() == TokenKind.RIGHT) {
            this.nextToken();
            return new Turn(false);
        }
        generateException("Turn left or right");
        return null;
    }

}
