<template>
  <div class="tile">
    <div class="tile-content" :class="{start: this.isStart, end: this.isEnd }"
         @click="toggleFieldType() "/>
    <div class="tile-west" :class="{ active: this.wallWest }"
         @click="toggleDirection(Direction.WEST)" />
    <div class="tile-east" :class="{ active: this.wallEast }"
         @click="toggleDirection(Direction.EAST)" />
    <div class="tile-north" :class="{ active: this.wallNorth }"
         @click="toggleDirection(Direction.NORTH)" />
    <div class="tile-south" :class="{ active: this.wallSouth }"
         @click="toggleDirection(Direction.SOUTH)" />
  </div>
</template>

<script>
import { Direction } from '../enums';

export default {
  name: 'TileEditor',
  props: ['tile', 'start', 'exits'],
  data() {
    return {
      isStart: false,
      isEnd: false,
      Direction,
    };
  },
  computed: {
    wallNorth() {
      return this.tile[1].directions.indexOf(Direction.NORTH) === -1;
    },
    wallSouth() {
      return this.tile[1].directions.indexOf(Direction.SOUTH) === -1;
    },
    wallEast() {
      return this.tile[1].directions.indexOf(Direction.EAST) === -1;
    },
    wallWest() {
      return this.tile[1].directions.indexOf(Direction.WEST) === -1;
    },
  },
  methods: {
    /**
     * Toggle if direction is allowed in this tile
     * @param {Direction} direction
     */
    toggleDirection(direction) {
      this.$emit('toggle-direction', direction);
    },
    toggleFieldType() {
      if (this.isStart) {
        this.isStart = false;
        this.isEnd = true;
      } else if (this.isEnd) {
        this.isEnd = false;
      } else {
        this.isStart = true;
      }
      this.$emit('toggle-field-type', {
        isStart: this.isStart,
        isEnd: this.isEnd,
      });
    },
  },
  mounted() {
    this.isStart = this.start.find(x => x.first == this.tile[0].first
     && x.second == this.tile[0].second) != undefined;

    this.isEnd = this.exits.find(x => x.first == this.tile[0].first
     && x.second == this.tile[0].second) != undefined;
  },
};
</script>

<style scoped>
.tile {
  width: 50px;
  height: 50px;
  position: relative;
}

.tile div {
  position: absolute;
}
.tile div.active {
  background-color: black;
}

.tile div:not(.active):not(.tile-content) {
  background-color: gray;
}

div.tile-content {
  width: 30px;
  height: 30px;
  top: 10px;
  left: 10px;
}

div.start {
  background-color: green;
}

div.end {
  background-color: red;
}

.tile-east, .tile-west {
  width: 10px;
  height: 50px;
}

.tile-east {
  right: 0;
}

.tile-west {
  left: 0;
}

.tile-north, .tile-south {
  height: 10px;
  width: 50px;
}

.tile-north {
  top: 0;
}

.tile-south {
  bottom: 0;
}
</style>
