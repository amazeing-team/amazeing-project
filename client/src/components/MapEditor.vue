<template>
  <v-container v-if="loaded">
    <v-row>
      <v-form>
        <v-btn @click="loadInputFromJSON()" rounded block large>
          <v-icon  size="25" left color="green darken-3">cloud_upload</v-icon>
            {{ $t('mapeditor-load-json') }}
        </v-btn>
        <v-text-field id="w-input" type="number" @input="generateTiles()" min=4 :label="$t('width')" v-model="width_input"/>
        <v-text-field id="h-input" type="number" @input="generateTiles()" min=4 :label="$t('height')" v-model="height_input"/>
        <v-alert type=info>
          {{ $t('reset_warning') }}
        </v-alert>
        <div id="cb-copy" v-if="valid" v-on:click="copyToClipboard()">
          <v-alert type="success" transition=fade-transition>
          {{ $t('map_valid') }}
          </v-alert>
        </div>
          <v-alert v-else type="error">
            {{ errorMsg }}
          </v-alert>
      </v-form>
    </v-row>
    <v-row v-for="row in height" :key="`row-${row}`" >
      <span v-for="col in width" :key="`row-${row}-col-${col}`">
        <tile-editor :key="reload" :tile="getTile(col - 1, row - 1)"
                     @toggle-direction="toggleDirection(col - 1, row - 1, $event)"
                     @toggle-field-type="toggleFieldType(col - 1, row - 1, $event)"
                     :start="start"
                     :exits="exits" />
      </span>
    </v-row>
    <v-divider/>
    <v-row>
      <input ref="out" :value="output" />
    </v-row>
  </v-container>
</template>

<script>
import { Direction } from '../enums';
import TileEditor from './TileEditor.vue';

