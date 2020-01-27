import { mount, shallowMount, createLocalVue } from '@vue/test-utils';
import Vuex from 'vuex';
import Vuetify from 'vuetify';
import Vue from 'vue';
import Settings from '../components/Settings.vue';
import fakeStore from './fakeStore';
import 'babel-polyfill';

describe('Settings', () => {
  let store;

  beforeEach(() => {
    Vue.use(Vuex);
    Vue.use(Vuetify);

    store = fakeStore.setStore(Vuex);
  });

  it('task will be loaded', async () => {
    Vue.config.silent = true;
    const wrapper = shallowMount(Settings, {
      store,
    });

    /* TODO: deperecated?
    console.log(wrapper.vm.$store.state.settings.keyboardLayout);
    console.log(wrapper.find('[label="QWERTY"]').html());
    let w2 = wrapper.find('[label="QWERTY"]');
    console.log(w2.element.checked);
    w2.element.checked = true;
    w2.trigger('change');
    await wrapper.vm.$nextTick();
    console.log(wrapper.vm.$store.state.settings.keyboardLayout); */
  });
});
