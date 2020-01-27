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

package amazing.model.statements;

import amazing.controller.Result;
import amazing.controller.interpreter.Token;
import amazing.controller.interpreter.Visitor;
import amazing.model.Tuple.Pair;

/**
 * class for representing errors occurred while parsing
 */
public class Error extends Statement {

    private final String msg;
    private final Token token;
    private final Pair<Integer, Integer> errorPosition;

    /**
     * constructor for error statement
     *
     * @param token         statement / token which caused error
     * @param msg           error message
     * @param errorPosition position of the error
     */
    public Error(final Token token, final String msg, final Pair<Integer, Integer> errorPosition) {
        this.token = token;
        this.msg = msg;
        this.errorPosition = errorPosition;
    }

    @Override
    public Result<?, amazing.controller.Error> accept(
            final Visitor<Result<?, amazing.controller.Error>> visitor) {
        return visitor.visit(this);
    }

    public Token getToken() {
        return this.token;
    }

    public Pair<Integer, Integer> getErrorPosition() {
        return this.errorPosition;
    }

    @Override
    public boolean isError() {
        return true;
    }

    @Override
    public String toString() {
        return this.msg;
    }
}
