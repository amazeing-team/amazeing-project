import { mount, createLocalVue, shallowMount } from '@vue/test-utils';
import Vuetify from 'vuetify';
import Vue from 'vue';
import HelpDialog from '../components/HelpDialog.vue';

Vue.config.silent = true;
Vue.prototype.$vuetify = { rtl: false };

Vue.use(Vuetify);

describe('HelpDialog', () => {
  it('texts were set correctly', () => {
    const wrapper = shallowMount(HelpDialog, {
      Vue,
      sync: false,
      propsData: {
        title: 'title',
        helptext: 'htext',
        displayHelp: true,
      },
      mocks: {
        $t: () => "",
      },
    });

    expect(wrapper.text()).toContain('htext');
  });
});
