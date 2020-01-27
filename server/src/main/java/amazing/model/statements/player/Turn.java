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

package amazing.model.statements.player;

import amazing.controller.Error;
import amazing.controller.Result;
import amazing.controller.interpreter.Visitor;
import amazing.model.statements.Statement;

/**
 * class for representing a left- or a right-turn of the player
 */
public class Turn extends Statement {

    private final boolean left;

    /**
     * constructor for turn
     *
     * @param left true if turn left, false if turn right
     */
    public Turn(final boolean left) {
        this.left = left;
    }

    @Override
    public Result<?, Error> accept(final Visitor<Result<?, Error>> visitor) {
        return visitor.visit(this);
    }

    @Override
    public String toString() {
        return "turn " + (left ? "left" : "right");
    }

    public boolean turnLeft() {
        return this.left;
    }

    public boolean turnRight() {
        return !this.left;
    }
}
