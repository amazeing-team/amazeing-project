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

import java.util.Objects;
import java.util.Stack;

public interface WebSocketInterface {

    void send(String string);

    class Dummy extends Stack<String> implements WebSocketInterface {

        private static final long serialVersionUID = 7197802113893795928L;
        private final int id;

        public Dummy(final int id) {
            this.id = id;
        }

        @Override
        public void send(final String string) {
            this.add(string);
        }

        @Override
        public int hashCode() {
            return this.id;
        }

        @Override
        public boolean equals(final Object obj) {
            if (Objects.nonNull(obj) && obj instanceof Dummy) {
                return obj == this || ((Dummy) obj).id == this.id;
            }
            return false;
        }
    }

    class WebSocketWrapper implements WebSocketInterface {

        private final org.java_websocket.WebSocket webSocket;

        WebSocketWrapper(final org.java_websocket.WebSocket webSocket) {
            this.webSocket = webSocket;
        }

        @Override
        public void send(final String string) {
            this.webSocket.send(string);
        }

        @Override
        public boolean equals(final Object o) {
            if (this == o) {
                return true;
            }
            if (o == null || getClass() != o.getClass()) {
                return false;
            }
            final WebSocketWrapper that = (WebSocketWrapper) o;
            return Objects.equals(webSocket, that.webSocket);
        }

        @Override
        public int hashCode() {
            return Objects.hash(webSocket);
        }
    }
}
