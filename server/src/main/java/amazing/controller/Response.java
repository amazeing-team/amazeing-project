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
import com.google.gson.annotations.Expose;

import java.util.ArrayDeque;
import java.util.List;
import java.util.Queue;

import static amazing.controller.Query.Type.*;

/**
 * Class used to serialize response to send to the client
 */
public class Response extends Query {

    @Expose
    private final String msg;
    @Expose
    @SuppressWarnings("PMD.UnusedPrivateField")
    private final Object response;
    @Expose
    @SuppressWarnings({
            "PMD.UnusedPrivateField",
            "PMD.SingularField"
    })
    private final int taskID;
    @Expose
    private final boolean valid;
    @Expose
    private final boolean win;

    Response(final Type type, final boolean valid, final boolean win, final Object response,
             final String msg, final int taskID) {
        super(type);
        this.valid = valid;
        this.win = win;
        this.response = response;
        this.msg = msg;
        this.taskID = taskID;
    }

    public static Response fromJson(final String str) {
        return SerializableJson.gson().fromJson(str, Response.class);
    }

    public boolean isValid() {
        return this.valid;
    }

    public boolean isWin() {
        return this.win;
    }

    abstract static class Builder {

        String msg;
        Response response;
        boolean valid;
        boolean win;

        Builder() {
            this.msg = "";
            this.valid = true;
            this.win = false;
        }

        abstract void build();

        public Response getResponse() {
            return this.response;
        }

        public void setError(final String msg) {
            this.msg = msg;
            this.valid = false;
            this.win = false;
        }

        public void setWin(final boolean win) {
            this.msg = win ? "Game won." : "Game lost.";
            this.valid = true;
            this.win = win;
        }
    }

    /**
     * Small class to send error
     */
    static class ErrorBuilder extends Builder {

        final String msg;

        ErrorBuilder(final String msg) {
            this.msg = msg;
        }

        @Override
        void build() {
            this.response = new Response(ERROR, false, false, null, this.msg, -1);
        }
    }

    public static class InitResponse extends Response {

        InitResponse(final boolean valid, final Object response, final String msg) {
            super(INIT, valid, false, response, msg, -1);
        }

        public static class Builder extends Response.Builder {

            Game.Frame frame;

            public void addFrame(final Game.Frame frame) {
                this.frame = frame;
            }

            @Override
            public void build() {
                this.response = new InitResponse(this.valid, this.frame, this.msg);
            }
        }
    }

    public static class MapUpdateResponse extends Response {

        MapUpdateResponse(final boolean valid, final boolean win, final Object response,
                          final String msg) {
            super(MAP_UPDATE, valid, win, response, msg, -1);
        }

        public static class Builder extends Response.Builder {

            Game.Frame frame;

            @Override
            public void build() {
                this.response = new MapUpdateResponse(this.valid, this.win, this.frame, this.msg);
            }

            void setFrame(final Game.Frame frame) {
                this.frame = frame;
            }
        }
    }

    public static class MoveMultiplayerResponse extends Response {

        MoveMultiplayerResponse(final boolean win, final Object response) {
            super(MOVE_MULTI, true, win, response, "", -1);
        }

        public static class Builder extends Response.Builder {

            private Game.Frame frame;

            @Override
            public void build() {
                this.response = new MoveMultiplayerResponse(this.win, this.frame);
            }

            void setFrame(final Game.Frame frame) {
                this.frame = frame;
            }
        }
    }

    public static class RegisterResponse extends Response {

        RegisterResponse(final boolean valid, final Object response, final String msg) {
            super(REGISTER, valid, false, response, msg, -1);
        }

        public static class Builder extends Response.Builder {

            final int userID;

            public Builder(final int userID) {
                super();
                this.userID = userID;
            }

            @Override
            public void build() {
                this.response = new RegisterResponse(this.valid, this.userID, this.msg);
            }
        }
    }

    public static class RoomResponse extends Response {

        RoomResponse(final Object response, final String msg) {
            super(ROOM, true, false, response, msg, -1);
        }

