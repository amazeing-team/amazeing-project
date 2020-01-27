import { mount, shallowMount, createLocalVue } from '@vue/test-utils';
import Vuex from 'vuex';
import Vuetify from 'vuetify';
import Vue from 'vue';
import TileEditor from '../components/TileEditor.vue';
import fakeStore from './fakeStore';
import 'babel-polyfill';

describe('TileEditor', () => {
  let store;

  beforeEach(() => {
    Vue.use(Vuex);
    Vue.use(Vuetify);

    store = fakeStore.setStore(Vuex);
  });

  it('task will be loaded', () => {
    Vue.config.silent = true;
    const wrapper = shallowMount(TileEditor, {
      store,
      propsData: {
        tile: [{ directions: 0 }, { directions: { indexOf: () => 0 } }],
        start: [],
        exits: [],
      },
    });

    // TODO: make more sense of the things below
    
    wrapper.vm.toggleDirection(0);
    wrapper.vm.toggleFieldType();
    wrapper.vm.isStart = true;
    wrapper.vm.toggleFieldType();
    wrapper.vm.isStart = false;
    wrapper.vm.isEnd = true;
    wrapper.vm.toggleFieldType();
  });
});
