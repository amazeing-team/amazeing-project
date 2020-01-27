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
import amazing.controller.Command.CommandFactory;
import amazing.controller.Query.*;
import amazing.controller.Response.TaskResponse.Builder;
import amazing.controller.interpreter.Interpreter;
import amazing.model.Room;
import amazing.model.Rotation;
import amazing.model.User;
import com.google.gson.JsonParseException;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;

import static amazing.controller.Query.Type.*;

public class Server {

    private static final Logger LOGGER = LoggerFactory.getLogger(Server.class);
    private final Map<User, Game> games = new HashMap<>();
    private final Map<Integer, Room> rooms = new HashMap<>();
    private final UserManager userManager = new UserManager();
    private int lastRoomId;
    private final TournamentManager tournamentManager = new TournamentManager(Config.SECRET_KEY);
    private final boolean remote;

    public Server(final boolean remote) {
        this.remote = remote;
    }

    public UserManager getUserManager() {
        return userManager;
    }

    public int getLastRoomId() {
        return lastRoomId;
    }

    public Room getRoom(final int id) {
        return rooms.getOrDefault(id, null);
    }

    /**
     * Handle message and root each message type to the correct handler
     *
     * @param message Message from the client
     * @param conn    Websocket connection (useful when you want to send a response)
     * @return an optional string to broadcast to each client
     */
    @SuppressWarnings({
            "PMD.AvoidSynchronizedAtMethodLevel",
            "PMD.ExcessiveMethodLength"
    })
    public synchronized Optional<String> handleMessage(final String message,
                                                       @NotNull final WebSocketInterface conn) {
        try {
            final var queryType = Query.fromJson(message).getType();
            if (queryType == REGISTER) {
                final var register = Query.Register.fromJson(message);
                this.handleRegister(register, conn);
                return Optional.empty();
            }
            final var user = userManager.getUserByWebsocket(conn);
            switch (Query.fromJson(message).getType()) {
                case UPDATE_USER:
                    final var updateUser = Query.UpdateUser.fromJson(message);
                    return this.handleUpdateUser(updateUser, user);
                case INIT:
                    final var init = Query.Init.fromJson(message);
                    this.handleInit(init, user);
                    return Optional.empty();
                case SHUTDOWN:
                    if (!this.remote) {
                        Server.handleShutdown(user);
                    }
                    return Optional.empty();
                case TURN_LEFT:
                case TURN_RIGHT:
                case MOVE:
                    final var action = Query.fromJson(message);
                    this.handleAction(action, user);
                    return Optional.empty();
                case TASK:
                    final var task = Task.fromJson(message);
                    this.handleTask(task, user);
                    return Optional.empty();
                case CREATE_ROOM_MULTIPLAYER:
                    final var createRoom = CreateRoomMultiplayer.fromJson(message);
                    return this.handleCreateRoomMultiplayer(createRoom, user);
                case JOIN_ROOM_MULTIPLAYER:
                    final var joinRoom = JoinRoomMultiplayer.fromJson(message);
                    return Optional.ofNullable(handleJoinRoomMultiplayer(joinRoom, user));
                case READY_MULTIPLAYER:
                    final var readyQuery = ReadyMultiplayer.fromJson(message);
                    return handleReadyMultiplayer(readyQuery, user);
                case TURN_LEFT_MULTI:
                case TURN_RIGHT_MULTI:
                case MOVE_MULTI:
                    final var actionMulti = Query.ActionMulti.fromJson(message);
                    return this.handleActionMulti(actionMulti, user);
                case CREATE_TOURNAMENT:
                    final var createTournament = Query.CreateTournament.fromJson(message);
                    return this.handleCreateTournament(createTournament);
                case REMOVE_TOURNAMENT:
                    final var removeTournament = Query.RemoveTournament.fromJson(message);
                    return this.handleRemoveTournament(removeTournament);
                case START_TOURNAMENT:
                    final var startTournament = Query.StartTournament.fromJson(message);
                    this.handleStartTournament(startTournament);
                    return Optional.empty();
                case JOIN_TOURNAMENT:
                    final var joinTournament = Query.JoinTournament.fromJson(message);
                    this.handleJoinTournament(joinTournament, user);
                    return Optional.empty();
                case ENDROUND_TOURNAMENT:
                    final var endRoundTournament = Query.EndRoundTournament.fromJson(message);
                    this.handleEndRoundTournament(endRoundTournament);
                    return Optional.empty();
                case WATCH_TOURNAMENT:
                    final var watchTournament = Query.WatchTournament.fromJson(message);
                    this.handleWatchTournament(watchTournament, user);
                    return Optional.empty();
                case TURN_LEFT_TOURNAMENT:
                case TURN_RIGHT_TOURNAMENT:
                case MOVE_TOURNAMENT:
                    final var actionTournament = Query.ActionTournament.fromJson(message);
                    this.handleActionTournament(actionTournament, user);
                    return Optional.empty();
                default:
                    LOGGER.error("unrecognized query");
                    return Optional.empty();
            }
        } catch (final JsonParseException e) {
            // Player send an illegal json
            final var builder = new Response.ErrorBuilder(e.getMessage());
            builder.build();
            conn.send(builder.getResponse().toJson());
        }
        // should be dead code
        return Optional.empty();
    }

