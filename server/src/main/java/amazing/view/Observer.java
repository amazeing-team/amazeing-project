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

package amazing.view;

import amazing.Config;
import amazing.controller.Game;
import amazing.model.Direction;
import amazing.model.Marker.Tile;
import amazing.model.Maze;
import amazing.model.Tuple.Pair.Boundary;
import amazing.model.Tuple.Pair.Coordinate;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

import java.util.*;
import java.util.Map.Entry;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Consumer;

import static org.apache.commons.lang3.StringEscapeUtils.escapeJava;

public abstract class Observer<T> {

    private final T observable;

    Observer(final T observable) {
        this.observable = observable;
    }

    T getObservable() {
        return this.observable;
    }

    public abstract void update();

    static class AnsiView extends Observer<Game> {

        private static final Map<Tile, String> UNICODE = new ConcurrentHashMap<>(15);

        static {
            for (final Entry<Character, Set<Direction>> entry : Config.UNICODE.entrySet()) {
                UNICODE.put(new Tile(entry.getValue()), entry.getKey().toString());
            }
        }

        private final Consumer<String> consumer;

        @Contract(pure = true)
        AnsiView(final Game game, final Consumer<String> consumer) {
            super(game);
            this.consumer = consumer;
        }

        @Override
        public void update() {
            final Maze maze = this.getObservable().getMaze();
            final Boundary boundary = maze.getBoundary();
            final Coordinate min = boundary.getFirst();
            final Coordinate max = boundary.getSecond();
            final List<String> rows = new ArrayList<>(max.getSecond() - min.getSecond());
            for (int y = min.getSecond(); y <= max.getSecond(); y++) {
                final List<String> columns = new ArrayList<>(max.getFirst() - min.getFirst());
                for (int x = min.getFirst(); x <= max.getFirst(); x++) {
                    final Coordinate coordinate = new Coordinate(x, y);
                    if (this.getObservable().getPlayers().entrySet().stream()
                            .anyMatch(i -> i.getValue().getCoordinate().equals(coordinate))) {
                        columns.add(AnsiColors.BLUE.colorize(String.valueOf(
                                this.getObservable().getPlayers().entrySet().stream()
                                        .filter(i -> i.getValue().getCoordinate().equals(coordinate)).findFirst()
                                        .orElseThrow().getKey().hashCode()), true));
                    } else if (maze.isStart(coordinate)) {
                        columns.add(AnsiColors.RED.colorize("s", true));
                    } else if (maze.isExit(coordinate)) {
                        columns.add(AnsiColors.GREEN.colorize("e", true));
                    } else {
                        final Optional<Tile> tile = maze.getTile(coordinate);
                        if (tile.isPresent()) {
                            columns.add(AnsiColors.YELLOW
                                    .colorize(UNICODE.getOrDefault(tile.get(), tile.toString()), true));
                        } else {
                            columns.add(" ");
                        }
                    }
                }
                rows.add(String.join("", columns));
            }
            this.consumer.accept(String.format("%n%s", String.join("\n", rows)));
        }

        enum AnsiColors {
            RESET(0),
            BLACK(30),
            BLUE(34),
            CYAN(36),
            GREEN(32),
            PURPLE(35),
            RED(31),
            WHITE(37),
            YELLOW(33),
            BRIGHT_BLACK(90),
            BRIGHT_BLUE(94),
            BRIGHT_CYAN(96),
            BRIGHT_GREEN(92),
            BRIGHT_PURPLE(95),
            BRIGHT_RED(91),
            BRIGHT_WHITE(97),
            BRIGHT_YELLOW(93),
            BG_BLACK(40),
            BG_BLUE(44),
            BG_CYAN(46),
            BG_GREEN(42),
            BG_PURPLE(45),
            BG_RED(41),
            BG_WHITE(47),
            BG_YELLOW(43),
            BRIGHT_BG_BLACK(100),
            BRIGHT_BG_BLUE(104),
            BRIGHT_BG_CYAN(106),
            BRIGHT_BG_GREEN(102),
            BRIGHT_BG_PURPLE(105),
            BRIGHT_BG_RED(101),
            BRIGHT_BG_WHITE(107),
            BRIGHT_BG_YELLOW(103);
            final int code;

            @Contract(pure = true)
            AnsiColors(final int code) {
                this.code = code;
            }

            @Contract("_, true -> !null")
            String colorize(final String string, final boolean reset) {
                if (reset) {
                    return this.colorize(string) + RESET.colorize("");
                } else {
                    return this.colorize(string);
                }
            }

            String colorize(final String string) {
                return String.format("\u001b[%dm%s", this.code, string);
            }

            @Override
            public String toString() {
                return escapeJava(this.colorize(""));
            }
        }
    }

    public static class ObserverFactory {

        @Contract(pure = true)
        private ObserverFactory() {
            // empty
        }

        @NotNull
        @Contract(value = "_, _ -> new", pure = true)
        public static Observer<Game> createAnsiView(final Game game, final Consumer<String> consumer) {
            return new AnsiView(game, consumer);
        }
    }
}