export default {
  name: 'MapEditor',
  components: { TileEditor },
  data() {
    return {
      loaded: false,
      reload: 1337, // initial value does not matter
      input: '',
      json_input: 'Insert JSON here',
      width_input: 4,
      height_input: 4,
      width: 4,
      height: 4,
      boundary: {
        first: { second: 0, first: 0 },
        second: { second: 0, first: 0 },
      },
      exits: [],
      start: [],
      tiles: [],
      numberStart: 0,
      numberEnd: 0,
      name: '',
      loading: false,
    };
  },
  computed: {
    errorMsg() {
      if (this.exits.length <= 0) {
        return this.$t('map_invalid_exit');
      }
      if (this.start.length !== 1) {
        return this.$t('map_invalid_start');
      }
      if (this.height < 4) {
        return this.$t('map_invalid_height');
      }
      if (this.width < 4) {
        return this.$t('map_invalid_width');
      }
      return null;
    },
    valid() {
      return this.errorMsg == null;
    },
    output() {
      if (!this.valid) {
        return this.$t('map_invalid');
      }
      return JSON.stringify({
        tiles: this.tiles,
        exits: this.exits,
        start: this.start[0],
        boundary: this.boundary,
        name: this.name,
      });
    },
  },
  methods: {
    /**
     * Generate tiles
     */
    generateTiles() {
      this.tiles = [];
      this.start = [];
      this.exits = [];
      [...Array(this.width).keys()].forEach((x) => {
        [...Array(this.height).keys()].forEach((y) => {
          this.tiles.push([
            {
              first: x,
              second: y,
            },
            {
              directions: [],
            },
          ]);
        });
      });
      this.loaded = true;
      // this.reload is used above as the key to the tiles. Changing it forces Vue to re-render.
      this.reload += 1;
    },
    /**
     * Get tiles for coordinate
     * @param {Integer} x
     * @param {Integer} y
     */
    getTile(x, y) {
      return this.tiles.find(tile => tile[0].first === x && tile[0].second === y);
    },
    /**
     * Toggle if moving to direction is allowed in the tile
     * @param {Object} tile
     * @param {Direction} direction
     */
    toggleDirectionTile(tile, direction) {
      const index = tile[1].directions.indexOf(direction);

      if (index === -1) {
        tile[1].directions.push(direction);
      } else {
        tile[1].directions.splice(index, 1);
      }
    },
    /**
     * Handler for toggleDirection in TileEditor
     * @param {Integer} x
     * @param {Integer} y
     * @param {Direction} direction
     */
    toggleDirection(x, y, direction) {
      this.toggleDirectionTile(this.getTile(x, y), direction);
      if (direction === Direction.NORTH) {
        const tile = this.getTile(x, y - 1);
        if (tile) {
          this.toggleDirectionTile(tile, Direction.SOUTH);
        }
        return;
      }
      if (direction === Direction.SOUTH) {
        const tile = this.getTile(x, y + 1);
        if (tile) {
          this.toggleDirectionTile(tile, Direction.NORTH);
        }
        return;
      }
      if (direction === Direction.EAST) {
        const tile = this.getTile(x + 1, y);
        if (tile) {
          this.toggleDirectionTile(tile, Direction.WEST);
        }
        return;
      }
      if (direction === Direction.WEST) {
        const tile = this.getTile(x - 1, y);
        if (tile) {
          this.toggleDirectionTile(tile, Direction.EAST);
        }
      }
    },
    toggleFieldType(x, y, { isStart, isEnd }) {
      if (isStart) {
        this.start.push({
          first: x,
          second: y,
        });
      } else {
        const start = this.start.find(field => field.first === x && field.second === y);
        if (start) {
          this.start.splice(this.start.indexOf(start));
        }
      }

      if (isEnd) {
        this.exits.push({
          first: x,
          second: y,
        });
      } else {
        const exit = this.exits.find(field => field.first === x && field.second === y);
        if (exit) {
          this.exits.splice(this.exits.indexOf(exit));
        }
      }
    },
    loadInputFromJSON() {
      const json = prompt(this.$t('mapeditor-insert-json'), '');
      if (json == null) {
        return;
      }
      try {
        const parsed = JSON.parse(json);
        this.loading = true;
        if (!('start' in parsed)) {
          throw new Error('No start field defined!');
        }
        if (!('exits' in parsed)) {
          throw new Error('No exit fields defined!');
        }
        if (!('tiles' in parsed)) {
          throw new Error('No tiles defined!');
        }
        if (!('boundary' in parsed)) {
          throw new Error('No boundary defined!');
        }
        this.width_input = parsed.boundary.second.first + 1;
        this.height_input = parsed.boundary.second.second + 1;
        this.start = [parsed.start];
        this.exits = parsed.exits;

        this.tiles = parsed.tiles;
        this.reload += 1;
        this.loaded = true;
        this.boundary = parsed.boundary;
        setTimeout(() => { this.loading = false; }, 100);
      } catch (e) {
        if (e instanceof SyntaxError) {
          alert(this.$t('mapeditor-input-syntaxerror'));
          console.log(e);
        } else {
          alert(e);
        }
      }
    },

    copyToClipboard() {
      const testingCodeToCopy = this.$refs.out;
      testingCodeToCopy.setAttribute('type', 'text');
      testingCodeToCopy.select();

      try {
        const successful = document.execCommand('copy');
        if (!successful) {
          alert('Oops, unable to copy');
        }
      } catch (err) {
        alert('Oops, unable to copy');
      }

      /* unselect the range */
      window.getSelection().removeAllRanges();
    },
  },
  watch: {
    width_input(newValue, oldValue) {
      if (newValue !== oldValue) {
        this.width = parseInt(newValue, 10);
        this.boundary.second.first = this.width - 1;
        if (!this.loading) {
          this.generateTiles();
        }
      }
    },
    height_input(newValue, oldValue) {
      if (newValue !== oldValue) {
        this.height = parseInt(newValue, 10);
        this.boundary.second.second = this.height - 1;
        if (!this.loading) {
          this.generateTiles();
        }
      }
    },
  },
  mounted() {
    this.boundary.second.second = this.height - 1;
    this.boundary.second.first = this.width - 1;
    this.generateTiles();
  },
};
</script>

<style scoped>

</style>