    private void handleWatchTournament(final Query.WatchTournament watchTournament, final User user) {
        this.tournamentManager
                .trackTournament(watchTournament.getPrivateKey(), watchTournament.getId(), user);
    }

    private void handleActionTournament(final Query.ActionTournament actionTournament,
                                        final User user) {
        this.tournamentManager.handleAction(actionTournament.getType(), user);
    }

    private void handleStartTournament(final Query.StartTournament startTournament) {
        final var result = this.tournamentManager
                .startTournament(startTournament.getPrivateKey(), startTournament.getId());
        if (result.isOk()) {
            this.sendTournamentUpdate(startTournament.getId());
        }
    }

    private void sendTournamentUpdate(final int id) {
        final var tournament = this.tournamentManager.getTournamentById(id);
        if (tournament.isEmpty()) {
            LOGGER.error("tournament is empty but could join");
            return;
        }
        final var builder = new Response.TournamentResponse.Builder(tournament.get());
        builder.build();
        final String string = builder.getResponse().toJson();
        LOGGER.trace(string);
        tournament.get().getUsers().forEach(user1 -> user1.send(string));
    }

    private void handleJoinTournament(final Query.JoinTournament joinTournament, final User user) {
        final var result = this.tournamentManager.joinTournament(joinTournament.getId(), user);
        if (result.isOk()) {
            sendTournamentUpdate(joinTournament.getId());
        }
    }

    private Optional<String> handleRemoveTournament(final Query.RemoveTournament removeTournament) {
        final var result = this.tournamentManager
                .removeTournament(removeTournament.getPrivateKey(), removeTournament.getId());
        if (result.isOk()) {
            final var builder = new Response.TournamentsResponse.Builder(this.tournamentManager);
            builder.build();
            return Optional.of(builder.getResponse().toJson());
        }
        return Optional.empty();
    }

    private Optional<String> handleCreateTournament(final Query.CreateTournament createTournament) {
        final var result = this.tournamentManager
                .createTournament(createTournament.getPrivateKey(), createTournament.getName());
        if (result.isOk()) {
            final var builder = new Response.TournamentsResponse.Builder(this.tournamentManager);
            builder.build();
            return Optional.of(builder.getResponse().toJson());
        }
        return Optional.empty();
    }

    private Optional<String> handleEndRoundTournament(
            final Query.EndRoundTournament endRoundTournament) {
        final var result = this.tournamentManager
                .endRound(endRoundTournament.getPrivateKey(), endRoundTournament.getId());
        if (result.isOk()) {
            final var builder = new Response.TournamentsResponse.Builder(this.tournamentManager);
            builder.build();
            return Optional.of(builder.getResponse().toJson());
        }
        return Optional.empty();
    }

    @NotNull
    private Optional<String> handleUpdateUser(@NotNull final Query.UpdateUser updateUser,
                                              @NotNull final User user) {
        user.setColor(updateUser.getColor());
        user.setName(updateUser.getName());
        final var builder = new Response.RoomResponse.Builder(new ArrayList<>(this.rooms.values()));
        builder.build();
        return Optional.of(builder.getResponse().toJson());
    }

    private Optional<String> handleActionMulti(final @NotNull Query.ActionMulti action,
                                               final @NotNull User user) {
        final var room = this.rooms.get(action.getRid());
        final var game = room.getGame();
        LOGGER.trace("ActionMultiplayer({}, {})", room, action.getType());
        final Result<?, Error> result;
        if (action.getType() == TURN_LEFT_MULTI) {
            result = game.turn(user, Rotation.LEFT);
        } else if (action.getType() == TURN_RIGHT_MULTI) {
            result = game.turn(user, Rotation.RIGHT);
        } else {
            result = game.move(user);
        }
        final var builder = new Response.MoveMultiplayerResponse.Builder();
        if (result.isError()) {
            Server.sendError(user, result.error().orElseThrow().toString());
        } else {
            game.getPlayers().forEach((user1, player) -> {
                builder.setFrame(game.buildFrame(user1));
                builder.setWin(game.getWinners().contains(user1));
                builder.build();
                user1.send(builder.getResponse().toJson());
            });
            if (game.getWinners().size() > 0) {
                // stop game someone won
                game.getPlayers().forEach((user1, player1) -> {
                    if (!game.getWinners().contains(user1)) {
                        final var stopBuilder = new Response.StopMultiResponse.Builder(
                                game.getPlayers().get(game.getWinners().get(0)));
                        stopBuilder.build();
                        user1.send(stopBuilder.getResponse().toJson());
                    }
                });
                this.rooms.remove(action.getRid());
                final var roomBuilder =
                        new Response.RoomResponse.Builder(new ArrayList<>(this.rooms.values()));
                roomBuilder.build();
                return Optional.of(roomBuilder.getResponse().toJson());
            }
        }
        return Optional.empty();
        // Maybe we could send the updated map for all player
    }

