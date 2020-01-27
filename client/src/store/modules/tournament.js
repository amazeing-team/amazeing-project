import Vue from 'vue';

const state = {
  tournament: {},
  id: -1,
  play: false,
  reload: 0,
  winTournament: false,
};

const actions = {
  tournamentAction(state, action) {
    Vue.prototype.$socket.sendObj({
      type: action,
    });
  },
};

const getters = {};

const mutations = {
  stopPlay(state) {
    state.play = false;
  },
  reloadState(state) {
    state.reload += 1;
  },
};

export default {
  namespaced: true,
  actions,
  getters,
  mutations,
  state,
};
