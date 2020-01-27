import { mount, shallowMount, createLocalVue } from '@vue/test-utils';
import Vuex from 'vuex';
import VueCookie from 'vue-cookie';
import Vuetify from 'vuetify';
import Vue from 'vue';
import TaskView from '../components/TaskView.vue';
import fakeStore from './fakeStore';
import 'babel-polyfill';

describe('TaskView', () => {
  let store;

  beforeEach(() => {
    Vue.use(Vuex);
    Vue.use(Vuetify);
    Vue.use(VueCookie);

    store = fakeStore.setStore(Vuex);
  });

  it('all funcs', async () => { //TODO
    let $route = {
        params: {
            id: "0"
        }
    };
    const wrapper = mount(TaskView, {
      store,
      vuetify: new Vuetify({}),
      propsData: {
        editor: false,
      },
    });

    wrapper.vm.onCodeChange('c');
    wrapper.vm.reset();
    wrapper.vm.sendInstructionsLocal('c');
    wrapper.vm.runInstructions();
    wrapper.vm.pause(); //TODO
  });

});
