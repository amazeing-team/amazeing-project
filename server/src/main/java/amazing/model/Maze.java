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

import amazing.controller.Error;
import amazing.controller.Error.ErrorFactory;
import amazing.controller.Result;
import amazing.controller.Result.ResultFactory;
import amazing.model.Tuple.Pair.Boundary;
import amazing.model.Tuple.Pair.Coordinate;
import com.google.common.collect.Iterators;
import com.google.gson.JsonSyntaxException;
import com.google.gson.annotations.Expose;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;
import java.util.Map.Entry;
import java.util.stream.Collectors;

import static amazing.Config.UNICODE;

/**
 * Class representing a Maze.
 */
public class Maze extends SerializableJson implements Iterable<Entry<Coordinate, Marker.Tile>> {

    private static final Logger LOGGER = LoggerFactory.getLogger(Maze.class);
    @Expose
    private final Boundary boundary;
    @Expose
    private Set<Coordinate> exits;
    @Expose
    private Coordinate start;
    @Expose
    private final Map<Coordinate, Marker.Tile> tiles;

    /**
     * Creates an empty maze.
     *
     * @param boundary the measurements of the maze
     */
    public Maze(@NotNull final Boundary boundary) {
        this.boundary = boundary;
        this.tiles = new HashMap<>(Iterators.size(this.boundary.iterator()), 1.0f);
        this.start = null;
        this.exits = null;
    }

    public Maze(@NotNull final Boundary boundary, final Coordinate start,
                final Set<Coordinate> exits) {
        this.boundary = boundary;
        this.tiles = new HashMap<>(Iterators.size(this.boundary.iterator()), 1.0f);
        this.start = start;
        this.exits = exits;
    }

    /**
     * Adds a tile to the maze.
     *
     * @param coordinate the place where the tile should be added
     * @param tile       the tile to be added
     * @return ok or an error on failure
     */
    public Result<?, Error> addTile(final Coordinate coordinate, final Marker.Tile tile) {
        Maze.LOGGER.trace("addTile({}, {})", coordinate, tile);
        if (this.tiles.containsKey(coordinate) || !coordinate.checkBoundaries(this.boundary)) {
            return ResultFactory.createError(ErrorFactory.createKeyError(coordinate));
        }
        this.tiles.put(coordinate, tile);
        return ResultFactory.createOk();
    }

    /**
     * Replaces a tile in the maze with a new, given one.
     *
     * @param coordinate the coordinate at which the replacement should happen
     * @param tile       the new tile
     * @return ok or an error if there was no tile at the coordinate in the maze
     */
    protected Result<?, Error> replaceTile(final Coordinate coordinate, final Marker.Tile tile) {
        Maze.LOGGER.trace("replaceTile({}, {})", coordinate, tile);
        if (!this.tiles.containsKey(coordinate) || !coordinate.checkBoundaries(this.boundary)) {
            return ResultFactory.createError(ErrorFactory.createKeyError(coordinate));
        }
        this.tiles.replace(coordinate, tile);
        return ResultFactory.createOk();
    }

    public Boundary getBoundary() {
        return this.boundary;
    }

    public Set<Coordinate> getExits() {
        return this.exits;
    }

    protected void setExits(final Set<Coordinate> exits) {
        this.exits = exits;
    }

    protected void setStart(final Coordinate start) {
        this.start = start;
    }

    public Coordinate getStart() {
        return this.start;
    }

    public Optional<Marker.Tile> getTile(final Coordinate coordinate) {
        return Optional.ofNullable(this.tiles.getOrDefault(coordinate, null));
    }

    @Contract(pure = true)
    public Map<Coordinate, Marker.Tile> getTiles() {
        return this.tiles;
    }

    public boolean isExit(@NotNull final Coordinate coordinate) {
        return this.exits.contains(coordinate);
    }

    public boolean isStart(@NotNull final Coordinate coordinate) {
        return coordinate.equals(this.start);
    }

    @NotNull
    @Override
    public Iterator<Entry<Coordinate, Marker.Tile>> iterator() {
        return this.tiles.entrySet().iterator();
    }

    /**
     * Builder to build mazes from JSON formatted strings.
     */
    public static class Builder {

        private final String source;
        boolean json;
        Coordinate start;
        Set<Coordinate> exits;

        /**
         * Creates the builder.
         *
         * @param source the JSON string
         */
        @Contract(pure = true)
        public Builder(final String source) {
            this.source = source;
            this.json = true;
        }

