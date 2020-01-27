import { mount, shallowMount } from '@vue/test-utils';
import Vuex from 'vuex';
import Vuetify from 'vuetify';
import Vue from 'vue';
import TournamentsAdmin from '../components/TournamentsAdmin.vue';
import fakeStore from './fakeStore';
import { Color } from '../enums';

describe('TournamentsAdmin', () => {
  let store;

  beforeEach(() => {
    Vue.use(Vuex);
    Vue.use(Vuetify);

    store = fakeStore.setStore(Vuex);
  });

  it('Color convertion works', () => {
    const wrapper = shallowMount(TournamentsAdmin, {
      store,
      mocks: {
        $t: () => "",
        $router: {
          push: () => {},
        },
      },
      
    });

    wrapper.vm.colorToImageLocal(Color.BLUE); //TODO
  });
});
