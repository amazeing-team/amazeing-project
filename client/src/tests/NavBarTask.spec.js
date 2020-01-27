import { mount, shallowMount, createLocalVue } from '@vue/test-utils';
import Vuex from 'vuex';
import VueCookie from 'vue-cookie';
import Vuetify from 'vuetify';
import Vue from 'vue';
import NavBarTask from '../components/NavBarTask.vue';
import fakeStore from './fakeStore';
import 'babel-polyfill';

describe('NavBarTask', () => {
  let store;

  beforeEach(() => {
    Vue.use(Vuex);
    Vue.use(Vuetify);
    Vue.use(VueCookie);

    store = fakeStore.setStore(Vuex);
  });

  it('check beforeRoute hook', async () => { //TODO
    const wrapper = shallowMount(NavBarTask, {
      store,
      stubs: ['router-link', 'router-view'],
      vuetify: new Vuetify({}),
      propsData: {
        editor: false,
      },
      mocks: {
        $route: {
          params: {
            id: "0",
          },
        },
        $t: () => "",
      },
    });

    wrapper.vm.$options.beforeRouteUpdate.call(wrapper.vm, "x", "y", () => {});
  });

});
