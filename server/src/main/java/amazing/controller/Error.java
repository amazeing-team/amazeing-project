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
import amazing.model.User;
import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

import java.util.Arrays;
import java.util.Objects;

public class Error extends Exception {

    @SuppressFBWarnings("IMC_IMMATURE_CLASS_BAD_SERIALVERSIONUID")
    private static final long serialVersionUID = 3522745334532759294L;
    private final Object[] args;
    private final Type type;

    private Error(final Type type, final Object... args) {
        this.type = type;
        this.args = Arrays.stream(args).map(Object::toString).toArray(String[]::new);
    }

    @Override
    public int hashCode() {
        return this.type.hashCode();
    }

    @Contract(value = "null -> false", pure = true)
    @Override
    public boolean equals(final Object obj) {
        if (Objects.nonNull(obj) && obj instanceof Error) {
            return obj == this || ((Error) obj).type == this.type;
        }
        return false;
    }

    @Contract(pure = true)
    public Type getType() {
        return this.type;
    }

    @Override
    public String toString() {
        return String.format(this.type.getFormat(), this.args);
    }

    public enum Type {
        EXIT_NOT_REACHABLE("Exit is not reachable.", true),
        FILE_ERROR("Could not open file.", true),
        NOT_FULLY_CONNECTED("Tiles not fully connected.", true),
        MISSING_VALUE("Missing value '%s'.", true),
        KEY_ERROR("Missing key '%s'", true),
        GAME_OVER("%s's game is over.", false),
        WRONG_MOVE("%s can not move '%s'.", false),
        EXCEPTION("%s", true),
        JSON("Json error.", true),
        CLI_ERROR("", true),
        INTERPRETER_ERROR("%s", false),
        TYPE_ERROR("%s", false),
        UNEXPECTED("You can hurt yourself if you run with scissors.", true),
        STACK("The cake is a lie.", true),
        AUTHENTICATION("You are not an admin or you forgot your password", true);
        boolean critical;
        String format;

        @Contract(pure = true)
        Type(final String format, final boolean critical) {
            this.format = format;
            this.critical = critical;
        }

        @Contract(pure = true)
        public String getFormat() {
            return this.format;
        }
    }

    public static class ErrorFactory {

        @Contract(pure = true)
        private ErrorFactory() {
            // empty
        }

        @NotNull
        public static Error createStackError() {
            return new Error(Type.STACK);
        }

        @NotNull
        public static Error createCliError() {
            return new Error(Type.CLI_ERROR);
        }

        @NotNull
        public static Error createExitNotReachable() {
            return new Error(Type.EXIT_NOT_REACHABLE);
        }

        @NotNull
        static Error createGameOver(final User user) {
            return new Error(Type.GAME_OVER, user);
        }

        @NotNull
        @Contract(" -> new")
        public static Error createUnexpectedError() {
            return new Error(Type.UNEXPECTED);
        }

        @NotNull
        @Contract("_ -> new")
        public static Error createInterpreterError(final String str) {
            return new Error(Type.INTERPRETER_ERROR, str);
        }

        @NotNull
        @Contract("_ -> new")
        public static Error createTypeError(final String str) {
            return new Error(Type.TYPE_ERROR, str);
        }

        @NotNull
        public static Error createJsonError() {
            return new Error(Type.JSON);
        }

        @NotNull
        public static Error createKeyError(final Object object) {
            return new Error(Type.KEY_ERROR, object);
        }

        @NotNull
        public static Error createMissingValue(final Object object) {
            return new Error(Type.MISSING_VALUE, object);
        }

        @NotNull
        public static Error createNotFullyConnected() {
            return new Error(Type.NOT_FULLY_CONNECTED);
        }

        @NotNull
        static Error createWrongPlayerMove(final User user, final Direction direction) {
            return new Error(Type.WRONG_MOVE, user, direction);
        }

        public static Error createAuthenticationError() {
            return new Error(Type.AUTHENTICATION);
        }
    }
}
