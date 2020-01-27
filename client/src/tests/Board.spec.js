import { mount, createLocalVue } from '@vue/test-utils';
import Vuex from 'vuex';
import Board from '../components/Board.vue';
import { storeArg } from './fakeStore';
import 'jest-canvas-mock';
import 'babel-polyfill';
import states from './gameStates';

const localVue = createLocalVue();
localVue.use(Vuex);

describe('Board', () => {
  let store;
  let imgSave;

  beforeAll(() => {
    imgSave = global.Image.prototype;
    Object.defineProperty(global.Image.prototype, 'src', {
      set(src) {
        const load_event = document.createEvent('Event');
        load_event.initEvent('load', false, false);
        this.dispatchEvent(load_event);
      },
    });
  });

  afterAll(() => {
    Object.defineProperty(global.Image.prototype, imgSave);
  });

  beforeEach(() => {
    storeArg.modules.game.state = states[0];
    store = new Vuex.Store(storeArg);
  });

  it('has a canvas', async () => {
    // Now mount the component and you have the wrapper
    const wrapper = mount(Board, { 
      store,
      localVue,
      mocks: {
        $t: () => "",
      },
    });

    await wrapper.vm.$nextTick();

    expect(wrapper.contains('canvas')).toBe(true);
  });

  it('resizing with + works', async () => { // TODO: -
    // Now mount the component and you have the wrapper
    const wrapper = mount(Board, { 
      store,
      localVue,
      mocks: {
        $t: () => "",
      },
    });

    await wrapper.vm.$nextTick();

    // TODO
  });
});
