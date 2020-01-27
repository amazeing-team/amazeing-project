<template>
  <div style="position: relative;" ref="canvasParent">
    <canvas :height="canvas.height" :width="canvas.width" ref="canvas" style="width: 100%"></canvas>
    <div v-if="players && players[id]">{{ $t('health') }}: {{ players[id].health }}</div>
  </div>
</template>

<style>
  canvas {
    background-color: black;
  }
</style>

<script>
/* eslint no-param-reassign: ["error", { "props": false }] */
import { mapState } from 'vuex';
import { Color, Direction, Item } from '../enums';

import empty from '../assets/img/boden.png';
import wall from '../assets/img/wall.png';
import final from '../assets/img/schild.png';
import itembox from '../assets/img/itembox.png';
import marker from '../assets/img/marker.png';

import playerBlue from '../assets/img/owl-blue-front.png';
import playerBlueLeft from '../assets/img/owl-blue-left.png';
import playerBlueRight from '../assets/img/owl-blue-right.png';
import playerBlueBack from '../assets/img/owl-blue-back.png';

import playerPink from '../assets/img/owl-pink-front.png';
import playerPinkLeft from '../assets/img/owl-pink-left.png';
import playerPinkRight from '../assets/img/owl-pink-right.png';
import playerPinkBack from '../assets/img/owl-pink-back.png';

import playerOrange from '../assets/img/owl-orange-front.png';
import playerOrangeLeft from '../assets/img/owl-orange-left.png';
import playerOrangeRight from '../assets/img/owl-orange-right.png';
import playerOrangeBack from '../assets/img/owl-orange-back.png';

import playerPurple from '../assets/img/owl-purple-front.png';
import playerPurpleLeft from '../assets/img/owl-purple-left.png';
import playerPurpleRight from '../assets/img/owl-purple-right.png';
import playerPurpleBack from '../assets/img/owl-purple-back.png';

/**
 * Global variable !!!!!!
 */
const tileSize = 100;

/**
 * Class responsible to draw the map
 */
class MapRegion {
  /**
   * Constructor
   * @param {Image} imgEmpty
   * @param {Image} imgWall
   * @param {Image} imgFinal
   * @param {Image} imgCarrot
   * @param {Image} imgTeleport
   * @param {Image} imgGoggles
   */
  constructor(imgEmpty, imgWall, imgFinal, imgCarrot, imgTeleport, imgGoggles) {
    this.imgEmpty = imgEmpty;
    this.imgWall = imgWall;
    this.imgFinal = imgFinal;
    this.imgCarrot = imgCarrot;
    this.imgTeleport = imgTeleport;
    this.imgGoggles = imgGoggles;
  }

  /**
   * Draw map
   * @param {CanvasRenderingContext2D} ctx
   * @param {Array} tiles
   * @param {Object} boundary
   * @param {array} exits
   */
  draw(ctx, tiles, boundary, exits) {
    Object.values(tiles).forEach((tile) => {
      const x = tile[0].first;
      const y = tile[0].second;

      ctx.drawImage(this.imgEmpty, tileSize * x, tileSize * y, tileSize + 1, tileSize + 1);

      this.drawCornerTiles(ctx, x, y);

      const { directions } = tile[1];
      if (!directions.includes(Direction.NORTH) || y === 0) {
        this.drawInnerTile(ctx, x, y, 1, 0, this.imgWall);
      }
      if (!directions.includes(Direction.SOUTH) || y === boundary.second) {
        this.drawInnerTile(ctx, x, y, 1, 2, this.imgWall);
      }
      if (!directions.includes(Direction.WEST) || x === 0) {
        this.drawInnerTile(ctx, x, y, 0, 1, this.imgWall);
      }
      if (!directions.includes(Direction.EAST) || x === boundary.first) {
        this.drawInnerTile(ctx, x, y, 2, 1, this.imgWall);
      }

      if (directions.length === 0) {
        this.drawInnerTile(ctx, x, y, 1, 1, this.imgWall);
      }

      if (tile[1].item) {
        const { item } = tile[1];
        switch (item.type) {
          case Item.CARROT:
            this.drawItem(ctx, x, y, this.imgCarrot);
            break;
          case Item.GOGGLES:
            this.drawItem(ctx, x, y, this.imgGoggles);
            break;
          case Item.TELEPORT:
            this.drawItem(ctx, x, y, this.imgTeleport);
            break;
          default:
            console.error('Undefined item given');
        }
      }

      const exit = exits.find(exit => exit.first === x && exit.second === y);
      if (exit) {
        this.drawItem(ctx, x, y, this.imgFinal);
      }
    });
  }

