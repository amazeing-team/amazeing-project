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

import amazing.controller.Result.ResultFactory;
import amazing.model.Direction;
import amazing.model.Marker.Tile;
import amazing.model.Maze;
import amazing.model.Tuple.Pair.Boundary;
import amazing.model.Tuple.Pair.Coordinate;
import org.jetbrains.annotations.NotNull;

import java.util.*;
import java.util.function.Function;

public final class Generator extends Maze {

    //  private static final Logger LOGGER = LoggerFactory.getLogger(Generator.class);
    private final Random random;
    private final Function<Integer, Double> factor;

    public Generator(final int width, final int height, final long seed,
                     final Function<Integer, Double> factor) {
        super(new Boundary(new Coordinate(0, 0), new Coordinate(width - 1, height - 1)));
        this.random = new Random(seed);
        this.factor = factor;
    }

    private void populate() {
        this.setStart(new Coordinate(this.random.nextInt(this.getBoundary().getSecond().getFirst()),
                this.random.nextInt(this.getBoundary().getSecond().getSecond())));
        this.setExits(Set.of(
                new Coordinate(this.random.nextInt(this.getBoundary().getSecond().getFirst()),
                        this.random.nextInt(this.getBoundary().getSecond().getSecond()))));
        for (final Coordinate coordinate : this.getBoundary()) {
            this.addTile(coordinate,
                    new Tile(Direction.NORTH, Direction.SOUTH, Direction.EAST, Direction.WEST));
        }
        for (int i = 0; i <= this.getBoundary().getSecond().getFirst(); i++) {
            {
                final Coordinate coordinate = new Coordinate(i, 0);
                final Set<Direction> directions =
                        new HashSet<>(this.getTile(coordinate).orElseThrow().getDirections());
                directions.remove(Direction.NORTH);
                this.replaceTile(coordinate, new Tile(directions));
            }
            {
                final Coordinate coordinate = new Coordinate(i, this.getBoundary().getSecond().getSecond());
                final Set<Direction> directions =
                        new HashSet<>(this.getTile(coordinate).orElseThrow().getDirections());
                directions.remove(Direction.SOUTH);
                this.replaceTile(coordinate, new Tile(directions));
            }
        }
        for (int i = 0; i <= this.getBoundary().getSecond().getSecond(); i++) {
            {
                final Coordinate coordinate = new Coordinate(0, i);
                final Set<Direction> directions =
                        new HashSet<>(this.getTile(coordinate).orElseThrow().getDirections());
                directions.remove(Direction.WEST);
                this.replaceTile(coordinate, new Tile(directions));
            }
            {
                final Coordinate coordinate = new Coordinate(this.getBoundary().getSecond().getFirst(), i);
                final Set<Direction> directions =
                        new HashSet<>(this.getTile(coordinate).orElseThrow().getDirections());
                directions.remove(Direction.EAST);
                this.replaceTile(coordinate, new Tile(directions));
            }
        }
    }

    public @NotNull Result<Maze, ?> generate() {
        this.populate();
        this.thin();
        return ResultFactory.createOk(this);
    }

    private void thin() {
        for (int i = 1; i < this.getBoundary().getSecond().apply((a, b) -> this.factor.apply(a * b));
             i++) {
            final Coordinate coordinate =
                    new Coordinate(this.random.nextInt(this.getBoundary().getSecond().getFirst()),
                            this.random.nextInt(this.getBoundary().getSecond().getSecond()));
            final Tile tile = this.getTile(coordinate).orElseThrow();
            final List<Direction> choices = new ArrayList<>(tile.getDirections());
            if (choices.size() <= 1) {
                continue;
            }
            final Direction direction = choices.get(this.random.nextInt(choices.size()));
            final Tile neighbour = this.getTile(coordinate.getCoordinate(direction)).orElseThrow();
            {
                final Set<Direction> directions = new HashSet<>(tile.getDirections());
                directions.remove(direction);
                this.replaceTile(coordinate, new Tile(directions));
            }
            {
                final Set<Direction> directions = new HashSet<>(neighbour.getDirections());
                directions.remove(direction.mirror());
                this.replaceTile(coordinate.getCoordinate(direction), new Tile(directions));
            }
            if (Maze.Builder.check(this).isError()) {
                this.replaceTile(coordinate, tile);
                this.replaceTile(coordinate.getCoordinate(direction), neighbour);
            }
        }
    }
    //  public static void main(final String[] args) {
    //    final Generator generator = new Generator(10, 10, 42, Math::sqrt);
    //    final Maze maze = generator.generate().ok().orElseThrow();
    //    ObserverFactory.createAnsiView(new Game(maze), Generator.LOGGER::info).update();
    //    Generator.LOGGER.info("\n{}", maze.toJson());
    //    Generator.LOGGER.info("check: {}", Maze.Builder.check(maze).isOk());
    //  }
}

