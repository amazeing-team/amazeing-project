import { mount, shallowMount } from '@vue/test-utils';
import Vuex from 'vuex';
import Vuetify from 'vuetify';
import Vue from 'vue';
import NewRoomDialog from '../components/NewRoomDialog.vue';
import fakeStore from './fakeStore';
import de from 'vuetify/es5/locale/de';
import VueRouter from 'vue-router';

describe('NewRoomDialog', () => {
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

  it('clicking on Create Room closes window and emits submit', () => {
    const wrapper = mount(NewRoomDialog, {
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

    wrapper.find('button').trigger('click');
    expect(wrapper.vm.$data.dialog).toBe(false);
    expect(wrapper.emitted('submit'));
  });

});
