import { mount, shallowMount } from '@vue/test-utils';
import Vuex from 'vuex';
import Vuetify from 'vuetify';
import Vue from 'vue';
import NewTournamentDialog from '../components/NewTournamentDialog.vue';
import fakeStore from './fakeStore';
import de from 'vuetify/es5/locale/de';
import VueRouter from 'vue-router';

describe('NewTournamentDialog', () => {
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

    store = fakeStore.setStore(Vuex);
  });

  it('clicking on Create Tournament closes window and emits submit', () => {
    const wrapper = shallowMount(NewTournamentDialog, {
      store,
      vuetify: vuetifyLocal,
      propsData: {
        inputData: {
          name: 'pretty_long_name',
          widt: 200,
          height: 100,
        },
      },
      mocks: {
        $t: () => {},
      },
    });

  });

});
