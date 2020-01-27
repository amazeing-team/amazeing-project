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

import java.util.Random;

/**
 * Class for Items to be picked up and used in the game.
 */
public abstract class Item extends SerializableJson {

    @Expose
    @SuppressWarnings({
            "PMD.UnusedPrivateField",
            "PMD.SingularField"
    })
    private final ItemType type;

    Item(final ItemType type) {
        this.type = type;
    }

    enum ItemType {
        GOGGLES,
        CARROT,
        TELEPORT
    }

    public abstract Result<?, Error> execute(final Game game, final Player player);

    public static class ItemFactory {

        /**
         * Returns a Carrot, Goggles or Teleport with one third probability each.
         *
         * @param random the Random object to be used
         * @return the random item
         */
        static Item createRandomItem(final Random random) {
            switch (random.nextInt(3)) {
                case 0:
                    return new Carrot();
                case 1:
                    return new Goggles();
                default:
                    return new Teleport();
            }
        }
    }

    static class Carrot extends Item {

        Carrot() {
            super(ItemType.CARROT);
        }

        @Override
        public Result<?, Error> execute(final Game game, final Player player) {
            player.computeVisibleDistance(dist -> ++dist);
            return Result.ResultFactory.createOk();
        }
    }

    static class Goggles extends Item {

        Goggles() {
            super(ItemType.GOGGLES);
        }

        @Override
        public Result<?, Error> execute(final Game game, final Player player) {
            player.pickupGoggles();
            return Result.ResultFactory.createOk();
        }
    }

    static class Teleport extends Item {

        Teleport() {
            super(ItemType.TELEPORT);
        }

        @Override
        public Result<?, Error> execute(final Game game, final Player player) {
            return game.teleport(player);
        }
    }
}