        /**
         * Creates a builder to build a maze from JSON.
         *
         * @param source the JSON string
         * @param start  the coordinate where players start the maze
         * @param exits  a set of exits where the player can finish
         */
        @Contract(pure = true)
        public Builder(final String source, @NotNull final Coordinate start,
                       final Set<Coordinate> exits) {
            this.source = source;
            this.start = start;
            this.exits = exits;
            this.json = false;
        }

        /**
         * Checks validity of a maze e.g. connectivity, starts, exits.
         *
         * @param maze the maze to be checked
         * @return an error or ok
         */
        public static Result<Maze, Error> check(@NotNull final Maze maze) {
            if (Objects.isNull(maze.getExits()) || maze.getExits().isEmpty() || Objects
                    .isNull(maze.getStart())) {
                return ResultFactory.createError(ErrorFactory.createJsonError());
            }
            for (final Coordinate coordinate : maze.getBoundary()) {
                if (maze.getTile(coordinate).isEmpty()) {
                    return ResultFactory.createError(ErrorFactory.createMissingValue(coordinate));
                }
            }
            for (final Entry<Coordinate, Marker.Tile> entry : maze) {
                for (final Direction direction : entry.getValue().getDirections()) {
                    final Optional<Marker.Tile> tile = maze.getTile(entry.getKey().getCoordinate(direction));
                    if (tile.isEmpty()) {
                        return ResultFactory
                                .createError(ErrorFactory.createKeyError(entry.getKey().getCoordinate(direction)));
                    }
                    if (!tile.get().hasDirection(direction.mirror())) {
                        return ResultFactory.createError(ErrorFactory.createNotFullyConnected());
                    }
                }
            }
            return Builder.checkConnectivity(maze);
        }

        /**
         * Checks whether all tiles are reachable (uses DFS).
         *
         * @param maze the maze to check
         * @return ok or an error if there are unreachable tiles
         */
        static Result<Maze, Error> checkConnectivity(@NotNull final Maze maze) {
            final Set<Coordinate> explored = new HashSet<>();
            final Queue<Coordinate> frontier = new ArrayDeque<>();
            Coordinate coordinate = maze.getStart();
            do {
                explored.add(coordinate);
                final Optional<Marker.Tile> tile = maze.getTile(coordinate);
                if (tile.isEmpty()) {
                    return ResultFactory.createError(ErrorFactory.createMissingValue(coordinate));
                }
                for (final Direction direction : tile.get().getDirections()) {
                    final Coordinate neighbour = coordinate.getCoordinate(direction);
                    if (!explored.contains(neighbour) && !frontier.contains(neighbour)) {
                        frontier.add(neighbour);
                    }
                }
                coordinate = frontier.poll();
            } while (Objects.nonNull(coordinate));
            if (!explored.containsAll(maze.getExits())) {
                return ResultFactory.createError(ErrorFactory.createExitNotReachable());
            }
            if (!explored.containsAll(maze.getTiles().entrySet().stream()
                    .filter(entry -> !entry.getValue().getDirections().isEmpty()).map(Entry::getKey)
                    .collect(Collectors.toSet()))) {
                return ResultFactory.createError(ErrorFactory.createNotFullyConnected());
            }
            return ResultFactory.createOk(maze);
        }

        /**
         * Builds a maze after checking the source.
         *
         * @return the maze or an error
         */
        public Result<Maze, Error> build() {
            if (this.json) {
                try {
                    return Builder.check(Maze.gson().fromJson(this.source, Maze.class));
                } catch (final IndexOutOfBoundsException | NumberFormatException
                        | JsonSyntaxException ignored) {
                    return ResultFactory.createError(ErrorFactory.createJsonError());
                }
            } else {
                final char[][] tiles =
                        Arrays.stream(this.source.split("\n")).map(String::toCharArray).toArray(char[][]::new);
                if (tiles.length == 0
                        || Arrays.stream(tiles).map(arr -> arr.length).min(Integer::compareTo).orElseThrow()
                        == 0) {
                    return ResultFactory.createError(ErrorFactory.createUnexpectedError());
                }
                final Boundary boundary = new Boundary(new Coordinate(0, 0), new Coordinate(
                        Arrays.stream(tiles).map(arr -> arr.length).max(Integer::compareTo).orElseThrow() - 1,
                        tiles.length - 1));
                final Maze maze = new Maze(boundary, this.start, this.exits);
                for (final Coordinate coordinate : boundary) {
                    final char key = tiles[coordinate.getSecond()][coordinate.getFirst()];
                    final var directions = UNICODE.get(key);
                    if (Objects.nonNull(directions)) {
                        maze.addTile(coordinate.clone(), new Marker.Tile(directions));
                    } else {
                        return ResultFactory.createError(ErrorFactory.createKeyError(key));
                    }
                }
                return Builder.check(maze);
            }
        }
    }
}
