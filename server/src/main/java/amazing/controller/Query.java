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

import amazing.model.Direction;
import amazing.model.Maze;
import amazing.model.SerializableJson;
import amazing.model.User;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import org.jetbrains.annotations.NotNull;

import static amazing.controller.Query.Type.*;

/**
 * Class used to deserialize json sent from the client
 */
public class Query extends SerializableJson {

    @Expose
    private final Type type;

    Query(@NotNull final Type type) {
        this.type = type;
    }

    static Query fromJson(final String str) {
        return SerializableJson.gson().fromJson(str, Query.class);
    }

    Type getType() {
        return this.type;
    }

    public enum Type {
        REGISTER,
        UPDATE_USER,
        INIT,
        SHUTDOWN,
        TASK,
        CREATE_ROOM_MULTIPLAYER,
        JOIN_ROOM_MULTIPLAYER,
        READY_MULTIPLAYER,
        START_GAME_MULTIPLAYER,
        TURN_LEFT,
        TURN_RIGHT,
        MOVE,
        ROOM,
        MAP_UPDATE,
        TURN_LEFT_MULTI,
        TURN_RIGHT_MULTI,
        MOVE_MULTI,
        STOP_MULTI, // someone won the game
        CREATE_TOURNAMENT,
        JOIN_TOURNAMENT,
        START_TOURNAMENT,
        ENDROUND_TOURNAMENT,
        REMOVE_TOURNAMENT,
        TOURNAMENTS,
        TOURNAMENT,
        TOURNAMENT_ROOM,
        TOURNAMENT_WIN,
        TURN_LEFT_TOURNAMENT,
        TURN_RIGHT_TOURNAMENT,
        MOVE_TOURNAMENT,
        WATCH_TOURNAMENT,
        ERROR
    }

    /**
     * Send by the client when creating a new room for a multiplayer game This is just an abstraction
     * over the json object
     */
    public static class CreateRoomMultiplayer extends Query {

        @Expose
        private final String name;
        @Expose
        private final int width;
        @Expose
        private final int height;

        public CreateRoomMultiplayer(final String name, final int width, final int height) {
            super(CREATE_ROOM_MULTIPLAYER);
            this.name = name;
            this.width = width;
            this.height = height;
        }

        static CreateRoomMultiplayer fromJson(final String str) {
            return SerializableJson.gson().fromJson(str, CreateRoomMultiplayer.class);
        }

        String getName() {
            return this.name;
        }

        int getWidth() {
            return width;
        }

        int getHeight() {
            return height;
        }
    }

    public static class UpdateUser extends Query {

        @Expose
        private final String name;
        @Expose
        private final User.Color color;

        public UpdateUser(final String name, final User.Color color) {
            super(UPDATE_USER);
            this.name = name;
            this.color = color;
        }

        static UpdateUser fromJson(final String str) {
            return SerializableJson.gson().fromJson(str, UpdateUser.class);
        }

        public User.Color getColor() {
            return color;
        }

        public String getName() {
            return name;
        }
    }

    public static class JoinRoomMultiplayer extends Query {

        @Expose
        private final int id;

        public JoinRoomMultiplayer(final int roomId) {
            super(JOIN_ROOM_MULTIPLAYER);
            this.id = roomId;
        }

        static JoinRoomMultiplayer fromJson(final String str) {
            return SerializableJson.gson().fromJson(str, JoinRoomMultiplayer.class);
        }

        int getId() {
            return this.id;
        }
    }

    public static class ReadyMultiplayer extends Query {

        @Expose
        private final int id;

        public ReadyMultiplayer(final int roomId) {
            super(READY_MULTIPLAYER);
            this.id = roomId;
        }

        static ReadyMultiplayer fromJson(final String str) {
            return SerializableJson.gson().fromJson(str, ReadyMultiplayer.class);
        }

        int getId() {
            return this.id;
        }
    }

    public static class Register extends Query {

        @Expose
        private final String name;
        @Expose
        private final User.Color color;

        public Register(final String name, final User.Color color) {
            super(REGISTER);
            this.name = name;
            this.color = color;
        }

        static Register fromJson(final String str) {
            return SerializableJson.gson().fromJson(str, Register.class);
        }

        String getName() {
            return this.name;
        }

        public User.Color getColor() {
            return color;
        }
    }

    public static class Init extends Query {

        @Expose
        @SerializedName("json_map")
        private final Maze jsonMap;
        @Expose
        private final Integer health;
        @Expose
        private final Direction direction;

