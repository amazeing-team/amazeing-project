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

import amazing.Config;
import amazing.controller.Error.ErrorFactory;
import amazing.controller.Game.Frame.FrameBuilder;
import amazing.controller.Item.ItemFactory;
import amazing.controller.Result.ResultFactory;
import amazing.model.*;
import amazing.model.Marker.Tile;
import amazing.model.Tuple.Pair.Boundary;
import amazing.model.Tuple.Pair.Coordinate;
import amazing.view.Observer;
import amazing.view.Observer.ObserverFactory;
import com.google.gson.annotations.Expose;
import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;
import java.util.Map.Entry;
import java.util.function.Predicate;
import java.util.stream.Collectors;

import static amazing.Config.*;

/**
 * Class that manages a maze route-finding game.
 */
public class Game extends SerializableJson {

    private static final Logger LOGGER = LoggerFactory.getLogger(Game.class);
    @Expose
    private final Maze maze;
    private final List<Observer<Game>> observers;
    @Expose
    private final Map<User, Player> players;
    private final Random random;
    private final Predicate<Player> win =
            player -> Objects.nonNull(player) && this.getMaze().isExit(player.getCoordinate())
                    && player.getHealth() > 0;
    private final Predicate<Player> gameOver = this.win.or(player -> player.getHealth() <= 0);
    private final boolean spawnItems;
    @Expose
    private int runs;

    /**
     * Class that manages a game, both single- and multiplayer.
     *
     * @param maze       maze in which the game will take place
     * @param spawnItems true if items should be used in maze, otherwise false
     */
    @Contract(pure = true)
    public Game(final Maze maze, final boolean spawnItems) {
        this.maze = maze;
        this.players = new HashMap<>();
        this.runs = 0;
        this.random = new Random(INITIAL_SEED);
        this.spawnItems = spawnItems;
        this.observers = new ArrayList<>();
        this.addObserver(ObserverFactory.createAnsiView(this, Game.LOGGER::trace));
    }

    @Contract(pure = true)
    public Game(final Maze maze) {
        this(maze, false);
    }

    public void reset() {
        LOGGER.trace("reset");
        this.players.forEach((user, player) -> player.reset());
        runs = 0;
        for (final Entry<Coordinate, Tile> entry : maze) {
            entry.getValue().reset();
        }
    }

    void setCurrentGameAsInitial() {
        LOGGER.trace("setCurrentGameAsInitial");
        this.players.forEach((key, val) -> val.setCurrentAsInitial());
    }

    private void addObserver(@NotNull final Observer<Game> observer) {
        this.observers.add(observer);
        observer.update();
    }

    /**
     * Class that represents one frame, to be shown in the browser.
     */
    public static class Frame extends Maze {

        @Expose
        @SuppressFBWarnings("URF_UNREAD_FIELD")
        Map<Integer, Player> players;
        @Expose
        @SuppressFBWarnings("URF_UNREAD_FIELD")
        Queue<String> log;

        Frame(final Boundary boundary, final Coordinate start, final Set<Coordinate> exits,
              final Map<Integer, Player> players, final Queue<String> log) {
            super(boundary, start, exits);
            this.players = players;
            this.log = log;
        }

        static class FrameBuilder {

            private final User user;
            private final Game game;
            private Frame frame;
            private final Queue<String> log;

            FrameBuilder(final @NotNull User user, final @NotNull Game game, final Queue<String> log) {
                this.user = user;
                this.game = game;
                if (log != null) {
                    this.log = new ArrayDeque<>(log);
                } else {
                    this.log = new ArrayDeque<>();
                }
            }

