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
import amazing.model.User;
import com.google.gson.annotations.Expose;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Class responsible to manage a tournament with multiples round
 *
 * <p>The idea tournament flow should be:
 *
 * <p>1. Admin create tournament (authentication with a key) 2. Users register to the tournament 3.
 * Admin close registration and tournament should begin assigning each user to a room with x user in
 * each room
 */
public class TournamentManager extends SerializableJson {

  private static final Logger LOGGER = LoggerFactory.getLogger(TournamentManager.class);
  @Expose
  private final List<Tournament> tournaments;
  private final String secretKey;
  private int lastId;

  public TournamentManager(final String secretKey) {
    this.secretKey = secretKey;
    this.tournaments = new ArrayList<>();
  }

  /**
   * Create new tournament
   *
   * @param privateKey the authentication key of the admin
   * @param name       the name of the tournament
   * @return Result.Ok() iff the tournament could be created
   */
  Result<?, Error> createTournament(final String privateKey, final String name) {
    if (!privateKey.equals(this.secretKey)) {
      return Result.ResultFactory.createError(Error.ErrorFactory.createAuthenticationError());
    }
    this.tournaments.add(new Tournament(this.lastId, name));
    this.lastId++;
    return Result.ResultFactory.createOk();
  }

  /**
   * Start tournament
   *
   * @param privateKey the authentication key of the admin
   * @param id         the id of the tournament
   * @return Result.Ok() iff the tournament could be started
   */
  Result<?, Error> startTournament(final String privateKey, final int id) {
    if (!privateKey.equals(this.secretKey)) {
      return Result.ResultFactory.createError(Error.ErrorFactory.createAuthenticationError());
    }
    final var tournament = this.getTournamentById(id);
    if (tournament.isEmpty()) {
      return Result.ResultFactory.createError(Error.ErrorFactory.createKeyError(id));
    }
    tournament.get().start();
    return Result.ResultFactory.createOk();
  }

  /**
   * Remove tournament from list
   *
   * @param privateKey the private key for maximal security
   * @param id         the tournament id
   * @return Result
   */
  Result<?, Error> removeTournament(final String privateKey, final int id) {
    if (!privateKey.equals(this.secretKey)) {
      return Result.ResultFactory.createError(Error.ErrorFactory.createAuthenticationError());
    }
    final var tournament = getTournamentById(id);
    tournament.ifPresent(this.tournaments::remove);
    return Result.ResultFactory.createOk();
  }

  /**
   * Add a watcher who track the status of the tournament
   *
   * @param privateKey the private key for maximal security
   * @param id         tournament id
   * @param user       the watcher
   * @return Result
   */
  Result<?, Error> trackTournament(final String privateKey, final int id, final User user) {
    LOGGER.trace("Add watcher {}", user);
    if (!privateKey.equals(this.secretKey)) {
      return Result.ResultFactory.createError(Error.ErrorFactory.createAuthenticationError());
    }
    final var tournament = this.getTournamentById(id);
    if (tournament.isPresent()) {
      tournament.get().addWatcher(user);
      tournament.get().notifyWatcher();
    }
    return Result.ResultFactory.createOk();
  }

  /**
   * Join tournament
   *
   * @param id   tournament id
   * @param user who want to join
   * @return Result
   */
  Result<?, Error> joinTournament(final int id, final User user) {
    final var tournament = this.getTournamentById(id);
    if (tournament.isEmpty()) {
      return Result.ResultFactory.createError(Error.ErrorFactory.createKeyError(id));
    }
    // make sure the current tournament is the only one with this user
    this.tournaments.forEach(tournament1 -> tournament1.getUsers().remove(user));
    if (!tournament.get().isStarted()) {
      tournament.get().addUser(user);
    }
    tournament.get().notifyWatcher();
    return Result.ResultFactory.createOk();
  }

  /**
   * Helper method to get a tournament by id and not by index
   * @param id the tournament id
   * @return a tournament with the given id iff exist
   */
  Optional<Tournament> getTournamentById(final int id) {
    return this.tournaments.stream().filter(tournament -> tournament.getId() == id).findFirst();
  }

  /**
   * End round event
   * @param privateKey
   * @param id
   * @return
   */
  Result<Optional<User>, Error> endRound(final String privateKey, final int id) {
    if (!privateKey.equals(this.secretKey)) {
      return Result.ResultFactory.createError(Error.ErrorFactory.createAuthenticationError());
    }
    final var tournament = this.getTournamentById(id);
    if (tournament.isEmpty()) {
      return Result.ResultFactory.createError(Error.ErrorFactory.createKeyError(id));
    }
    tournament.get().endRound();
    return Result.ResultFactory.createOk();
  }

  /**
   * Get tournament where user is playing
   * @param user the user
   * @return a tournament iff this user is playing in one
   */
  private Optional<Tournament> getTournamentByUser(final User user) {
    return this.tournaments.stream().filter(tournament -> tournament.getUsers().contains(user))
        .findFirst();
  }

  /**
   * Handle move action from player
   * @param type the type of action (turn left, turn right or move)
   * @param user the user who executed this action
   */
  void handleAction(final Query.Type type, final User user) {
    final var tournament = this.getTournamentByUser(user).get();
    if (type == Query.Type.MOVE_TOURNAMENT) {
      tournament.handleAction(user, Game.Action.MOVE);
    } else if (type == Query.Type.TURN_LEFT_TOURNAMENT) {
      tournament.handleAction(user, Game.Action.TURN_LEFT);
    } else if (type == Query.Type.TURN_RIGHT_TOURNAMENT) {
      tournament.handleAction(user, Game.Action.TURN_RIGHT);
    }
  }

  /**
   * Remove user from all tournaments (probably because of a disconnect)
   * @param user the user we want to remove
   */
  void removeUser(final @NotNull User user) {
    this.tournaments.forEach(tournament -> {
      tournament.getUsers().remove(user);
      tournament.getWatcher().remove(user);
    });
  }
}
