import KeyboardLayout from '../../enums';

const state = {
  animationSpeed: 10, // min 1, max 10
  keyboardLayout: KeyboardLayout.QWERTY,
};

const getters = {};

const actions = {};

const mutations = {
  setAnimationSpeed(state, animationSpeed) {
    state.animationSpeed = animationSpeed;
  },
  setKeyboardLayout(state, keyboardLayout) {
    state.keyboardLayout = keyboardLayout;
  },
};

export default {
  namespaced: true,
  state,
  getters,
  actions,
  mutations,
};
