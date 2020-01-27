import { mount, createLocalVue, shallowMount } from '@vue/test-utils';
import Vuex from 'vuex';
import Vuetify from 'vuetify';
import Vue from 'vue';
import fakeStore from './fakeStore';
import 'babel-polyfill';
import G from 'generatorics';
import ControlButtons from '../components/ControlButtons.vue';

describe('ControlButtons', () => {
  let store;

  beforeEach(() => {
    Vue.use(Vuex);
    Vue.use(Vuetify);

    store = fakeStore.setStore(Vuex);
  });

  it('clicking on send instruction should emit send-instructions', () => {
    const wrapper = mount(ControlButtons, {
        mocks: {
            $t: () => "",
        },
    });

    wrapper.find('#send-instructions-btn').trigger('click');
    expect(wrapper.emitted('send-instructions'));
  });

  it('clicking on the step button should emit a step event', async () => {
    const wrapper = mount(ControlButtons, {
      propsData: {
        hasNextStep: true,
        errorShow: false,
        instructions_sent: true,
        runing: false,
      },
      mocks: {
        $t: () => "",
      },
    });

    wrapper.find('#step-btn').trigger('click');
    expect(wrapper.emitted('step'));
  });

  it('clicking on run should emit play', async () => {
    const wrapper = mount(ControlButtons, {
      propsData: {
        hasNextStep: true,
        errorShow: false,
        instructions_sent: true,
        runing: false,
      },
      mocks: {
        $t: () => "",
      },
    });

    wrapper.find('#run-instructions-btn').trigger('click');
    expect(wrapper.emitted('play'));
  });

  it('clicking on pause emits pause', async () => {
    const wrapper = mount(ControlButtons, {
      propsData: {
        hasNextStep: true,
        errorShow: false,
        instructions_sent: true,
        runing: true,
      },
      mocks: {
        $t: () => "",
      },
    });

    wrapper.find('#pause-btn').trigger('click');
    expect(wrapper.emitted('pause'));
  });

  it('reset button emits reset', async () => {
    const wrapper = mount(ControlButtons, {
      propsData: {
        hasNextStep: true,
        errorShow: false,
        instructions_sent: true,
        runing: true,
      },
      mocks: {
        $t: () => "",
      },
    });

    wrapper.find('#reset-game-state-btn').trigger('click');
    expect(wrapper.emitted('reset'));
  });

  it('try different configurations not to be faulty', async () => {
    const states = G.baseN([true, false], 4);

    for (const state of states) {
      const wrapper = mount(ControlButtons, {
        propsData: {
          hasNextStep: state[0],
          errorShow: state[1],
          instructions_sent: state[2],
          runing: state[3],
        },
        mocks: {
            $t: () => "",
          },
      });

      // TODO: make more sense of the things below
      ['#reset-game-state-btn', '#pause-btn', '#run-instructions-btn',
        '#step-btn', '#send-instructions-btn'].forEach((id) => {
        expect(wrapper.find(id));
      });
    }
  });
});
