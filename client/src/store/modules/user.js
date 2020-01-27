import { Color } from '../../enums';

const state = {
  id: -1,
  name: navigator.language === 'de' ? 'Heribert Schnösel' : 'John Doe',
  taskSolved: 0,
  owlColor: Color.BLUE,
};

const getters = {
  tasksWithStatus(state, getters, rootState) {
    return rootState.tasks.tasks.map((task) => {
      const updatedTask = task;
      console.log(task.id, state.taskSolved + 1, task.id > (state.taskSolved + 1));
      updatedTask.locked = task.id > (state.taskSolved + 1);
      updatedTask.done = task.id <= state.taskSolved;
      return updatedTask;
    });
  },
  name: state => state.name,
};

const actions = {
  complete({ commit }, { taskSolvedId, wm }) {
    if (taskSolvedId > state.taskSolved) {
      commit('setTaskSolved', taskSolvedId);
      wm.$cookie.set('task-solved', taskSolvedId);
    }
  },
  loadUserName({ state }, { wm }) {
    const username = wm.$cookie.get('username');
    if (username) {
      state.name = username;
    }
  },
  loadTaskSolved({ state }, { wm }) {
    const taskSolved = parseInt(wm.$cookie.get('task-solved'), 10);
    if (taskSolved) {
      state.taskSolved = taskSolved;
    }
  },
  saveUserName({ commit }, { wm, value }) {
    commit('setUsername', value);
    wm.$cookie.set('username', value);
  },
  loadOwlColor({ state }, { wm }) {
    const color = wm.$cookie.get('color');
    if (color) {
      state.owlColor = color;
    }
  },
  saveOwlColor({ commit }, { wm, value }) {
    commit('setOwlColor', value);
    wm.$cookie.set('color', value);
  },
};

const mutations = {
  setTaskSolved(state, taskSolved) {
    state.taskSolved = taskSolved;
  },
  setUsername(state, name) {
    state.name = name;
  },
  setOwlColor(state, color) {
    state.owlColor = color;
  },
};

export default {
  namespaced: true,
  state,
  getters,
  actions,
  mutations,
};
