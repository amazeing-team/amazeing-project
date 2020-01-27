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

import amazing.controller.Item;
import com.google.gson.annotations.Expose;
import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

import java.util.Arrays;
import java.util.Collections;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * Class representing a mark on a tile in the game.
 */
public class Marker extends SerializableJson {

    @Expose
    private final Set<Direction> directions;

    public Marker(final Direction... directions) {
        this.directions = Arrays.stream(directions).collect(Collectors.toUnmodifiableSet());
    }

    public Marker(final Set<Direction> directions) {
        this.directions = Collections.unmodifiableSet(directions);
    }

    public Set<Direction> getDirections() {
        return this.directions;
    }

    public boolean hasDirection(final Direction direction) {
        return this.directions.contains(direction);
    }

    @Override
    public int hashCode() {
        int bits = 0;
        for (final Direction direction : Direction.values()) {
            bits <<= 1;
            if (this.directions.contains(direction)) {
                bits |= 1;
            }
        }
        return bits;
    }

    @Contract(value = "null -> false", pure = true)
    @Override
    public boolean equals(final Object obj) {
        if (Objects.nonNull(obj) && obj instanceof Marker) {
            return obj == this || ((Marker) obj).directions.equals(this.directions);
        }
        return false;
    }

    /**
     * Class representing a tile. Every tile has directions in which it can be left / from which it
     * can be accessed.
     */
    public static class Tile extends Marker implements Cloneable {

        @Expose(deserialize = false)
        private Item item;

        public Tile(final Direction... directions) {
            super(directions);
            this.item = null;
        }

        public Tile(final Set<Direction> directions) {
            super(directions);
            this.item = null;
        }

        private Tile(final Set<Direction> directions, final Item item) {
            super(directions);
            this.item = item;
        }

        public void reset() {
            this.item = null;
        }

        /**
         * Leaves an item on the field, overwrites the old item, if present.
         *
         * @param item the new item
         */
        public void dropItem(@NotNull final Item item) {
            this.item = item;
        }

        public boolean hasItem() {
            return Objects.nonNull(this.item);
        }

        /**
         * Pops the items off the tile, hasItem() will always return false afterwards.
         *
         * @return the Item which was on the tile or null
         */
        public Item popItem() {
            try {
                return this.item;
            } finally {
                this.item = null;
            }
        }

        @Override
        @SuppressFBWarnings("COM_PARENT_DELEGATED_CALL")
        public int hashCode() {
            return super.hashCode();
        }

        @Override
        @SuppressWarnings("PMD.UselessOverridingMethod")
        public boolean equals(final Object obj) {
            return super.equals(obj);
        }

        @Override
        @SuppressFBWarnings("CN_IDIOM_NO_SUPER_CALL")
        @SuppressWarnings({
                "PMD.ProperCloneImplementation",
                "PMD.CloneThrowsCloneNotSupportedException"
        })
        public Tile clone() {
            return new Tile(getDirections(), item);
        }
    }
}
