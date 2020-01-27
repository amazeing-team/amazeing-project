import { mount, shallowMount, spyOn } from '@vue/test-utils';
import Vuex from 'vuex';
import Vuetify from 'vuetify';
import Vue from 'vue';
import Tournament from '../components/Tournament.vue';
import { storeArg } from './fakeStore';
import de from 'vuetify/es5/locale/de';
import VueRouter from 'vue-router';
import { tournamentRoom } from './rooms';

describe('Tournament', () => {
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

    storeArg.modules.rooms.state.rooms = [tournamentRoom];

    store = new Vuex.Store(storeArg);
  });

  it('Player can be marked as ready', () => {
    const wrapper = mount(Tournament, {
      store,
      vuetify: vuetifyLocal,
      mocks: {
        $t: () => {},
        $route: {
          params: {
              id: "0"
          }
        },
      },
    });

    wrapper.find('button').trigger('click');
  });

});
