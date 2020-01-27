import Vue from 'vue';
import { Direction } from '../../enums';

const state = {
  currentStep: 0,
  allData: null,
  initialState: null,
  id: 0,
  log: [],
  msg: '',
  valid: true,
  win: false,
  players: {
    0: {
      coordinate: {
        second: 1,
        first: 1,
      },
      direction: 'NORTH',
      health: 10,
    },
  },
  boundary: {
    second: { second: 4, first: 4 },
    first: { second: 0, first: 0 },
  },
  exits: [
    {
      second: 4,
      first: 4,
    },
  ],
  start: {
    second: 0,
    first: 0,
  },
  tiles: [],
  renderBoard: true, // tick to rerender the board
};

const getters = {
  hasNextStep(state) {
    if (state.allData == null) {
      return false;
    }
    return state.currentStep < state.allData.length;
  },
};

const actions = {
  render({ state }) {
    state.renderBoard = !state.renderBoard;
  },
  register({ rootState }) {
    Vue.prototype.$socket.sendObj({
      type: 'REGISTER',
      name: rootState.user.name,
      color: rootState.user.owlColor,
    });
  },
  updateUser({ rootState }) {
    Vue.prototype.$socket.sendObj({
      type: 'UPDATE_USER',
      name: rootState.user.name,
      color: rootState.user.owlColor,
    });
  },
  chooseTask(ctx, { task }) {
    // console.log('chooseTask', task);
    Vue.prototype.$socket.sendObj({
      type: 'INIT',
      json_map: task.json_map,
      health: task.health,
      direction: Direction.NORTH,
    });
  },
  turnLeft({ state }) {
    Vue.prototype.$socket.sendObj({
      type: 'TURN_LEFT',
    });
    state.renderBoard = !state.renderBoard;
  },
  turnRight({ state }) {
    Vue.prototype.$socket.sendObj({
      type: 'TURN_RIGHT',
    });
    state.renderBoard = !state.renderBoard;
  },
  move({ state }) {
    Vue.prototype.$socket.sendObj({
      type: 'MOVE',
    });
    state.renderBoard = !state.renderBoard;
  },
  sendInstructions(ctx, { instructions, taskID }) {
    Vue.prototype.$socket.sendObj({
      type: 'TASK',
      instructions,
      taskID,
    });
  },
  shutdown() {
    Vue.prototype.$socket.sendObj({
      type: 'SHUTDOWN',
    });
  },
  setStep({ state, dispatch }) {
    if (state.currentStep === 0 && state.initialState !== null) {
      Object.entries(state.initialState).forEach(([key, entry]) => {
        state[key] = entry;
      });
    } else if (state.allData !== null && state.allData.length > state.currentStep - 1) {
      Object.entries(state.allData[state.currentStep - 1]).forEach(([key, entry]) => {
        state[key] = entry;
      });
    }
    dispatch('render');
  },
  nextStep({ getters, commit, dispatch }) {
    if (getters.hasNextStep) {
      commit('incrementStep');
      dispatch('setStep');
    } else {
      console.warn('Call nextStep when !hasNextStep');
    }
  },
  resetStep({ commit, dispatch }) {
    commit('resetStep');
    dispatch('setStep');
  },
};

const mutations = {
  incrementStep(state) {
    state.currentStep += 1;
  },
  resetStep(state) {
    state.currentStep = 0;
  },
  setWin(state, win) {
    state.win = win;
  },
};

export default {
  namespaced: true,
  state,
  getters,
  actions,
  mutations,
};
