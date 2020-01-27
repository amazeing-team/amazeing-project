import { mount, createLocalVue, shallowMount } from '@vue/test-utils';
import Vuetify from 'vuetify';
import Vue from 'vue';
import Vuex from 'vuex';
import VueCookie from 'vue-cookie';
import ServerSelect from '../components/ServerSelect.vue';
import fakeStore from './fakeStore';

Vue.config.silent = true;
Vue.prototype.$vuetify = { rtl: false };

Vue.use(Vuetify);
Vue.use(Vuex);

describe('ServerSelect', () => {
  let store;

  beforeEach(() => {
    Vue.use(Vuex);
    Vue.use(Vuetify);
    Vue.use(VueCookie);

    store = fakeStore.setStore(Vuex);
  });

  it('Do not fail when executing basic instructions. (webSocket false)', () => {
    const wrapper = shallowMount(ServerSelect, {
      Vue,
      sync: false,
      store,
      propsData: { vueWebsocket: false },
      mocks: {
        $t: () => "",
      },
    });
    wrapper.vm.reconnect();
  });

  it('Do not fail when executing basic instructions. (webSocket true)', () => {
    const wrapper = shallowMount(ServerSelect, {
      Vue,
      sync: false,
      store,
      propsData: { vueWebsocket: true },
      mocks: {
        $disconnect: () => {},
        $connect: () => {},
        $t: () => "",
      },
    });
    wrapper.vm.reconnect();
  });
});
