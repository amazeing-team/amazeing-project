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

import amazing.model.User;
import org.jetbrains.annotations.Contract;

import java.util.HashMap;
import java.util.Map;

public class UserManager {

    private int id;
    private final Map<WebSocketInterface, User> users;

    @Contract(pure = true)
    UserManager() {
        this.users = new HashMap<>();
    }

    /**
     * Registers a user with his connection.
     *
     * @param conn the websocket connection
     * @param name the name of the user
     * @return the registered user
     */
    User registerUser(final WebSocketInterface conn, final String name, final User.Color color) {
        final var user = new User(this.id++, name, conn, color);
        this.users.put(conn, user);
        return user;
    }

    public User getUserByWebsocket(final WebSocketInterface conn) {
        return this.users.get(conn);
    }
}
