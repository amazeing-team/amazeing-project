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

import com.google.gson.annotations.Expose;
import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

import java.util.*;
import java.util.function.BiFunction;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public interface Tuple extends Cloneable {

    /**
     * This class implements a unary tuple (Singleton).
     *
     * @param <T> type of the tuple's component
     */
    class Single<T> implements Tuple {

        @Expose
        private T first;

        @Contract(pure = true)
        Single(final T first) {
            this.first = first;
        }

        void apply(@NotNull final Function<T, T> first) {
            this.first = first.apply(this.first);
        }

        public T getFirst() {
            return this.first;
        }

        @Override
        public int hashCode() {
            return this.first.hashCode();
        }

        @Contract(value = "null -> false", pure = true)
        @Override
        public boolean equals(final Object obj) {
            if (Objects.nonNull(obj) && obj instanceof Single<?>) {
                final Single<?> single = (Single<?>) obj;
                return obj == this || single.first.equals(this.first);
            }
            return false;
        }

        @Override
        public String toString() {
            return String.format("(%s)", this.getFirst().toString());
        }
    }

    /**
     * This class implements a basic tuple.
     *
     * @param <T> type of the first component
     * @param <U> type of the second component
     */
    class Pair<T, U> extends Single<T> implements Tuple {

        @Expose
        private U second;

        @Contract(pure = true)
        public Pair(final T first, final U second) {
            super(first);
            this.second = second;
        }

        public <R> R apply(@NotNull final BiFunction<T, U, R> biFunction) {
            return biFunction.apply(this.getFirst(), this.second);
        }

        void apply(@NotNull final Function<T, T> first, @NotNull final Function<U, U> second) {
            super.apply(first);
            this.second = second.apply(this.second);
        }

        public U getSecond() {
            return this.second;
        }

        @Override
        public int hashCode() {
            return Objects.hash(super.hashCode(), this.second.hashCode());
        }

        @Contract(value = "null -> false", pure = true)
        @Override
        public boolean equals(final Object obj) {
            if (Objects.nonNull(obj) && obj instanceof Pair<?, ?>) {
                final Pair<?, ?> pair = (Pair<?, ?>) obj;
                return obj == this || super.equals(obj) && pair.second.equals(this.second);
            }
            return false;
        }

        @Override
        public String toString() {
            return String.format("(%s, %s)", this.getFirst().toString(), this.getSecond().toString());
        }

        /**
         * Class implementing the boundary of a rectangle.
         */
        public static class Boundary extends Pair<Coordinate, Coordinate>
                implements Iterable<Coordinate> {

            /**
             * Constructs the boundary. Invariant for coordinates first: (x1, y1), second: (x2, y2) is x1
             * ≤ x2 and y1 ≤ y2.
             *
             * @param first  the top-left coordinate (inclusive)
             * @param second the bottom-right coordinate (inclusive)
             */
            public Boundary(final Coordinate first, final Coordinate second) {
                super(first, second);
            }

            @NotNull
            @Override
            public Iterator<Coordinate> iterator() {
                return this.range().iterator();
            }

            /**
             * @return all discrete coordinates in the boundary
             */
            List<Coordinate> range() {
                final Coordinate min = this.getFirst();
                final Coordinate max = this.getSecond();
                assert (min.getFirst() <= max.getFirst() && min.getSecond() <= max.getSecond());
                return IntStream.rangeClosed(min.getSecond(), max.getSecond()).mapToObj(
                        y -> IntStream.rangeClosed(min.getFirst(), max.getFirst())
                                .mapToObj(x -> new Coordinate(x, y)).collect(Collectors.toSet()))
                        .flatMap(Collection::stream).sorted(Comparator.reverseOrder())
                        .collect(Collectors.toList());
            }
        }

        /**
         * Class representing a coordinate.
         */
        public static class Coordinate extends Pair<Integer, Integer>
                implements Comparable<Coordinate> {

            public Coordinate() {
                super(0, 0);
            }

            public Coordinate(final Integer x, final Integer y) {
                super(x, y);
            }

            /**
             * Returns the range of points (x +/- range, y +/-range) around the actual coordinate (x,y).
             *
             * @param range the maximal distance of x and y values
             * @return the range as a list of coordinates
             */
            public List<Coordinate> range(final int range) {
                final Boundary boundary =
                        new Boundary(new Coordinate(this.getFirst() - range, this.getSecond() - range),
                                new Coordinate(this.getFirst() + range, this.getSecond() + range));
                return boundary.range().stream().filter(coordinate -> this.manhattan(coordinate) <= range)
                        .collect(Collectors.toList());
            }

            /**
             * Calculates the manhattan distance.
             *
             * @param coordinate to which the distance shall be calculated
             * @return the manhattan distance
             */
            public int manhattan(final Coordinate coordinate) {
                return Math.abs(this.getFirst() - coordinate.getFirst()) + Math
                        .abs(this.getSecond() - coordinate.getSecond());
            }

            /**
             * Checks whether the coordinate is in a given area.
             *
             * @param boundary the area to check
             * @return true if coordinate is in  a given area, otherwise false
             */
            boolean checkBoundaries(@NotNull final Tuple.Pair.Boundary boundary) {
                final Coordinate min = boundary.getFirst();
                final Coordinate max = boundary.getSecond();
                return this.getFirst() >= min.getFirst() && this.getFirst() <= max.getFirst()
                        && this.getSecond() >= min.getSecond() && this.getSecond() <= max.getSecond();
            }

            @Override
            public int compareTo(@NotNull final Tuple.Pair.Coordinate coordinate) {
                final int tmp = Integer.compare(coordinate.getFirst(), this.getFirst());
                return tmp == 0 ? Integer.compare(coordinate.getSecond(), this.getSecond()) : tmp;
            }

            /**
             * Creates a new coordinate with the next integer values in the given direction.
             *
             * @param direction the direction e.g. NORTH
             * @return the new coordinate
             */
            public Coordinate getCoordinate(@NotNull final Direction direction) {
                final Coordinate offset = direction.getOffset();
                return new Coordinate(this.getFirst() + offset.getFirst(),
                        this.getSecond() + offset.getSecond());
            }

            @Override
            @SuppressFBWarnings("CN_IDIOM_NO_SUPER_CALL")
            @SuppressWarnings({
                    "PMD.ProperCloneImplementation",
                    "PMD.CloneThrowsCloneNotSupportedException"
            })
            public Coordinate clone() {
                return new Coordinate(this.getFirst(), this.getSecond());
            }
        }
    }
}
