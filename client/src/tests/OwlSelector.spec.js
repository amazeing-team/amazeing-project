import { mount, createLocalVue, shallowMount } from '@vue/test-utils';
import Vuex from 'vuex';
import Vuetify from 'vuetify';
import VueRouter from 'vue-router';
import Vue from 'vue';
import router from '../router/index';
import fakeStore from './fakeStore';
import 'babel-polyfill';
import de from 'vuetify/es5/locale/de';
import OwlSelector from '../components/OwlSelector.vue';

describe('OwlSelector', () => {
  let store;
  let vuetifyLocal;

  beforeEach(() => {
    Vue.use(Vuex);
    Vue.use(Vuetify);

    vuetifyLocal = new Vuetify({
      lang: {
        locales: { de },
        current: 'de',
      },
      icons: {
        iconfont: 'mdi',
      },
    });

    Vue.use(VueRouter);

    store = fakeStore.setStore(Vuex);
  });

  it('mount works', () => {
    mount(OwlSelector, { store, router, vuetify: vuetifyLocal });
  });
});
