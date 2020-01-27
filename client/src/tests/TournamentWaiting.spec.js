import { mount, shallowMount } from '@vue/test-utils';
import Vuex from 'vuex';
import Vuetify from 'vuetify';
import Vue from 'vue';
import TournamentWaiting from '../components/TournamentWaiting.vue';
import fakeStore from './fakeStore';
import { Color } from '../enums';

describe('TournamentWaiting', () => {
  let store;

  beforeEach(() => {
    Vue.use(Vuex);
    Vue.use(Vuetify);

    store = fakeStore.setStore(Vuex);
  });

  it('Color convertion works', () => {
    const wrapper = shallowMount(TournamentWaiting, {
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

   
  it('Routing hooks do not malfunction', () => {
    const wrapper = shallowMount(TournamentWaiting, {
      store,
      mocks: {
        $t: () => "",
        $router: {
          push: () => {},
        },
      },
      
    });

    wrapper.vm.$options.beforeRouteUpdate.call(wrapper.vm, "x", "y", () => {});
    wrapper.vm.$options.beforeRouteEnter.call(wrapper.vm, "x", "y", (f) => {f(wrapper.vm)});
  });

});