    /**
     * Remove user handled from the websocket connection to free some place
     *
     * @param conn Websocket connection
     * @return built response for server
     */
    String handleClose(final @NotNull WebSocketInterface conn) {
        final var user = this.userManager.getUserByWebsocket(conn);
        // filter useless rooms
        final Iterator<Map.Entry<Integer, Room>> it = this.rooms.entrySet().iterator();
        while (it.hasNext()) {
            final var entry = it.next();
            final var players = entry.getValue().getGame().getPlayers();
            if (players.containsKey(user)) {
                // remove user and if user is now empty remove room
                players.remove(user);
                if (players.isEmpty()) {
                    LOGGER.trace("Remove room {}", entry.getValue().getName());
                    it.remove();
                }
            }
        }
        // filter useless games
        this.games.remove(user);
        this.tournamentManager.removeUser(user);
        final var builder = new Response.RoomResponse.Builder(new ArrayList<>(this.rooms.values()));
        builder.build();
        return builder.getResponse().toJson();
    }

    private String handleJoinRoomMultiplayer(@NotNull final JoinRoomMultiplayer joinRoom,
                                             @NotNull final User user) {
        LOGGER.trace("JoinRoomMultiplayer({}, {})", joinRoom.getId(), user.getUid());
        if (Objects.isNull(rooms.get(joinRoom.getId())) && !rooms.get(joinRoom.getId()).isPlaying()) {
            final var builder = new Response.ErrorBuilder(
                    "No room found with id " + joinRoom.getId() + " number of room " + rooms.size()
                            + "or game is already launched");
            this.rooms.forEach((key, room) -> {
                LOGGER.trace("Room Available({}, {})", key, room.isPlaying());
            });
            builder.build();
            return builder.getResponse().toJson();
        }
        rooms.get(joinRoom.getId()).addUser(user);
        final var builder = new Response.RoomResponse.Builder(new ArrayList<>(this.rooms.values()));
        builder.build();
        return builder.getResponse().toJson();
    }

    private Optional<String> handleReadyMultiplayer(@NotNull final ReadyMultiplayer readyQuery,
                                                    final User user) {
        LOGGER.trace("ReadyMultiplayer({}, {})", readyQuery.getId(), user);
        final var room = rooms.get(readyQuery.getId());
        room.getGame().getPlayers().get(user).setReady(true);
        if (!room.isReady()) {
            final var builder = new Response.RoomResponse.Builder(new ArrayList<>(this.rooms.values()));
            builder.build();
            return Optional.of(builder.getResponse().toJson());
        } else {
            room.setPlaying(true);
            final var initialFrame = room.getFrame(user);
            final var builder = new Response.StartGameMultiplayerResponse.Builder(initialFrame);
            builder.build();
            // broacast only to player in the room
            room.getGame().getPlayers()
                    .forEach((user1, player) -> user1.send(builder.getResponse().toJson()));
            return Optional.empty();
        }
    }

    /**
     * Handle create room multiplayer event
     *
     * @param createRoom Websocket query
     * @param user
     * @return Room event to broadcast
     */
    private Optional<String> handleCreateRoomMultiplayer(
            @NotNull final CreateRoomMultiplayer createRoom, final User user) {
        LOGGER.trace("CreateRoomMultiplayer({})", createRoom.getName());
        // TODO check name is not already existing
        if (createRoom.getName().length() < 4 || createRoom.getName().length() > 100) {
            Server.sendError(user, "Room name is incorrect");
            return Optional.empty();
        }
        if (createRoom.getHeight() < 5 || createRoom.getHeight() > 200) {
            Server.sendError(user, "Room height it incorrect");
            return Optional.empty();
        }
        if (createRoom.getWidth() < 5 || createRoom.getWidth() > 200) {
            Server.sendError(user, "Room width is incorrect");
            return Optional.empty();
        }
        final var room =
                new Room(lastRoomId, createRoom.getName(), createRoom.getWidth(), createRoom.getHeight());
        rooms.put(lastRoomId, room);
        lastRoomId++;
        final var builder = new Response.RoomResponse.Builder(new ArrayList<>(this.rooms.values()));
        builder.build();
        return Optional.of(builder.getResponse().toJson());
    }

