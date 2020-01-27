import { mount } from '@vue/test-utils';
import Vuetify from 'vuetify';
import Vue from 'vue';
import MapEditor from '../components/MapEditor.vue';
import 'babel-polyfill';

describe('MapEditor', () => {
  beforeEach(() => {
    Vue.use(Vuetify);
  });

  it('clicking on a lot of tiles generates start fields', async () => {
    const wrapper = mount(MapEditor, {
        mocks: {
            $t: () => "",
        },
    });

    // wait until editor was loaded
    await wrapper.vm.$nextTick();

    // set values for map
    wrapper.find('#w-input').setValue(8);
    wrapper.find('#h-input').setValue(10);

    // rotate all tiles 4 times
    const clickOnAllTiles = () => {
      wrapper.findAll('.tile').wrappers.forEach((tile) => {
        const subComponents = tile.findAll('div');
        subComponents.trigger('click');
      });

      return wrapper.vm.$nextTick();
    };

    await clickOnAllTiles();
    await clickOnAllTiles();
    await clickOnAllTiles();
    await clickOnAllTiles();

    // assert that there are starts now
    // TODO
  });

  it('clipboard copy works', async () => {
    const wrapper = mount(MapEditor, {
        mocks: {
            $t: () => "",
        },
    });

    // wait until editor was loaded
    await wrapper.vm.$nextTick();

    // TODO
  });
});
