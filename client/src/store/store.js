import Vue from 'vue';
import Vuex from 'vuex';

import user from './modules/user';
import settings from './modules/settings';
import game from './modules/game';
import tasks from './modules/tasks';
import rooms from './modules/rooms';
import tournaments from './modules/tournaments';
import tournament from './modules/tournament';

Vue.use(Vuex);

export default new Vuex.Store({
  modules: {
    tasks,
    user,
    settings,
    game,
    rooms,
    tournaments,
    tournament,
  },
  state: {
    socket: {
      isConnected: false,
      message: '',
      reconnectError: false,
    },
    socketConnected: false,
  },
  mutations: {
    SET_TASKS: (state, payload) => {
      state.tasks = payload;
      state.loaded = true;
    },
    SOCKET_ONOPEN(state, event) {
      Vue.prototype.$socket = event.currentTarget;
      state.socketConnected = true;
    },
    SOCKET_ONCLOSE(state) {
      console.log('on_close');
      state.socketConnected = false;
    },
    SOCKET_ONERROR(state, event) {
      console.error(state, event);
    },
    // default handler called for all methods
    SOCKET_ONMESSAGE(state, message) {
      state.socketConnected = true;
      // eslint-disable-next-line no-param-reassign
      state.socket.message = message;
      console.log('Received message from websocket: ', message);
      if (message.type === 'REGISTER') {
        state.user.id = message.response;
      } else if (message.type === 'TASK') {
        state.game.allData = message.response;
        state.game.valid = message.valid;
        state.game.win = message.win;
        state.game.msg = message.msg;
        state.game.renderBoard = !state.game.renderBoard;
      } else if (message.type === 'MAP_UPDATE') {
        Object.entries(message.response).forEach(([key, entry]) => {
          if (key === 'players') {
            Object.entries(entry).forEach(([idx, player]) => {
              state.game.players[idx] = player;
              state.game.initialState.players[idx] = player;
            });
          }
          state.game[key] = entry;
          state.game.initialState[key] = entry;
        });
        state.game.renderBoard = !state.game.renderBoard;
        state.game.win = message.win;
      } else if (message.type === 'START_GAME_MULTIPLAYER') {
        if (message.response.players[this.state.user.id]) {
          // user is in the room
          // init game state and open multiplayer game
          state.game.initialState = message.response;
          Object.entries(state.game.initialState)
            .forEach(([key, entry]) => {
              state.game[key] = entry;
            });
          state.rooms.play = true;
          state.rooms.win = false;
          state.tournament.play = true;
          state.game.renderBoard = !state.game.renderBoard;
        } else {
          console.log('ignored', message.response.players, this.state.user.id);
        }
      } else if (message.type === 'INIT') {
        // update game state
        state.game.initialState = message.response;
        Object.entries(message.response).forEach(([key, entry]) => {
          state.game[key] = entry;
        });
        state.game.renderBoard = !state.game.renderBoard;
      } else if (message.type === 'ROOM') {
        this.state.rooms.rooms = message.response;
      } else if (message.type === 'MOVE_MULTI') {
        Object.entries(message.response)
          .forEach(([key, entry]) => {
            this.state.game[key] = entry;
          });
        state.rooms.win = message.win;
        state.game.renderBoard = !state.game.renderBoard;
      } else if (message.type === 'STOP_MULTI') {
        state.rooms.winner = message.response;
        state.tournament.play = false;
      } else if (message.type === 'TOURNAMENTS') {
        state.tournaments.tournaments = message.response.tournaments;
      } else if (message.type === 'TOURNAMENT') {
        state.tournament.tournament = message.response;
      } else if (message.type === 'TOURNAMENT_ROOM') {
        state.rooms.id = message.response;
        state.tournament.play = true;
      } else if (message.type === 'TOURNAMENT_WIN') {
        state.tournament.winTournament = true;
      }
    },
    // mutations for reconnect methods
    SOCKET_RECONNECT(state, count) {
      console.info(state, count);
    },
    SOCKET_RECONNECT_ERROR(state) {
      state.socket.reconnectError = true;
    },
  },
});