  /**
   * Draw the 4 corner tiles
   * @param {CanvasRenderingContext2D} ctx
   * @param {Integer} outerPosX coordinate (in 0...boundary.second.first )
   * @param {Integer} outerPosY coordinate (in 0...boundary.second.second)
   */
  drawCornerTiles(ctx, outerPosX, outerPosY) {
    this.drawInnerTile(ctx, outerPosX, outerPosY, 0, 0, this.imgWall);
    this.drawInnerTile(ctx, outerPosX, outerPosY, 2, 0, this.imgWall);
    this.drawInnerTile(ctx, outerPosX, outerPosY, 0, 2, this.imgWall);
    this.drawInnerTile(ctx, outerPosX, outerPosY, 2, 2, this.imgWall);
  }

  /**
   * Draw to an inner tile (tiles are subdivided into 9 inner tiles)
   * @param {CanvasRenderingContext2D} ctx
   * @param {Integer} outerPosX outer coordinate (in 0...boundary.second.first )
   * @param {Integer} outerPosY outer coordinate (in 0...boundary.second.second)
   * @param {Integer} innerPosX inner coordinate (in 0...2)
   * @param {Integer} innerPosY inner coordinate (in 0...2)
   * @param {Image} image
   */
  drawInnerTile(ctx, outerPosX, outerPosY, innerPosX, innerPosY, image) {
    ctx.drawImage(image,
      tileSize * (outerPosX + innerPosX / 3),
      tileSize * (outerPosY + innerPosY / 3),
      tileSize / 3,
      tileSize / 3);
  }

  /**
   * Draw item in the map
   * @param {CanvasRenderingContext2D} ctx Canvas context to draw
   * @param {Integer} x position in x axis
   * @param {Integer} y position in y axis
   * @param {Image} imgItem item to draw
   */
  drawItem(ctx, x, y, imgItem) {
    const WIDTH_HEIGHT_RATIO = 0.7;

    ctx.drawImage(
      imgItem,
      x * tileSize + tileSize / 3,
      y * tileSize + tileSize / 3,
      tileSize / 3 * WIDTH_HEIGHT_RATIO,
      tileSize / 3,
    );
  }
}

/**
 * Class responsible to draw player into the canvas
 */
class Player {
  direction = 'NORTH';

  /**
   * Constructor
   * @param {number} x
   * @param {number} y
   * @param {Image} imgNorth
   * @param {Image} imgSouth
   * @param {Image} imgEast
   * @param {Image} imgWest
   * @param {Image} imgMarker
   */
  constructor(x, y, imgNorth, imgSouth, imgEast, imgWest, imgMarker) {
    // coordinate in the canvas (animation)
    this.x = x;
    this.y = y;

    this.imgNorth = imgNorth;
    this.imgSouth = imgSouth;
    this.imgEast = imgEast;
    this.imgWest = imgWest;
    this.imgMarker = imgMarker;
    this.visible = true;
  }

  /**
   * Draw player
   * @param {CanvasRenderingContext2D} ctx
   */
  draw(ctx) {
    if (!this.visible) {
      return;
    }
    let img = null;
    switch (this.direction) {
      case Direction.NORTH:
        img = this.imgNorth;
        break;
      case Direction.SOUTH:
        img = this.imgSouth;
        break;
      case Direction.EAST:
        img = this.imgEast;
        break;
      case Direction.WEST:
        img = this.imgWest;
        break;
      default:
        console.error('Should not happen');
    }
    
    this.drawMarker(ctx, this.markers);
    ctx.drawImage(
      img,
      this.x * tileSize + tileSize / 3,
      this.y * tileSize + tileSize / 3,
      tileSize / 3,
      tileSize / 3,
    );
  }

  /**
   * Draw player markers
   * @param {CanvasRenderingContext2D} ctx
   * @param markers
   */
  drawMarker(ctx, markers) {
    Array.from(markers).forEach((marker) => {
      ctx.drawImage(
        this.imgMarker,
        marker[0].first * tileSize + tileSize / 3,
        marker[0].second * tileSize + tileSize / 3,
        tileSize / 3,
        tileSize / 3,
      );
    });
  }
}

/**
 * Camera class used to focus the view on the player
 */
class Camera {
  /**
   * Update x and y coordinate
   * @param cvs
   * @param ctx
   * @param {Object} player
   * @param {Integer} width
   * @param {Integer} height
   */
  focus(cvs, ctx, player, width, height) {
    // Account for half of player w/h to make their rectangle centered
    const x = this.clamp(player.x * tileSize - cvs.width / 2 + tileSize / 2,
      0, width * tileSize - cvs.width / 2);

    const y = this.clamp(player.y * tileSize - cvs.height / 2
      + tileSize / 2, 0, height * tileSize - cvs.height / 2);

    // Flip the sign b/c positive shifts the canvas to the right, negative - to the left
    ctx.translate(-x, -y);
  }