    /**
     * Received when running on playground mode
     *
     * @param action Action send by the user
     * @param user   User who send the query
     */
    private void handleAction(final @NotNull Query action, final User user) {
        LOGGER.trace("Action({}, {})", user, action.getType());
        final Game game = games.get(user);
        assert (game != null);
        final CommandFactory factory = new CommandFactory(user);
        final Command command;
        if (action.getType() == TURN_LEFT) {
            command = factory.createTurnLeft();
        } else if (action.getType() == TURN_RIGHT) {
            command = factory.createTurnRight();
        } else {
            command = factory.createMove();
        }
        final Result<?, Error> result = game.run(command);
        game.setCurrentGameAsInitial();
        final var builder = new Response.MapUpdateResponse.Builder();
        if (result.isError()) {
            builder.setError(result.error().orElseThrow().toString());
        } else {
            builder.setWin(game.getWinners().contains(user));
        }
        builder.setFrame(game.buildFrame(user));
        builder.build();
        user.send(builder.getResponse().toJson());
    }

    /**
     * Received a list of instruction from client
     *
     * <p>Deserialize task, Check if game for user exist, Run interpreter on instructions (generate
     * frames), Send each frames to the client back
     *
     * @param task Task send by the user
     * @param user User who send the action
     */
    private void handleTask(final @NotNull Task task, final User user) {
        LOGGER.trace("Task({}, {})", user, task.getInstructions());
        final Builder builder = new Builder(task.getID());
        final Game game = games.get(user);
        if (game == null) {
            LOGGER.trace("No game for user {}. Contain key {}, List game available:", user.getUid(),
                    games.containsKey(user));
            games.forEach((k, v) -> LOGGER.trace("game for user {}", k));
            builder.setError("No game for player with username " + user.getName());
            builder.build();
            user.send(builder.getResponse().toJson());
            return;
        }
        game.reset();
        final Interpreter interpreter = new Interpreter(game, user, task.getInstructions(), builder);
        final Result<?, Error> result = interpreter.init();
        if (result.isError()) {
            builder.setError(result.error().orElseThrow().toString());
        } else {
            interpreter.run();
        }
        builder.build();
        user.send(builder.getResponse().toJson());
        game.reset();
    }

    /**
     * Send by the client when disconnecting * The server should not expect every client to disconnect
     * itself TODO take a look at onClose in the Websocket class
     */
    private static void handleShutdown(final User user) {
        LOGGER.trace("Shutdown({})", user);
        final var builder = new Response.ShutdownResponse.Builder();
        builder.build();
        user.send(builder.getResponse().toJson());
    }

    /**
     * Handle register event send by client Send to the client an unique id
     *
     * @param register Websocket query
     * @param conn     Websocket connection
     */
    private void handleRegister(final @NotNull Register register,
                                @NotNull final WebSocketInterface conn) {
        LOGGER.trace("Register({})", register.getName());
        final User user = userManager.registerUser(conn, register.getName(), register.getColor());
        final var builder = new Response.RegisterResponse.Builder(user.getUid());
        this.games.put(user, null);
        builder.build();
        conn.send(builder.getResponse().toJson());
        final var roomBuilder = new Response.RoomResponse.Builder(new ArrayList<>(this.rooms.values()));
        roomBuilder.build();
        conn.send(roomBuilder.getResponse().toString());
        final var tournamentBuilder = new Response.TournamentsResponse.Builder(this.tournamentManager);
        tournamentBuilder.build();
        conn.send(tournamentBuilder.getResponse().toString());
    }

    /**
     * Send by the client when choosing a task or using the playground The client should send the map
     * to use The map is send
     *
     * @param init Serialized INIT message
     * @param user User who send the task
     */
    private void handleInit(final @NotNull Init init, final User user) {
        LOGGER.trace("Init({})", init.getMaze());
        final Game game = new Game(init.getMaze());
        games.replace(user, game);
        LOGGER.trace("Add game {} for user {}", game, user);
        final Result<?, Error> result = game.addPlayer(user, init.getDirection(), init.getHealth());
        if (result.isError()) {
            LOGGER.error(result.error().orElseThrow().toString());
            final Response.ErrorBuilder builder =
                    new Response.ErrorBuilder(result.error().orElseThrow().toString());
            builder.build();
            user.send(builder.getResponse().toJson());
            return;
        }
        final Response.InitResponse.Builder builder = new Response.InitResponse.Builder();
        builder.addFrame(game.buildFrame(user));
        builder.build();
        user.send(builder.getResponse().toJson());
        LOGGER.trace("return init({})", builder.getResponse().toJson());
    }

    private static void sendError(@NotNull final User user, final String msg) {
        final Response.ErrorBuilder builder = new Response.ErrorBuilder(msg);
        builder.build();
        user.send(builder.getResponse().toJson());
    }
}

