import { mount, shallowMount, createLocalVue } from '@vue/test-utils';
import Vuex from 'vuex';
import VueCookie from 'vue-cookie';
import Vuetify from 'vuetify';
import Vue from 'vue';
import Task from '../components/Task.vue';
import fakeStore from './fakeStore';
import 'babel-polyfill';

describe('Task', () => {
  let store;

  beforeEach(() => {
    Vue.use(Vuex);
    Vue.use(Vuetify);
    Vue.use(VueCookie);

    store = fakeStore.setStore(Vuex);
  });

  it('routing hooks work', async () => {
    let $route = {
        params: {
            id: "0"
        }
    };
    const wrapper = shallowMount(Task, {
      store,
      vuetify: new Vuetify({}),
      propsData: {
        editor: false,
      },
      mocks: {
        $cookie: {
          get: () => '',
          set: () => {},
        },
        $route,
        $router: {
            push: () => {$route.params.id = "1";},
        },
        setWin: () => {},
        $t: () => "",
      },
    });

    wrapper.vm.$options.beforeRouteUpdate.call(wrapper.vm, "x", "y", () => {});
    wrapper.vm.$options.beforeRouteEnter.call(wrapper.vm, "x", "y", (f) => {f(wrapper.vm)});
  });

  it('routing hooks work', async () => {
    let $route = {
        params: {
            id: "0"
        }
    };
    const wrapper = shallowMount(Task, {
      store,
      vuetify: new Vuetify({}),
      propsData: {
        editor: false,
      },
      mocks: {
        $cookie: {
          get: () => '',
          set: () => {},
        },
        $route,
        $router: {
            push: () => {$route.params.id = "1";},
        },
        setWin: () => {},
        $t: () => "",
      },
    });

    wrapper.vm.next();
    wrapper.vm.updateCode(':)'); //TODO
  });
});
