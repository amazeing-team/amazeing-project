import { mount, shallowMount, spyOn } from '@vue/test-utils';
import Vuex from 'vuex';
import Vuetify from 'vuetify';
import Vue from 'vue';
import Room from '../components/Room.vue';
import fakeStore from './fakeStore';
import de from 'vuetify/es5/locale/de';
import VueRouter from 'vue-router';

describe('Room', () => {
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

  it('Roomname is displayed', () => {
    const wrapper = mount(Room, {
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

    expect(wrapper.text()).toContain('sample_room');
  });

  it('Player can be marked as ready', () => {
    const wrapper = mount(Room, {
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
    expect(fakeStore.testVars.roomReadyIds).toContain(0);
  });

});
