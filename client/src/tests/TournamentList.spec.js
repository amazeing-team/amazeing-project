import { mount, shallowMount } from '@vue/test-utils';
import Vuex from 'vuex';
import Vuetify from 'vuetify';
import Vue from 'vue';
import TournamentList from '../components/TournamentList.vue';
import fakeStore from './fakeStore';

describe('TournamentList', () => {
  let store;

  beforeEach(() => {
    Vue.use(Vuex);
    Vue.use(Vuetify);

    store = fakeStore.setStore(Vuex);
  });

  it('The user is asked to select a tournament', () => {
    const wrapper = shallowMount(TournamentList, {
      store,
      mocks: {
        $t: () => "",
      },
    });
    expect(wrapper.text()).toContain('Select a tournament');
  });

  it('Can join a tournament', () => {
    const wrapper = shallowMount(TournamentList, {
      store,
      mocks: {
        $t: () => "",
        $router: {
          push: () => {},
        },
      },
    });
    
    wrapper.vm.join({id: 42});
    expect(fakeStore.tournamentsActions.joinTournament).toHaveBeenCalled();
  });

});
