import { mount, shallowMount } from '@vue/test-utils';
import Vuex from 'vuex';
import Vuetify from 'vuetify';
import Vue from 'vue';
import Home from '../components/Home.vue';
import fakeStore from './fakeStore';
import de from 'vuetify/es5/locale/de';
import router from '../router/index';
import VueRouter from 'vue-router';

describe('Home', () => {
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

  it('setting a new username works', () => {
    const wrapper = mount(Home, {
      store,
      router, 
      vuetify: vuetifyLocal,
      mocks: {
        $t: () => {},
      },
    });

    const user_name = "og_user";
    wrapper.find('input').setValue(user_name);
    expect(fakeStore.testVars.uName).toBe(user_name);
  });

});