            Result<Frame, ?> build() {
                final Player player = this.game.getPlayers().get(this.user);
                if (player == null) {
                    final StringBuilder string = new StringBuilder(" present ");
                    for (final var player1 : this.game.getPlayers().keySet()) {
                        string.append(player1.getUid()).append(", ");
                    }
                    throw new IllegalStateException("Player don't exist for user " + user.getUid() + string);
                }
                final Coordinate center = player.getCoordinate();
                final int visibility =
                        player.hasGoggles() ? this.game.getMaze().getBoundary().apply(Coordinate::manhattan) :
                                this.game.getPlayers().get(this.user).getVisibleDistance();
                final Map<Integer, Player> players = new HashMap<>(this.game.getPlayers().size(), 1.0f);
                this.game.getPlayers().entrySet().stream()
                        .filter(entry -> entry.getValue().getCoordinate().manhattan(center) <= visibility)
                        .forEach(entry -> players.put(entry.getKey().getUid(), entry.getValue().clone()));
                final Maze maze = game.getMaze();
                this.frame = new Frame(maze.getBoundary(), maze.getStart().clone(),
                        Collections.unmodifiableSet(maze.getExits()), players, this.log);
                for (final Coordinate coordinate : this.game.getPlayers().get(this.user).getCoordinate()
                        .range(visibility)) {
                    this.game.getMaze().getTile(coordinate)
                            .ifPresent(tile -> this.frame.addTile(coordinate, tile.clone()));
                }
                return ResultFactory.createOk(this.frame);
            }
        }
    }

    /**
     * Builds a Frame to be sent to the client.
     *
     * @param user the user who received the Frame
     * @param log  log items to be displayed
     * @return built frame
     */
    public Frame buildFrame(final @NotNull User user, final Queue<String> log) {
        final FrameBuilder builder = new FrameBuilder(user, this, log);
        return builder.build().ok().orElseThrow();
    }

    /**
     * Builds a Frame to be sent to the client.
     *
     * @param user the user who received the Frame
     * @return built frame
     */
    public Frame buildFrame(final @NotNull User user) {
        final FrameBuilder builder = new FrameBuilder(user, this, null);
        return builder.build().ok().orElseThrow();
    }

    /**
     * Adds a user to the current game.
     *
     * @param user the user to be added
     * @return an error if the user is already in the game else ok
     */
    public Result<?, Error> addPlayer(final User user) {
        return addPlayer(user, Config.INITIAL_DIRECTION, Config.INITIAL_HEALTH);
    }

    public Result<?, Error> addPlayer(final User user, final Direction direction,
                                      final Integer health) {
        if (this.players.containsKey(user)) {
            return ResultFactory.createError(ErrorFactory.createKeyError(user));
        }
        this.players.put(user,
                new Player(this.maze.getStart(), direction, health, user.getUid(), user.getColor(),
                        new HashMap<>()));
        return ResultFactory.createOk();
    }

    Result<?, Error> damage(final User user, final Error error) {
        if (!this.players.containsKey(user)) {
            return ResultFactory.createError(ErrorFactory.createKeyError(user));
        }
        this.players.get(user).computeHealth(health -> health - 1);
        return ResultFactory.createError(error);
    }

    public List<User> getLosers() {
        return this.players.entrySet().stream().filter(i -> this.win.negate().test(i.getValue()))
                .map(Entry::getKey).sorted(Comparator.reverseOrder()).collect(Collectors.toList());
    }

    public Maze getMaze() {
        return this.maze;
    }

    public Map<User, Player> getPlayers() {
        return this.players;
    }

    public List<User> getWinners() {
        return this.players.entrySet().stream().filter(i -> this.win.test(i.getValue()))
                .map(Entry::getKey).sorted(Comparator.reverseOrder()).collect(Collectors.toList());
    }

    public boolean isGameOver(final User user) {
        return this.gameOver.test(this.players.get(user));
    }

    Result<?, Error> move(final User user) {
        if (!this.players.containsKey(user)) {
            return ResultFactory.createError(ErrorFactory.createKeyError(user));
        }
        final Player player = this.players.get(user);
        final Optional<Tile> tile = this.maze.getTile(player.getCoordinate());
        if (tile.isEmpty()) {
            return ResultFactory.createError(ErrorFactory.createMissingValue(player.getCoordinate()));
        }
        if (tile.get().hasDirection(player.getDirection())) {
            player.wearOff(); // Items wear off when moving
            player.computeCoordinate(coordinate -> coordinate.getCoordinate(player.getDirection()));
            final Optional<Tile> newTile = this.maze.getTile(player.getCoordinate());
            assert newTile.isPresent(); //Player was just moved to this new tile, so the Tile has to exist
            if (newTile.get().hasItem()) {
                return newTile.get().popItem().execute(this, player);
            }
            if (this.spawnItems && random.nextDouble() >= ITEM_DROP_PROP) { //No Items in Singleplayer
                dropItem();
            }
            return ResultFactory.createOk();
        }
        return this.damage(user, ErrorFactory.createWrongPlayerMove(user, player.getDirection()));
    }

