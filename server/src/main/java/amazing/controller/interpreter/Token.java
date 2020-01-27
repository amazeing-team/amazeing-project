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

import static amazing.controller.Util.equalsWithNulls;

public final class Token {

    private final TokenKind tokenKind;
    private final Pair<Integer, Integer> position;

    /**
     * This constructor is used to create tokens whose text is is not of interest after lexing.
     *
     * @param tokenKind Defines which token will be created.
     * @param position  The position of the first character of the text the token was identified
     *                  with.
     */
    public Token(final TokenKind tokenKind, final Pair<Integer, Integer> position) {
        this.tokenKind = tokenKind;
        this.position = position;
    }


    @Override
    public boolean equals(final Object o) {
        if (o == null) {
            return false;
        }
        if (o.getClass() != Token.class) {
            return false;
        }
        final Token t = (Token) o;
        return equalsWithNulls(t.position, position) && equalsWithNulls(t.tokenKind, tokenKind);
    }

    @Override
    public int hashCode() {
        return position.hashCode();
    }

    @Override
    public String toString() {
        return tokenKind.toString() + ' ' + position.toString();
    }

    public Pair<Integer, Integer> getPosition() {
        return position;
    }

    public TokenKind getTokenKind() {
        return tokenKind;
    }
}
