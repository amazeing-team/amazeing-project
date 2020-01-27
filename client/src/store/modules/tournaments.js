import Vue from 'vue';

const state = {
  tournaments: {},
  privateKey: 'thesecretpassword',
};

const actions = {
  createTournament({ state }, { name }) {
    Vue.prototype.$socket.sendObj({
      type: 'CREATE_TOURNAMENT',
      name,
      privateKey: state.privateKey,
    });
  },
  joinTournament({ rootState }, { id }) {
    Vue.prototype.$socket.sendObj({
      type: 'JOIN_TOURNAMENT',
      id,
    });
    rootState.tournament.id = id;
    rootState.tournament.winTournament = false;
  },
  startTournament({ state }, { id }) {
    Vue.prototype.$socket.sendObj({
      type: 'START_TOURNAMENT',
      id,
      privateKey: state.privateKey,
    });
  },
  deleteTournament({ state }, { id }) {
    Vue.prototype.$socket.sendObj({
      type: 'REMOVE_TOURNAMENT',
      id,
      privateKey: state.privateKey,
    });
  },
  trackTournament({ state, rootState }, { id }) {
    Vue.prototype.$socket.sendObj({
      type: 'WATCH_TOURNAMENT',
      id,
      privateKey: state.privateKey,
    });
    rootState.tournament.id = id;
  },
};

const getters = {};
const mutations = {};

export default {
  namespaced: true,
  actions,
  getters,
  mutations,
  state,
};