        public static class Builder extends Response.Builder {

            final List<Room> rooms;

            public Builder(final List<Room> rooms) {
                super();
                this.rooms = rooms;
            }

            @Override
            public void build() {
                this.response = new RoomResponse(this.rooms, this.msg);
            }
        }
    }

    public static class ShutdownResponse extends Response {

        ShutdownResponse(final String msg) {
            super(SHUTDOWN, true, false, null, msg, -1);
        }

        public static class Builder extends Response.Builder {

            @Override
            public void build() {
                this.response = new ShutdownResponse("bye");
            }
        }
    }

    public static class StartGameMultiplayerResponse extends Response {

        StartGameMultiplayerResponse(final boolean win, final Game.Frame frame) {
            super(START_GAME_MULTIPLAYER, true, win, frame, "", -1);
        }

        public static class Builder extends Response.Builder {

            private final Game.Frame frame;

            public Builder(final Game.Frame frame) {
                super();
                this.frame = frame;
            }

            @Override
            public void build() {
                this.response = new StartGameMultiplayerResponse(this.win, this.frame);
            }
        }
    }

    public static class StopMultiResponse extends Response {

        StopMultiResponse(final Player winner) {
            super(STOP_MULTI, true, false, winner, "", -1);
        }

        public static class Builder extends Response.Builder {

            final Player player;

            public Builder(final Player player) {
                super();
                this.player = player;
            }

            @Override
            public void build() {
                this.response = new StopMultiResponse(this.player);
            }
        }
    }

    public static class TaskResponse extends Response {

        TaskResponse(final boolean valid, final boolean win, final Object response, final String msg,
                     final int taskID) {
            super(TASK, valid, win, response, msg, taskID);
        }

        public static class Builder extends Response.Builder {

            private final Integer taskID;
            Queue<Game.Frame> frames;

            public Builder(final Integer taskID) {
                super();
                this.frames = new ArrayDeque<>();
                this.taskID = taskID;
            }

            public void addFrame(final Game.Frame frame) {
                this.frames.add(frame);
            }

            @Override
            public void build() {
                this.response = new TaskResponse(this.valid, this.win, this.frames, this.msg, this.taskID);
            }
        }
    }

    /**
     * Give general info about the tournament the player is currently playing
     */
    public static class TournamentResponse extends Response {

        TournamentResponse(final Object response, final String msg) {
            super(TOURNAMENT, true, false, response, msg, -1);
        }

        public static class Builder extends Response.Builder {

            final Tournament tournament;

            public Builder(final Tournament tournament) {
                super();
                this.tournament = tournament;
            }

            @Override
            public void build() {
                this.response = new TournamentResponse(this.tournament, this.msg);
            }
        }
    }

    /**
     * Give general info about room id in tournament
     */
    public static class TournamentRoomResponse extends Response {

        TournamentRoomResponse(final Object response, final String msg) {
            super(TOURNAMENT_ROOM, true, false, response, msg, -1);
        }

        public static class Builder extends Response.Builder {

            private final int id;

            public Builder(final int id) {
                super();
                this.id = id;
            }

            @Override
            public void build() {
                this.response = new TournamentRoomResponse(this.id, this.msg);
            }
        }
    }

    /**
     * Give general info about tournaments available
     */
    public static class TournamentsResponse extends Response {

        TournamentsResponse(final Object response, final String msg) {
            super(TOURNAMENTS, true, false, response, msg, -1);
        }

        public static class Builder extends Response.Builder {

            final TournamentManager tournamentManager;

            public Builder(final TournamentManager tournamentManager) {
                super();
                this.tournamentManager = tournamentManager;
            }

            @Override
            public void build() {
                this.response = new TournamentsResponse(this.tournamentManager, this.msg);
            }
        }
    }

    /**
     * Let the winner know they won
     */
    public static class TournamentWinResponse extends Response {
        public TournamentWinResponse(final String msg) {
            super(TOURNAMENT_WIN, true, true, null, msg, -1);
        }
    }
}
