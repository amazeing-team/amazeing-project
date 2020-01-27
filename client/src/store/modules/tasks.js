import Axios from 'axios';

const state = {
  tasks: [],
  playground: {
    code: '',
    json_map: {},
    id: -1,
    name: 'Playground',
  },
  loaded: false,
};

const getters = {
  getTaskById(state) {
    return id => state.tasks.find(task => task.id === id);
  },
};

const actions = {
  /**
   * Connect to the admin server and get the available task
   * @param commit
   * @param state
   * @return {Promise<void>}
   */
  async loadTasks({ commit, state }) {
    Axios.get('http://localhost:8000/api/tasks').then(({ data }) => {
      commit('setTasks', data.map((task, index) => {
        // eslint-disable-next-line no-param-reassign
        task.json_map = JSON.parse(task.json_map);
        // eslint-disable-next-line no-param-reassign
        task.id = index;
        return task;
      }));
    }).catch(() => {
      state.loaded = true;
    });
  },
};

const mutations = {
  setTasks(state, tasks) {
    state.tasks = tasks;
    state.loaded = true;
  },
  generatePlaygroundMap(state) {
    state.playground.health = 999999999;
    // eslint-disable-next-line camelcase
    state.playground.json_map = {
      boundary: {
        first: {
          first: 0,
          second: 0,
        },
        second: {
          first: 4,
          second: 4,
        },
      },
      exits: [
        {
          first: 3,
          second: 3,
        },
      ],
      start: {
        first: 1,
        second: 1,
      },
      tiles: [
        [
          {
            first: 2,
            second: 1,
          },
          {
            directions: [
              'NORTH',
              'WEST',
              'EAST',
              'SOUTH',
            ],
          },
        ],
        [
          {
            first: 3,
            second: 2,
          },
          {
            directions: [
              'NORTH',
              'WEST',
              'EAST',
              'SOUTH',
            ],
          },
        ],
        [
          {
            first: 1,
            second: 0,
          },
          {
            directions: [
              'SOUTH',
              'EAST',
              'WEST',
            ],
          },
        ],
        [
          {
            first: 4,
            second: 3,
          },
          {
            directions: [
              'SOUTH',
              'NORTH',
              'WEST',
            ],
          },
        ],
        [
          {
            first: 4,
            second: 4,
          },
          {
            directions: [
              'WEST',
              'NORTH',
            ],
          },
        ],
        [
          {
            first: 1,
            second: 1,
          },
          {
            directions: [
              'NORTH',
              'WEST',
              'EAST',
              'SOUTH',
            ],
          },
        ],
        [
          {
            first: 2,
            second: 2,
          },
          {
            directions: [
              'NORTH',
              'WEST',
              'EAST',
              'SOUTH',
            ],
          },
        ],
        [
          {
            first: 3,
            second: 3,
          },
          {
            directions: [
              'NORTH',
              'WEST',
              'EAST',
              'SOUTH',
            ],
          },
        ],
        [
          {
            first: 0,
            second: 0,
          },
          {
            directions: [
              'SOUTH',
              'EAST',
            ],
          },
        ],
        [
          {
            first: 1,
            second: 2,
          },
          {
            directions: [
              'NORTH',
              'WEST',
              'EAST',
              'SOUTH',
            ],
          },
        ],
        [
          {
            first: 3,
            second: 4,
          },
          {
            directions: [
              'EAST',
              'NORTH',
              'WEST',
            ],
          },
        ],
        [
          {
            first: 2,
            second: 3,
          },
          {
            directions: [
              'NORTH',
              'WEST',
              'EAST',
              'SOUTH',
            ],
          },
        ],
        [
          {
            first: 0,
            second: 1,
          },
          {
            directions: [
              'EAST',
              'NORTH',
              'SOUTH',
            ],
          },
        ],
        [
          {
            first: 0,
            second: 2,
          },
          {
            directions: [
              'EAST',
              'NORTH',
              'SOUTH',
            ],
          },
        ],
        [
          {
            first: 1,
            second: 3,
          },
          {
            directions: [
              'NORTH',
              'WEST',
              'EAST',
              'SOUTH',
            ],
          },
        ],
        [
          {
            first: 2,
            second: 4,
          },
          {
            directions: [
              'EAST',
              'NORTH',
              'WEST',
            ],
          },
        ],
        [
          {
            first: 1,
            second: 4,
          },
          {
            directions: [
              'EAST',
              'NORTH',
              'WEST',
            ],
          },
        ],
        [
          {
            first: 0,
            second: 3,
          },
          {
            directions: [
              'EAST',
              'NORTH',
              'SOUTH',
            ],
          },
        ],
        [
          {
            first: 0,
            second: 4,
          },
          {
            directions: [
              'EAST',
              'NORTH',
            ],
          },
        ],
        [
          {
            first: 4,
            second: 0,
          },
          {
            directions: [
              'SOUTH',
              'WEST',
            ],
          },
        ],
        [
          {
            first: 4,
            second: 1,
          },
          {
            directions: [
              'SOUTH',
              'NORTH',
              'WEST',
            ],
          },
        ],
        [
          {
            first: 3,
            second: 0,
          },
          {
            directions: [
              'SOUTH',
              'EAST',
              'WEST',
            ],
          },
        ],
        [
          {
            first: 4,
            second: 2,
          },
          {
            directions: [
              'SOUTH',
              'NORTH',
              'WEST',
            ],
          },
        ],
        [
          {
            first: 3,
            second: 1,
          },
          {
            directions: [
              'NORTH',
              'WEST',
              'EAST',
              'SOUTH',
            ],
          },
        ],
        [
          {
            first: 2,
            second: 0,
          },
          {
            directions: [
              'SOUTH',
              'EAST',
              'WEST',
            ],
          },
        ],
      ],
    };

    state.playground.code = '';
    // eslint-disable-next-line camelcase
  },
};

export default {
  namespaced: true,
  state,
  getters,
  actions,
  mutations,
};
