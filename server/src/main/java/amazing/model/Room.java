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

import amazing.Config;
import amazing.controller.Error;
import amazing.controller.*;
import com.google.gson.annotations.Expose;
import org.jetbrains.annotations.NotNull;

/**
 * Represent a multi-player game with the users and the game state.
 */
public class Room extends SerializableJson {

    @Expose
    private final int id;
    @Expose
    private final String name;
    @Expose
    private final Game game;
    @Expose
    private boolean playing;

    /**
     * Constructor
     *
     * @param id         the id of the room
     * @param name       the name of the room
     * @param mazeWidth  the game maze width
     * @param mazeHeight the game maze height
     */
    public Room(final int id, final String name, final int mazeWidth, final int mazeHeight) {
        this.id = id;
        this.name = name;
        this.game = new Game(
                new Generator(mazeWidth, mazeHeight, id, i -> (double) i).generate().ok().orElseThrow(),
                true);
    }

    /**
     * Is the room ready to play? is there enough player and all players are ready?
     *
     * @return true iff the room is ready
     */
    public boolean isReady() {
        return this.game.getPlayers().size() > 1 && this.game.getPlayers().values().stream()
                .allMatch(Player::isReady);
    }

    /**
     * Add player to room
     *
     * @param user the user we want to add to the room
     */
    public void addUser(final User user) {
        this.game.addPlayer(user, Config.INITIAL_DIRECTION, Config.INITIAL_HEALTH);
    }

    /**
     * Get current game state as visible for the given user
     *
     * @param user the current user
     * @return the view visible for an user
     */
    public Game.Frame getFrame(final @NotNull User user) {
        return game.buildFrame(user);
    }

    public Result<?, Error> handleAction(final @NotNull User user, final Game.Action action) {
        return this.getGame().handleAction(user, action);
    }

    public Game getGame() {
        return game;
    }

    public String getName() {
        return name;
    }

    public boolean isPlaying() {
        return playing;
    }

    public void setPlaying(final boolean playing) {
        this.playing = playing;
    }

    public boolean containsUser(final User user) {
        return this.getGame().getPlayers().get(user) != null;
    }

    public int getId() {
        return id;
    }
}
