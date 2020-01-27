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
import amazing.model.Marker;
import amazing.model.SerializableJson;
import amazing.model.Tuple.Pair;
import amazing.model.Tuple.Pair.Coordinate;
import amazing.model.User;
import com.google.gson.annotations.Expose;
import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.function.Function;

import static amazing.Config.GOGGLES_DURABILITY;
import static amazing.Config.VISIBILITY;

public class Player extends SerializableJson implements Cloneable {

    private static final Logger LOGGER = LoggerFactory.getLogger(Player.class);
    @Expose
    Pair.Coordinate coordinate;
    @Expose
    private Direction direction;
    @Expose
    Integer health;
    /// is the player ready to play (only used in multi-player)
    @Expose
    private boolean ready;
    private Pair.Coordinate initialCoordinate;
    private Direction initialDirection;
    private Integer initialHealth;
    private Map<Coordinate, Marker> initialMarkers;
    @Expose
    private final int uid;
    @Expose
    private final User.Color color;
    @Expose
    private Map<Coordinate, Marker> markers;
    private int visibleDistance = VISIBILITY;
    private int goggles;

    @Contract(pure = true)
    Player(@NotNull final Coordinate coordinate, final Direction direction, final Integer health,
           final int uid, final User.Color color, final Map<Coordinate, Marker> markers) {
        this.coordinate = coordinate.clone();
        this.initialDirection = direction;
        this.initialCoordinate = coordinate.clone();
        this.initialHealth = health;
        this.initialMarkers = new HashMap<>();
        this.direction = direction;
        this.health = health;
        this.markers = new HashMap<>(markers);
        this.uid = uid;
        this.color = color;
    }

    void reset() {
        this.coordinate = this.initialCoordinate.clone();
        this.direction = this.initialDirection;
        this.health = this.initialHealth;
        this.markers = new HashMap<>(this.initialMarkers);
        Player.LOGGER
                .trace("reset({}, {}, {}, {})", this.coordinate, this.direction, this.health, this.markers);
    }

    void setCurrentAsInitial() {
        this.initialCoordinate = this.coordinate.clone();
        this.initialDirection = this.direction;
        this.initialHealth = this.health;
        this.initialMarkers = new HashMap<>(this.markers);
        Player.LOGGER
                .trace("setCurrentAsInitial({}, {}, {}, {})", this.initialCoordinate, this.initialDirection,
                        this.initialHealth, this.initialMarkers);
    }

    void computeCoordinate(@NotNull final Function<Pair.Coordinate, Pair.Coordinate> coordinate) {
        this.coordinate = coordinate.apply(this.coordinate);
    }

    void computeDirection(@NotNull final Function<Direction, Direction> direction) {
        this.direction = direction.apply(this.direction);
    }

    void computeHealth(@NotNull final Function<Integer, Integer> health) {
        this.health = health.apply(this.health);
    }

    public Coordinate getCoordinate() {
        return this.coordinate;
    }

    public Direction getDirection() {
        return this.direction;
    }

    public int getHealth() {
        return this.health;
    }

    /**
     * Decreases durability of items with non-immediate effects (e.g. Goggles).
     */
    void wearOff() {
        if (this.goggles > 0) {
            this.goggles--;
        }
    }

    void pickupGoggles() {
        this.goggles = GOGGLES_DURABILITY;
    }

    boolean hasGoggles() {
        return this.goggles > 0;
    }

    void addMarker(final Coordinate coordinate, final Marker marker) {
        Player.LOGGER.trace("addMarker({}, {})", coordinate, marker);
        this.markers.put(coordinate, marker);
    }

    void removeMarker(final Coordinate coordinate) {
        Player.LOGGER.trace("removeMarker({})", coordinate);
        this.markers.remove(coordinate);
    }

    Optional<Marker> getMarker(final Coordinate coordinate) {
        Player.LOGGER
                .trace("getMarker({}, {})", coordinate, this.markers.getOrDefault(coordinate, null));
        return Optional.ofNullable(this.markers.getOrDefault(coordinate, null));
    }

    int getVisibleDistance() {
        return this.visibleDistance;
    }

    void computeVisibleDistance(@NotNull final Function<Integer, Integer> visibleDistance) {
        this.visibleDistance = visibleDistance.apply(this.visibleDistance);
    }

    @Override
    @SuppressFBWarnings("CN_IDIOM_NO_SUPER_CALL")
    @SuppressWarnings({
            "PMD.ProperCloneImplementation",
            "PMD.CloneThrowsCloneNotSupportedException"
    })
    public Player clone() {
        return new Player(this.coordinate.clone(), this.direction, this.health, this.uid, this.color,
                this.markers);
    }

    public boolean isReady() {
        return this.ready;
    }

    void setReady(final boolean ready) {
        this.ready = ready;
    }
}
