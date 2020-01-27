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

package amazing.model;

import amazing.model.Tuple.Pair.Coordinate;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

import java.util.Locale;

import static com.google.common.math.IntMath.mod;

/**
 * Cardinal Directions.
 */
public enum Direction {
    NORTH(0, new Coordinate(0, -1)),
    EAST(1, new Coordinate(1, 0)),
    SOUTH(2, new Coordinate(0, 1)),
    WEST(3, new Coordinate(-1, 0));
    private final int idx;
    private final Coordinate offset;

    @Contract(pure = true)
    Direction(final int idx, final Coordinate offset) {
        this.idx = idx;
        this.offset = offset;
    }

    /**
     * Each direction has an offset, by which the next discrete coordinate in that direction can be
     * calculated.
     *
     * @return the coordinate e.g. (0,-1) for NORTH
     */
    @Contract(pure = true)
    public Coordinate getOffset() {
        return this.offset;
    }

    /**
     * @return the opposite direction e.g. NORTH when called on SOUTH
     */
    public Direction mirror() {
        return Direction.values()[mod(this.idx + 2, Direction.values().length)];
    }

    /**
     * Rotates starting from a given direction.
     *
     * @param rotation direction to rotate to e.g. LEFT
     * @return the direction the player faces after rotation
     */
    public Direction rotate(@NotNull final Rotation rotation) {
        switch (rotation) {
            case LEFT:
                return Direction.values()[mod(this.idx - 1, Direction.values().length)];
            case RIGHT:
                return Direction.values()[mod(this.idx + 1, Direction.values().length)];
            case BACK:
                return Direction.values()[mod(this.idx + 2, Direction.values().length)];
            default:
                return this;
        }
    }

    @Override
    public String toString() {
        return super.toString().toLowerCase(Locale.US);
    }
}
