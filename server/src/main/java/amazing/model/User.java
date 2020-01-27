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

import amazing.controller.WebSocketInterface;
import com.google.gson.annotations.Expose;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

import java.util.Objects;

/**
 * User class.
 */
public class User extends SerializableJson implements Comparable<User> {

    @Expose
    private final int uid;
    @Expose
    private String name;
    @Expose
    private Color color;
    private final WebSocketInterface webSocket;

    /**
     * Creates a user.
     *
     * @param uid       the unique id of the user
     * @param name      the name of the user
     * @param webSocket the socket over which the user communicates
     * @param color     color of owl picked
     */
    @Contract(pure = true)
    public User(final int uid, final String name, final WebSocketInterface webSocket,
                final Color color) {
        this.uid = uid;
        this.name = name;
        this.color = color;
        this.webSocket = webSocket;
    }

    @Override
    public int compareTo(@NotNull final User user) {
        return Integer.compare(this.uid, user.uid);
    }

    @Override
    public int hashCode() {
        return this.uid;
    }

    @Contract(value = "null -> false", pure = true)
    @Override
    public boolean equals(final Object obj) {
        if (Objects.nonNull(obj) && obj instanceof User) {
            return obj == this || ((User) obj).uid == this.uid;
        }
        return false;
    }

    @Override
    public String toString() {
        return this.name;
    }

    public int getUid() {
        return uid;
    }

    public String getName() {
        return name;
    }

    public void setName(final String name) {
        this.name = name;
    }

    public void setColor(final Color color) {
        this.color = color;
    }

    public void send(final String message) {
        this.webSocket.send(message);
    }

    public Color getColor() {
        return color;
    }

    /**
     * Enum representing the available owl colors.
     */
    public enum Color {
        BLUE,
        PINK,
        ORANGE,
        PURPLE,
    }
}
