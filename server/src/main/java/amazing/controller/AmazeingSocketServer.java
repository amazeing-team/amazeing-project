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

import amazing.controller.WebSocketInterface.WebSocketWrapper;
import org.java_websocket.WebSocket;
import org.java_websocket.handshake.ClientHandshake;
import org.java_websocket.server.WebSocketServer;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;

/**
 * Class responsible for all the websocket communication.
 */
public class AmazeingSocketServer extends WebSocketServer {

    private static final Logger LOGGER = LoggerFactory.getLogger(AmazeingSocketServer.class);
    private final Server server;

    public AmazeingSocketServer(final InetSocketAddress address, final boolean remote) {
        super(address);
        this.server = new Server(remote);
    }

    @Override
    public void onOpen(@NotNull final WebSocket conn, @NotNull final ClientHandshake handshake) {
        LOGGER.trace("Websocket connection opened({}, {})",
                conn.getRemoteSocketAddress().getAddress().getHostAddress(), handshake);
        conn.send("{\"status\":\"Connected\"}");
    }

    @Override
    @SuppressWarnings("PMD.AvoidCatchingThrowable")
    public void onClose(@NotNull final WebSocket conn, final int code, final String reason,
                        final boolean remote) {
        LOGGER.trace("Websocket connection closed({}, {}, {}, {})", conn.toString(), code, reason,
                remote);
        try {
            this.broadcast(server.handleClose(new WebSocketWrapper(conn)));
        } catch (final Throwable throwable) {
            LOGGER.error("error({}, {})", throwable.getMessage(), throwable.getStackTrace());
        }
    }

    @Override
    @SuppressWarnings("PMD.AvoidCatchingThrowable")
    public void onMessage(@NotNull final WebSocket conn, final String message) {
        LOGGER.trace("Websocket message received({}, {})", conn, message);
        LOGGER.trace("Server({})", server);
        try {
            server.handleMessage(message, new WebSocketWrapper(conn)).ifPresent(this::broadcast);
        } catch (final Throwable throwable) {
            LOGGER.error("error({}, {})", throwable.getMessage(), throwable.getStackTrace());
        }
    }

    @Override
    @SuppressWarnings("PMD.AvoidCatchingThrowable")
    public void onMessage(@NotNull final WebSocket conn, final ByteBuffer message) {
        LOGGER.trace("message({}, {})", conn, message);
        try {
            server.handleMessage(new String(message.array(), StandardCharsets.UTF_8),
                    new WebSocketWrapper(conn));
        } catch (final Throwable throwable) {
            LOGGER.error("error({}, {})", throwable.getMessage(), throwable.getStackTrace());
        }
    }

    @Override
    public void onError(final WebSocket conn, @NotNull final Exception ex) {
        LOGGER.error("error({}, {})", ex.getMessage(), ex.getStackTrace());
    }

    @Override
    public void onStart() {
        LOGGER.trace("start({}, {})", this.getAddress(), this.getPort());
    }
}