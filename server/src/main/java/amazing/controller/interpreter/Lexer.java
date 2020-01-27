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

import amazing.model.Tuple.Pair;
import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;

import java.util.LinkedList;
import java.util.List;

import static java.lang.Character.isWhitespace;

/**
 * Lexer for the user's program: lexes String (program) to Tokens.
 */
@SuppressWarnings({
        "checkstyle:OneStatementPerLine",
        "checkstyle:MissingSwitchDefault",
        "PMD.CyclomaticComplexity",
        "PMD.SwitchStmtsShouldHaveDefault"
})
@SuppressFBWarnings({
        "CC_CYCLOMATIC_COMPLEXITY",
        "SF_SWITCH_NO_DEFAULT",
        "STT_STRING_PARSING_A_FIELD"
})
public class Lexer {

    private static final int TAB_LENGTH = 4;
    private final String input;
    private final List<Token> tokenOutput = new LinkedList<>();
    private final Position position = new Position(1, 1);
    private final Position readMark = new Position();
    private final Position tokenPosition = new Position();
    private int tokenIndex;
    private int markIndex;
    private int index;

    /**
     * Constructor for Lexer.
     *
     * @param s user's program as string
     */
    public Lexer(final String s) {
        input = s;
        if (isWhitespace(peak())) {
            pop();
        }
    }

    private void pop() {
        final char c = peak();
        if (isWhitespace(c)) {
            handleWhitespaces(c);
        } else {
            position.x++;
        }
        index++;
    }

    private void handleWhitespaces(final char c) {
        if (isLineBreak(c)) {
            if (c == '\r' && next() == '\n') {
                index++;
            }
            position.x = 1;
            position.y++;
        } else if (c == '\t') {
            position.x += TAB_LENGTH;
        } else if (c == ' ') {
            position.x++;
        } else {
            throw new LexingException(String.valueOf(c), new Pair<>(position.x, position.y),
                    "Unrecognized character");
        }
    }

    private static boolean isLineBreak(final char c) {
        return c == '\r' || c == '\n';
    }

    private void skipWhiteSpaces() {
        while (isWhitespace(peak())) {
            pop();
        }
    }

    private char next() {
        return index < input.length() - 1 ? input.charAt(index + 1) : '\0';
    }

    private char peak() {
        return index < input.length() ? input.charAt(index) : '\0';
    }

    private void markPosition() {
        markIndex = index;
        readMark.set(position);
    }

    private void restorePosition() {
        index = markIndex;
        position.set(readMark);
    }

    private Pair<Integer, Integer> getTokenPosition() {
        return new Pair<>(tokenPosition.y, tokenPosition.x);
    }

    private boolean read(final String s, final boolean whitespaceAtEnd) {
        markPosition();
        for (int i = 0; i < s.length(); i++) {
            if (s.charAt(i) != peak()) {
                restorePosition();
                return false;
            }
            pop();
        }
        if (peak() == '\0') {
            return true;
        }
        if (whitespaceAtEnd) {
            if (!isWhitespace(peak())) {
                restorePosition();
                return false;
            } else {
                pop();
            }
        }
        return true;
    }

    private TokenKind returnOnRead(final String s, final TokenKind t) {
        return read(s, true) ? t : null;
    }

    private LexingException createLexingException(final String msg) {
        int i = tokenIndex;
        while (!(i == input.length() || isWhitespace(input.charAt(i++)))) {
        }
        String name = input.substring(tokenIndex, i);
        if (name.length() > 8) {
            name = name.substring(0, 8) + "...";
        }
        return new LexingException(name, getTokenPosition(), msg);
    }

    private Token createStandardToken(final TokenKind tk) {
        return new Token(tk, getTokenPosition());
    }


    /**
     * Starts the lexing process for the String the lexer has been initialized with.
     *
     * @return The list of Tokens the lexer created.
     */
    @SuppressWarnings("PMD.ExcessiveMethodLength")
    public List<Token> lex() {
        char c;
        while ((c = peak()) != '\0') {
            if (isWhitespace(c)) {
                skipWhiteSpaces();
                c = peak();
            }
            if (c == '\0') {
                return tokenOutput;
            }
            TokenKind tk = null;
            tokenIndex = index;
            tokenPosition.set(position);
            switch (c) {
                case 'l':
                    tk = handleL();
                    break;
                case 'm':
                    tk = handleM();
                    break;
                case 'r':
                    tk = handleR();
                    break;
                case 't':
                    tk = handleT();
                    break;
            }
            if (tk == null) {
                index = tokenIndex;
                position.set(tokenPosition);
                throw createLexingException("Unrecognized Token!");
            } else {
                tokenOutput.add(createStandardToken(tk));
            }
        }
        return tokenOutput;
    }

    private TokenKind handleL() {
        return returnOnRead("left", TokenKind.LEFT);
    }

    private TokenKind handleM() {
        return returnOnRead("move", TokenKind.MOVE);
    }

    private TokenKind handleR() {
        return returnOnRead("right", TokenKind.RIGHT);
    }

    private TokenKind handleT() {
        return returnOnRead("turn", TokenKind.TURN);
    }

    private static class Position {

        @SuppressWarnings({
                "checkstyle:EmptyLineSeparator",
                "checkstyle:MultipleVariableDeclarations"
        })
        int x, y; /* SuppressWarnings shouldn't be necessary by our config but somehow is,
                 probably a checkstyle bug */

        Position() {
        }

        Position(final int x, final int y) {
            this.x = x;
            this.y = y;
        }

        public void set(final Position position) {
            x = position.x;
            y = position.y;
        }
    }
}
