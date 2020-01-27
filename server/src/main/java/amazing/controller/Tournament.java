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

import amazing.model.Room;
import amazing.model.SerializableJson;
import amazing.model.User;
import com.google.gson.annotations.Expose;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * This class represents a tournament
 */
public class Tournament extends SerializableJson {

    private static final Logger LOGGER = LoggerFactory.getLogger(Tournament.class);
    private static final int MAZE_HEIGHT = 20;
    private static final int MAZE_WIDTH = 20;
    private static final int MAX_PLAYER_PER_ROOM = 4;
    @Expose
    private final int id;
    @Expose
    private final String name;
    private final List<User> watchers;
    @Expose
    @SuppressWarnings("PMD.UnusedPrivateField")
    private boolean hasEnded;
    private List<Room> rooms;
    @Expose
    private int round;
    @Expose
    private boolean started;
    @Expose
    private List<User> users;
    @Expose
    @SuppressWarnings("PMD.UnusedPrivateField")
    private User winner;

    Tournament(final int id, final @NotNull String name) {
        this.id = id;
        this.name = name;
        this.users = new ArrayList<>();
        this.rooms = new ArrayList<>();
        this.watchers = new ArrayList<>();
        this.started = false;
        this.round = 0;
    }

    void addUser(final User user) {
        this.users.add(user);
    }

    void addWatcher(final User user) {
        this.watchers.add(user);
    }

    void endRound() {
        if (this.rooms.size() == 1) {
            //endTournament
            this.hasEnded = true;
            this.winner = this.rooms.get(0).getGame().getWinners().get(0);
            return;
        }
        this.round++;
        this.users = this.rooms.stream().map(room -> room.getGame().getWinners()).flatMap(List::stream)
                .collect(Collectors.toList());
        this.rooms = new ArrayList<>();
        this.start();
    }

    /**
     * Find room currently used by user
     *
     * @param user the user we are searching his room
     * @return a room iff the user is currently playing in the room
     */
    private Optional<Room> findRoomForUser(final User user) {
        return this.rooms.stream().filter(room -> room.containsUser(user)).findFirst();
    }

    public int getId() {
        return this.id;
    }

    List<User> getUsers() {
        return this.users;
    }

    List<User> getWatcher() {
        return this.watchers;
    }

    /**
     * Handle move action for player
     *
     * @param user   The user we want to move
     * @param action The action executed by the user (move, turn left or turn right)
     * @return the room that was updated
     */
    Result<Room, Error> handleAction(final User user, final Game.Action action) {
        final var room = this.findRoomForUser(user);
        if (room.isEmpty()) {
            return Result.ResultFactory.createError(Error.ErrorFactory.createKeyError(user));
        }
        final var actionResult = room.get().handleAction(user, action);
        if (actionResult.isOk()) {
            final var game = room.get().getGame();
            final var isAlive = game.getPlayers().get(user).getHealth() > 0;
            final var builder = new Response.MoveMultiplayerResponse.Builder();
            if (!isAlive) {
                final var stopBuilder = new Response.StopMultiResponse.Builder(null);
                stopBuilder.build();
                user.send(stopBuilder.getResponse().toJson());
                // kick player
                game.getPlayers().remove(user);
                this.users.remove(user);
                this.notifyWatcher();
                if (game.getPlayers().isEmpty()) {
                    // nobody won
                    this.rooms.remove(room.get());
                }
            }
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
                        // send notification that the player loosed
                        final var stopBuilder = new Response.StopMultiResponse.Builder(
                                game.getPlayers().get(game.getWinners().get(0)));
                        stopBuilder.build();
                        user1.send(stopBuilder.getResponse().toJson());
                        // kick player
                        this.users.remove(user1);
                    }
                });
                final var tournamentBuilder = new Response.TournamentResponse.Builder(this);
                tournamentBuilder.build();
                final var string = tournamentBuilder.getResponse().toJson();
                game.getWinners().forEach(winner -> winner.send(string));
                this.notifyWatcher();
                // remove now useless room
                this.rooms.remove(room.get());
            }
            if (this.rooms.isEmpty()) {
                this.nextRound();
            }
            return Result.ResultFactory.createOk(room.get());
        }
        return Result.ResultFactory.createError(actionResult.error().get());
    }

    boolean isStarted() {
        return this.started;
    }

    /**
     * Join available room and if there is no available room create a new room
     *
     * @param user the user who participate in the tournament
     */
    private void joinAvailableRoom(final User user) {
        final boolean[] foundRoom = {false};
        this.rooms.forEach(room -> {
            if (room.getGame().getPlayers().size() < MAX_PLAYER_PER_ROOM) {
                room.addUser(user);
                final var player = room.getGame().getPlayers().get(user);
                player.setReady(true);
                foundRoom[0] = true;
            }
        });
        LOGGER.trace("create new roomre r e {}", foundRoom[0]);
        if (!foundRoom[0]) {
            LOGGER.trace("create new room");
            // no room available
            // create new room and put user in it
            this.rooms.add(new Room(this.rooms.size(),
                    "Tournament: " + this.name + ", Round: " + this.round + ", Room: " + this.rooms.size(),
                    MAZE_WIDTH, MAZE_HEIGHT));
            final var room = this.rooms.get(this.rooms.size() - 1);
            room.addUser(user);
            final var player = room.getGame().getPlayers().get(user);
            player.setReady(true);
        }
    }

    private void nextRound() {
        if (this.users.size() > 1) {
            // Next round
            this.start();
        }
        // we have a winner :D
    }

    void notifyWatcher() {
        LOGGER.trace("notifyWatcher");
        final var tournamentBuilder = new Response.TournamentResponse.Builder(this);
        tournamentBuilder.build();
        final String string = tournamentBuilder.getResponse().toJson();
        this.watchers.forEach(user1 -> user1.send(string));
    }

    /**
     * Assign player to rooms and start tournament
     */
    public void start() {
        if (this.users.size() == 1) {
            this.users.get(0).send(new Response.TournamentWinResponse("You won").toJson());
            return;
        }

        this.users.forEach(this::joinAvailableRoom);
        this.rooms.forEach(room -> room.setPlaying(true));
        this.started = true;
        this.round++;
        LOGGER.trace("Number room = {}", this.rooms.size());
        this.rooms.forEach(room -> {
            room.setPlaying(true);
            final var roomBuilder = new Response.RoomResponse.Builder(this.rooms);
            roomBuilder.build();
            final String roomMsg = roomBuilder.getResponse().toJson();
            final var tournamentRoomBuilder = new Response.TournamentRoomResponse.Builder(room.getId());
            tournamentRoomBuilder.build();
            final String tournamentRoomMsg = tournamentRoomBuilder.getResponse().toJson();
            room.getGame().getPlayers().forEach(((user, player) -> {
                user.send(roomMsg);
                user.send(tournamentRoomMsg);
                final var initialFrame = room.getFrame(user);
                final var builder = new Response.StartGameMultiplayerResponse.Builder(initialFrame);
                builder.build();
                user.send(builder.getResponse().toJson());
            }));
        });
        this.notifyWatcher();
    }
}