  /**
   * Make sure coord is between min or max
   * @param {number} coord
   * @param {number} min
   * @param {number} max
   * @returns {number}
   */
  clamp(coord, min, max) {
    if (coord < min) {
      return min;
    } if (coord > max) {
      return max;
    }
    return coord;
  }
}

export default {
  name: 'Board',
  data() {
    return {
      images: {
        wall: null,
        empty: null,
        player0: null, // we can't use a lis :(
      },
      img: new Image(),
      map: null,
      playerObjects: {},
      controls: null,
      camera: new Camera(),
      ctx: null,
      canvas: {
        width: 1,
        height: 1,
      },
      destroyLoop: false,
    };
  },
  computed: {
    ...mapState('game', ['tiles', 'boundary', 'players', 'exits', 'renderBoard']),
    ...mapState('user', ['id']),
  },
  methods: {
    handleResize() {
      const { canvas } = this.$refs;

      // ...then set the internal size to match
      const size = Math.min(canvas.offsetWidth, canvas.offsetHeight);
      canvas.height = size;
      canvas.width = size;

      this.canvas.height = size;
      this.canvas.width = size;
      setTimeout(this.render, 20);
    },
    /**
     * Render board
     */
    render() {
      console.log('render');
      // Reset
      this.ctx.setTransform(1, 0, 0, 1, 0, 0);
      this.ctx.clearRect(0, 0, this.canvas.width, this.canvas.height);

      Object.values(this.playerObjects).forEach((player) => {
        player.visible = false;
      });

      // Update player information from vuex
      Object.entries(this.players).forEach(([uid, player]) => {
        const playerObject = this.playerObjects[uid.toString()];
        playerObject.x = player.coordinate.first;
        playerObject.y = player.coordinate.second;
        playerObject.direction = player.direction;
        playerObject.markers = player.markers;
        playerObject.visible = true;
      });

      // Get player currently playing on this session
      const player = this.playerObjects[this.id.toString()];

      if (player === undefined) {
        console.error('player is undefined during rendering', this.players, this.playerObjects,
          this.id, Object.keys(this.players).includes(this.id.toString()));
      }

      // Focus camera
      this.camera.focus(this.canvas, this.ctx, player, this.boundary.second.first - 1,
        this.boundary.second.second - 1);

      // Draw black background
      this.ctx.rect(0, 0, this.canvas.width * 10, this.canvas.height * 10);
      this.ctx.fillStyle = 'black';
      this.ctx.fill();

      // Draw
      this.map.draw(this.ctx, this.tiles, this.boundary.second, this.exits);

      Object.values(this.playerObjects)
        .forEach(player => player.draw(this.ctx));

      // draw the main player at the end again
      player.draw(this.ctx);
    },
  },
  watch: {
    renderBoard() {
      this.render();
    },
  },
  beforeDestroy() {
    window.removeEventListener('resize', this.handleResize);
    this.destroyLoop = true;
  },
  mounted() {
    this.images = {};
    this.images.player = { };
    this.images.player[Color.BLUE] = {};
    this.images.player[Color.PURPLE] = {};
    this.images.player[Color.PINK] = {};
    this.images.player[Color.ORANGE] = {};

    const promises = [];

    // this long list of premise can't be refactored
    // I tried and I got a lot of image with null as value
    promises.push(new Promise((resolve) => {
      this.images.empty = new Image();
      this.images.empty.addEventListener('load', () => resolve());
      this.images.empty.src = empty;
    }));

    promises.push(new Promise((resolve) => {
      this.images.wall = new Image();
      this.images.wall.addEventListener('load', () => resolve());
      this.images.wall.src = wall;
    }));

    promises.push(new Promise((resolve) => {
      this.images.final = new Image();
      this.images.final.addEventListener('load', () => resolve());
      this.images.final.src = final;
    }));

    promises.push(new Promise((resolve) => {
      this.images.player[Color.BLUE].forward = new Image();
      this.images.player[Color.BLUE].forward.addEventListener('load', () => resolve());
      this.images.player[Color.BLUE].forward.src = playerBlue;
    }));

    promises.push(new Promise((resolve) => {
      this.images.player[Color.BLUE].left = new Image();
      this.images.player[Color.BLUE].left.addEventListener('load', () => resolve());
      this.images.player[Color.BLUE].left.src = playerBlueLeft;
    }));

    promises.push(new Promise((resolve) => {
      this.images.player[Color.BLUE].right = new Image();
      this.images.player[Color.BLUE].right.addEventListener('load', () => resolve());
      this.images.player[Color.BLUE].right.src = playerBlueRight;
    }));

    promises.push(new Promise((resolve) => {
      this.images.player[Color.BLUE].back = new Image();
      this.images.player[Color.BLUE].back.addEventListener('load', () => resolve());
      this.images.player[Color.BLUE].back.src = playerBlueBack;
    }));

    promises.push(new Promise((resolve) => {
      this.images.player[Color.PINK].forward = new Image();
      this.images.player[Color.PINK].forward.addEventListener('load', () => resolve());
      this.images.player[Color.PINK].forward.src = playerPink;
    }));

    promises.push(new Promise((resolve) => {
      this.images.player[Color.PINK].left = new Image();
      this.images.player[Color.PINK].left.addEventListener('load', () => resolve());
      this.images.player[Color.PINK].left.src = playerPinkLeft;
    }));

    promises.push(new Promise((resolve) => {
      this.images.player[Color.PINK].right = new Image();
      this.images.player[Color.PINK].right.addEventListener('load', () => resolve());
      this.images.player[Color.PINK].right.src = playerPinkRight;
    }));

    promises.push(new Promise((resolve) => {
      this.images.player[Color.PINK].back = new Image();
      this.images.player[Color.PINK].back.addEventListener('load', () => resolve());
      this.images.player[Color.PINK].back.src = playerPinkBack;
    }));

    promises.push(new Promise((resolve) => {
      this.images.player[Color.ORANGE].forward = new Image();
      this.images.player[Color.ORANGE].forward.addEventListener('load', () => resolve());
      this.images.player[Color.ORANGE].forward.src = playerOrange;
    }));

    promises.push(new Promise((resolve) => {
      this.images.player[Color.ORANGE].left = new Image();
      this.images.player[Color.ORANGE].left.addEventListener('load', () => resolve());
      this.images.player[Color.ORANGE].left.src = playerOrangeLeft;
    }));

    promises.push(new Promise((resolve) => {
      this.images.player[Color.ORANGE].right = new Image();
      this.images.player[Color.ORANGE].right.addEventListener('load', () => resolve());
      this.images.player[Color.ORANGE].right.src = playerOrangeRight;
    }));

    promises.push(new Promise((resolve) => {
      this.images.player[Color.ORANGE].back = new Image();
      this.images.player[Color.ORANGE].back.addEventListener('load', () => resolve());
      this.images.player[Color.ORANGE].back.src = playerOrangeBack;
    }));

    promises.push(new Promise((resolve) => {
      this.images.player[Color.PURPLE].forward = new Image();
      this.images.player[Color.PURPLE].forward.addEventListener('load', () => resolve());
      this.images.player[Color.PURPLE].forward.src = playerPurple;
    }));

    promises.push(new Promise((resolve) => {
      this.images.player[Color.PURPLE].left = new Image();
      this.images.player[Color.PURPLE].left.addEventListener('load', () => resolve());
      this.images.player[Color.PURPLE].left.src = playerPurpleLeft;
    }));

    promises.push(new Promise((resolve) => {
      this.images.player[Color.PURPLE].right = new Image();
      this.images.player[Color.PURPLE].right.addEventListener('load', () => resolve());
      this.images.player[Color.PURPLE].right.src = playerPurpleRight;
    }));

    promises.push(new Promise((resolve) => {
      this.images.player[Color.PURPLE].back = new Image();
      this.images.player[Color.PURPLE].back.addEventListener('load', () => resolve());
      this.images.player[Color.PURPLE].back.src = playerPurpleBack;
    }));

    promises.push(new Promise((resolve) => {
      this.images.carrot = new Image();
      this.images.carrot.addEventListener('load', () => resolve());
      this.images.carrot.src = itembox;
    }));

    promises.push(new Promise((resolve) => {
      this.images.marker = new Image();
      this.images.marker.addEventListener('load', () => resolve());
      this.images.marker.src = marker;
    }));

    Promise.all(promises).then(() => {
      this.ctx = this.$refs.canvas.getContext('2d');
      // create entities
      this.map = new MapRegion(this.images.empty, this.images.wall, this.images.final,
        this.images.carrot, this.images.carrot, this.images.carrot);
      Object.entries(this.players).forEach(([uid, player]) => {
        this.playerObjects[uid] = new Player(player.coordinate.x, player.coordinate.y,
          this.images.player[player.color].back, this.images.player[player.color].forward,
          this.images.player[player.color].right, this.images.player[player.color].left,
          this.images.marker);
      });
      this.render();
      this.handleResize();
      window.addEventListener('resize', this.handleResize);
      setTimeout(this.render, 500);
    });
  },
};
</script>
