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

import amazing.model.SerializableJson;
import com.google.gson.annotations.Expose;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

import java.util.Optional;

@SuppressWarnings({
        "PMD.ShortMethodName",
        "PMD.ShortVariable",
        "PMD.ShortClassName"
})
public abstract class Result<T, R> extends SerializableJson {

    @Contract(pure = true)
    private Result() {
        // empty
    }

    public Optional<R> error() {
        return Optional.empty();
    }

    public abstract boolean isError();

    public abstract boolean isOk();

    public Optional<T> ok() {
        return Optional.empty();
    }

    static class Error<T, R> extends Result<T, R> {

        @Expose
        R error;

        Error(final R error) {
            this.error = error;
        }

        @Override
        public Optional<R> error() {
            return Optional.ofNullable(this.error);
        }

        @Override
        public boolean isError() {
            return true;
        }

        @Override
        public boolean isOk() {
            return false;
        }
    }

    static class Ok<T, R> extends Result<T, R> {

        @Expose
        T ok;

        Ok(final T ok) {
            this.ok = ok;
        }

        @Override
        public boolean isError() {
            return false;
        }

        @Override
        public boolean isOk() {
            return true;
        }

        @Override
        public Optional<T> ok() {
            return Optional.ofNullable(this.ok);
        }
    }

    public static class ResultFactory {

        private ResultFactory() {
            // empty
        }

        @NotNull
        @Contract("_ -> new")
        public static <T, R> Result<T, R> createError(final R error) {
            return new Error<>(error);
        }

        @NotNull
        @Contract("_ -> new")
        public static <T, R> Result<T, R> createOk(final T ok) {
            return new Ok<>(ok);
        }

        @NotNull
        @Contract(" -> new")
        public static <T, R> Result<T, R> createOk() {
            return new Ok<>(null);
        }

        @SafeVarargs
        @NotNull
        public static <T, R> Result<T, R> checkErrors(final Result<T, R>... results) {
            for (final Result<T, R> res : results) {
                if (res.isError()) {
                    return res;
                }
            }
            return new Ok<>(null);
        }
    }
}