    public Result<Optional<Marker>, Error> getMarker(final User user, final Coordinate coordinate) {
        if (!this.players.containsKey(user)) {
            return ResultFactory.createError(ErrorFactory.createKeyError(user));
        }
        return ResultFactory.createOk(players.get(user).getMarker(coordinate));
    }

    public Result<?, Error> addMarker(final User user, final Coordinate coordinate,
                                      final Marker marker) {
        if (!this.players.containsKey(user)) {
            return ResultFactory.createError(ErrorFactory.createKeyError(user));
        }
        players.get(user).addMarker(coordinate, marker);
        return ResultFactory.createOk();
    }

    public Result<?, Error> removeMarker(final User user, final Coordinate coordinate) {
        if (!this.players.containsKey(user)) {
            return ResultFactory.createError(ErrorFactory.createKeyError(user));
        }
        players.get(user).removeMarker(coordinate);
        return ResultFactory.createOk();
    }

    /**
     * Executes commands.
     *
     * @param commands the commands to be executed
     * @return success or an error, if a command fails
     */
    public Result<?, Error> run(@NotNull final Command... commands) {
        if (commands.length > MAX_STACK || runs > MAX_STACK) {
            return ResultFactory.createError(ErrorFactory.createStackError());
        }
        for (final Command command : commands) {
            Game.LOGGER.trace("{}", command);
            final Result<?, Error> result = command.execute(this);
            if (result.error().isPresent()) {
                return result;
            }
            this.observers.forEach(Observer::update);
        }
        this.runs++;
        return ResultFactory.createOk();
    }

    Result<?, Error> teleport(final Player player) {
        final Set<Coordinate> deadEndTiles =
                maze.getTiles().entrySet().stream().filter(en -> en.getValue().getDirections().isEmpty())
                        .map(Entry::getKey).collect(Collectors.toSet());
        final List<Coordinate> coordinates = this.maze.getTiles().keySet().stream()
                .filter(coordinate -> !(deadEndTiles.contains(coordinate)))
                .sorted(Comparator.reverseOrder()).collect(Collectors.toList());
        player
                .computeCoordinate(coordinate -> coordinates.get(this.random.nextInt(coordinates.size())));
        return ResultFactory.createOk();
    }

    Result<?, Error> turn(final User user, final Rotation rotation) {
        if (!this.players.containsKey(user)) {
            return ResultFactory.createError(ErrorFactory.createKeyError(user));
        }
        this.players.get(user).computeDirection(direction -> direction.rotate(rotation));
        return ResultFactory.createOk();
    }

    private void dropItem() {
        final Set<Coordinate> occupiedTiles =
                players.values().stream().map(player -> player.coordinate).collect(Collectors.toSet());
        final Set<Coordinate> deadEndTiles =
                maze.getTiles().entrySet().stream().filter(en -> en.getValue().getDirections().isEmpty())
                        .map(Entry::getKey).collect(Collectors.toSet());
        final List<Coordinate> coordinates = this.maze.getTiles().keySet().stream().filter(
                coordinate -> !(occupiedTiles.contains(coordinate)) || !deadEndTiles.contains(coordinate))
                .sorted(Comparator.reverseOrder()).collect(Collectors.toList());
        if (!(coordinates.isEmpty())) {
            this.maze.getTile(coordinates.get(this.random.nextInt(coordinates.size()))).orElseThrow()
                    .dropItem(ItemFactory.createRandomItem(random));
        }
    }

    public Result<?, Error> handleAction(final User user, final Action action) {
        if (!getPlayers().containsKey(user)) {
            return ResultFactory.createError(ErrorFactory.createKeyError(user));
        }
        switch (action) {
            case MOVE:
                this.move(user);
                break;
            case TURN_LEFT:
                this.turn(user, Rotation.LEFT);
                break;
            case TURN_RIGHT:
                this.turn(user, Rotation.RIGHT);
                break;
            default:
                break;
        }
        return ResultFactory.createOk();
    }

    public enum Action {
        MOVE,
        TURN_LEFT,
        TURN_RIGHT
    }
}