        public Init(final Maze jsonMap, final Integer health, final Direction direction) {
            super(INIT);
            this.jsonMap = jsonMap;
            this.health = health;
            this.direction = direction;
        }

        static Init fromJson(final String str) {
            return SerializableJson.gson().fromJson(str, Init.class);
        }

        Maze getMaze() {
            return this.jsonMap;
        }

        public Direction getDirection() {
            return this.direction;
        }

        Integer getHealth() {
            return this.health;
        }
    }

    public static class Shutdown extends Query {

        public Shutdown() {
            super(SHUTDOWN);
        }
    }

    public static class Task extends Query {

        @Expose
        private final String instructions;
        @Expose
        private final Integer taskID;

        public Task(final String instructions, final Integer taskID) {
            super(TASK);
            this.instructions = instructions;
            this.taskID = taskID;
        }

        static Task fromJson(final String str) {
            return SerializableJson.gson().fromJson(str, Task.class);
        }

        String getInstructions() {
            return this.instructions;
        }

        int getID() {
            return this.taskID;
        }
    }

    public static class ActionMulti extends Query {

        @Expose
        private final int rid;

        public ActionMulti(final Type type, final int rid) {
            super(type);
            this.rid = rid;
        }

        static ActionMulti fromJson(final String str) {
            return SerializableJson.gson().fromJson(str, ActionMulti.class);
        }

        int getRid() {
            return this.rid;
        }
    }

    public static class CreateTournament extends Query {

        @Expose
        private final String privateKey;
        @Expose
        private final String name;

        public CreateTournament(final String privateKey, final String name) {
            super(CREATE_TOURNAMENT);
            this.privateKey = privateKey;
            this.name = name;
        }

        static CreateTournament fromJson(final String str) {
            return SerializableJson.gson().fromJson(str, CreateTournament.class);
        }

        public String getPrivateKey() {
            return privateKey;
        }

        public String getName() {
            return name;
        }
    }

    public static class JoinTournament extends Query {

        @Expose
        private final int id;

        public JoinTournament(final int id) {
            super(JOIN_TOURNAMENT);
            this.id = id;
        }

        static JoinTournament fromJson(final String str) {
            return SerializableJson.gson().fromJson(str, JoinTournament.class);
        }

        public int getId() {
            return id;
        }
    }

    public static class RemoveTournament extends Query {

        @Expose
        private final int id;
        @Expose
        private final String privateKey;

        public RemoveTournament(final String privateKey, final int id) {
            super(REMOVE_TOURNAMENT);
            this.id = id;
            this.privateKey = privateKey;
        }

        static RemoveTournament fromJson(final String str) {
            return SerializableJson.gson().fromJson(str, RemoveTournament.class);
        }

        public int getId() {
            return id;
        }

        public String getPrivateKey() {
            return privateKey;
        }
    }

    public static class StartTournament extends Query {

        @Expose
        private final int id;
        @Expose
        private final String privateKey;

        public StartTournament(final String privateKey, final int id) {
            super(START_TOURNAMENT);
            this.id = id;
            this.privateKey = privateKey;
        }

        static StartTournament fromJson(final String str) {
            return SerializableJson.gson().fromJson(str, StartTournament.class);
        }

        public int getId() {
            return id;
        }

        public String getPrivateKey() {
            return privateKey;
        }
    }

    public static class EndRoundTournament extends Query {

        @Expose
        private final int id;
        @Expose
        private final String privateKey;

        public EndRoundTournament(final String privateKey, final int id) {
            super(ENDROUND_TOURNAMENT);
            this.id = id;
            this.privateKey = privateKey;
        }

        static EndRoundTournament fromJson(final String str) {
            return SerializableJson.gson().fromJson(str, EndRoundTournament.class);
        }

        public int getId() {
            return id;
        }

        public String getPrivateKey() {
            return privateKey;
        }
    }

    public static class WatchTournament extends Query {

        @Expose
        private final int id;
        @Expose
        private final String privateKey;

        public WatchTournament(final String privateKey, final int id) {
            super(WATCH_TOURNAMENT);
            this.id = id;
            this.privateKey = privateKey;
        }

        static WatchTournament fromJson(final String str) {
            return SerializableJson.gson().fromJson(str, WatchTournament.class);
        }

        public int getId() {
            return id;
        }

        public String getPrivateKey() {
            return privateKey;
        }
    }

    public static class ActionTournament extends Query {

        public ActionTournament(final Type type) {
            super(type);
        }

        static ActionTournament fromJson(final String str) {
            return SerializableJson.gson().fromJson(str, ActionTournament.class);
        }
    }
}
