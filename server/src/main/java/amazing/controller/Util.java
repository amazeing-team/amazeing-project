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

import java.util.Objects;

public final class Util {

    private Util() {
    }

    /**
     * For two objects a,b this function computes a.equals(b) without throwing a NullPointerException
     * when a is null. We define null.equals(b) iff b == null.
     *
     * @param a First object for comparison.
     * @param b Second object for comparison.
     * @return True iff a and b are equal.
     */
    @SuppressWarnings("PMD.CompareObjectsWithEquals")
    public static boolean equalsWithNulls(final Object a, final Object b) {
        if (a == b) {
            return true;
        }
        if (Objects.isNull(a) || Objects.isNull(b)) {
            return false;
        }
        return a.equals(b);
    }
}
