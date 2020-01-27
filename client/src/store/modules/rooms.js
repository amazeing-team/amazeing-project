import Vue from 'vue';

const state = {
  rooms: {},
  id: 0,
  play: false,
  win: false,
  winner: null,
};

const getters = { };

const actions = {
  createRoom(ctx, { name, width, height }) {
    Vue.prototype.$socket.sendObj({
      type: 'CREATE_ROOM_MULTIPLAYER',
      name,
      width,
      height,
    });
  },
  joinRoom({ state }, { id }) {
    state.id = id;
    Vue.prototype.$socket.sendObj({
      type: 'JOIN_ROOM_MULTIPLAYER',
      id,
    });
  },
  readyRoom(ctx, { id }) {
    Vue.prototype.$socket.sendObj({
      type: 'READY_MULTIPLAYER',
      id,
    });
  },
  multiplayerAction({ state }, action) {
    Vue.prototype.$socket.sendObj({
      type: action,
      rid: state.id,
    });
  },
  reset({ state }) {
    state.play = false;
    state.win = false;
    state.winner = null;
  },
};

const mutations = { };

export default {
  namespaced: true,
  state,
  getters,
  actions,
  mutations,
};
