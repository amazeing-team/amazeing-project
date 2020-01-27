import { Color } from '../enums';
import sampleTasks from './tasks';
import { exampleRoom, tournamentRoom } from './rooms'

let gameActions;
let gameGetters;
let tasksMutations;
let setStore;
let roomsActions;
let storeArg;
let taskGetters;
let gameMutations;
let testVars;
let tournamentActions;
let tournamentsActions;
let tournamentMutations;

tournamentMutations = {
  stopPlay() {
    ;
  },
};

testVars = {
  uName: 'initial',
  roomReadyIds: [],
};

tournamentsActions = {
  joinTournament: jest.fn(),
};

tournamentActions = {
  tournamentAction: jest.fn(),
};

gameActions = {
  register: jest.fn(),
  chooseTask: jest.fn(),
  sendInstructions: jest.fn(),
  runInstructions: jest.fn(),
  shutdown: jest.fn(),
  nextStep: jest.fn(),
  resetStep: jest.fn(),
  turnLeft: jest.fn(),
  turnRight: jest.fn(),
  move: jest.fn(),
  pause: jest.fn(),
  reset: jest.fn(),
  updateUser: jest.fn(),
};

const userActions = {
  saveOwlColor: jest.fn(),
  saveUserName: ({ commit }, { wm, value }) => testVars.uName = value,
};

roomsActions = {
  register: jest.fn(),
  createRoom: jest.fn(),
  joinRoom: jest.fn(),
  reset: jest.fn(),
  readyRoom(ctx, { id }) {
    testVars.roomReadyIds.push(id);
  },
};

gameGetters = {
  hasNextStep: () => false,
};

taskGetters = {
  getTaskById(state) {
    return id => state.tasks.find(task => task.id === id);
  },
};

tasksMutations = {
  generatePlaygroundMap: jest.fn(),
};

gameMutations = {
  setWin(state, win) {
    ;
  },
};

storeArg = {
  rooms: {

  },
  modules: {
    game: {
      namespaced: true,
      state: {
        log: [],
        players: [],
        valid: true,
        win: false,
      },
      actions: gameActions,
      getters: gameGetters,
      mutations: gameMutations,
    },
    tasks: {
      namespaced: true,
      state: {
        loaded: true,
        code: '',
        playground: {
          code: '',
          tiles: [],
        },
        tasks: sampleTasks,
      },
      mutations: tasksMutations,
      getters: taskGetters,
    },
    user: {
      namespaced: true,
      state: {
        id: 0,
        name: 'Default username',
        taskSolved: false,
        owlColor: Color.BLUE,
      },
      actions: userActions,
    },
    rooms: {
      namespaced: true,
      state: {
        rooms: [exampleRoom],
        id: 1,
        win: false,
        winner: 'none',
      },
      actions: roomsActions,
    },
    settings: {
      state: {
        animationSpeed: 1,
        keyboardLayout: 'QWERTZ',
      },
    },
    tournaments: {
      namespaced: true,
      state: {
        tournaments: [tournamentRoom],
      },
      actions: tournamentsActions,
    },
    tournament: {
      namespaced: true,
      state: {
        tournament: {
          round: 0,
          users: true,
        },
        loaded: false,
        play: false,
      },
      actions: tournamentActions,
      mutations: tournamentMutations,
    },
  },
};

setStore = Vuex => new Vuex.Store(storeArg);

export default {
  gameActions,
  gameGetters,
  tasksMutations,
  roomsActions,
  setStore,
  userActions,
  testVars,
  tournamentActions,
  tournamentsActions
};

export { storeArg };
