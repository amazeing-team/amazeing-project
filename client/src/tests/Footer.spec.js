import { mount, shallowMount, createLocalVue } from '@vue/test-utils';
import Vuex from 'vuex';
import VueCookie from 'vue-cookie';
import Vuetify from 'vuetify';
import Vue from 'vue';
import Playground from '../components/Playground.vue';
import fakeStore from './fakeStore';
import 'babel-polyfill';

describe('Playground', () => {
  let store;

  beforeEach(() => {
    Vue.use(Vuex);
    Vue.use(Vuetify);
    Vue.use(VueCookie);

    store = fakeStore.setStore(Vuex);
  });

  it('task will be loaded', () => {
    Vue.config.silent = true;
    const wrapper = shallowMount(Playground, {
      store,
      propsData: {
        editor: false,
      },
      mocks: {
        $cookie: {
          get: () => '',
        },
        $t: () => "",
      },
    });

    expect(wrapper.vm.loadedTask).toBe(true);
    Vue.config.silent = false;
  });
});
