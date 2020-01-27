import { mount, shallowMount } from '@vue/test-utils';
import Vuex from 'vuex';
import Vuetify from 'vuetify';
import Vue from 'vue';
import Lobby from '../components/Lobby.vue';
import fakeStore from './fakeStore';

describe('Lobby', () => {
  let store;

  beforeEach(() => {
    Vue.use(Vuex);
    Vue.use(Vuetify);

    store = fakeStore.setStore(Vuex);
  });

  it('lists rooms', () => {
    const wrapper = shallowMount(Lobby, {
      store,
      mocks: {
        $t: () => "",
      },
    });
    expect(wrapper.text()).toContain('Tournaments');
  });

  it('join room', () => {
    const wrapper = shallowMount(Lobby, {
      store,
      mocks: {
        $router: {
            push: () => {},
        },
        $t: () => "",
      },
    });
    
    wrapper.vm.join({id: 0}); //TODO
  });

  it('validate creates the room', () => {
    const wrapper = shallowMount(Lobby, {
      store,
      mocks: {
        $t: () => "",
      },
    });
    
    expect(fakeStore.roomsActions.createRoom).not.toHaveBeenCalled();
    wrapper.vm.validate();
    expect(fakeStore.roomsActions.createRoom).toHaveBeenCalled();
  });

});
