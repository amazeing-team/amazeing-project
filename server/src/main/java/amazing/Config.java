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

package amazing;

import amazing.model.Direction;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public class Config {

    public static final Direction INITIAL_DIRECTION = Direction.NORTH;
    public static final int MAX_STACK = 1000;
    public static final int INITIAL_HEALTH = 10;
    public static final int INITIAL_SEED = 1337;
    public static final int VISIBILITY = 1;
    public static final int GOGGLES_DURABILITY = 1;
    public static final double ITEM_DROP_PROP = 0.5;
    public static final String SECRET_KEY = "thesecretpassword";
    static final String VERSION = "0.5";
    public static final Map<Character, Set<Direction>> UNICODE =
            Collections.unmodifiableMap(new HashMap<>(16, 1.0f) {
                private static final long serialVersionUID = 362414457398823312L;

                {
                    this.put('│', Set.of(Direction.NORTH, Direction.SOUTH));
                    this.put('─', Set.of(Direction.EAST, Direction.WEST));
                    this.put('╰', Set.of(Direction.NORTH, Direction.EAST));
                    this.put('╭', Set.of(Direction.EAST, Direction.SOUTH));
                    this.put('╮', Set.of(Direction.SOUTH, Direction.WEST));
                    this.put('╯', Set.of(Direction.NORTH, Direction.WEST));
                    this.put('┬', Set.of(Direction.EAST, Direction.SOUTH, Direction.WEST));
                    this.put('┤', Set.of(Direction.NORTH, Direction.SOUTH, Direction.WEST));
                    this.put('┴', Set.of(Direction.NORTH, Direction.EAST, Direction.WEST));
                    this.put('├', Set.of(Direction.NORTH, Direction.EAST, Direction.SOUTH));
                    this.put('┼', Set.of(Direction.NORTH, Direction.EAST, Direction.SOUTH, Direction.WEST));
                    this.put('╵', Set.of(Direction.NORTH));
                    this.put('╶', Set.of(Direction.EAST));
                    this.put('╷', Set.of(Direction.SOUTH));
                    this.put('╴', Set.of(Direction.WEST));
                    this.put('·', Set.of());
                }
            });
}
