import { Color, Direction } from '../enums';

const states = [{
  tiles: [
    [
      {
        second: 0,
        first: 1,
      },
      {
        directions: [
          'SOUTH',
          'EAST',
          'WEST',
        ],
      },
    ],
    [
      {
        second: 1,
        first: 2,
      },
      {
        directions: [
          'NORTH',
          'WEST',
          'EAST',
          'SOUTH',
        ],
      },
    ],
    [
      {
        second: 1,
        first: 1,
      },
      {
        directions: [
          'NORTH',
          'WEST',
          'EAST',
          'SOUTH',
        ],
      },
    ],
    [
      {
        second: 1,
        first: 0,
      },
      {
        directions: [
          'EAST',
          'NORTH',
          'SOUTH',
        ],
      },
    ],
    [
      {
        second: 2,
        first: 1,
      },
      {
        directions: [
          'NORTH',
          'WEST',
          'EAST',
          'SOUTH',
        ],
      },
    ],
  ],
  boundary: {
    second: 3,
    first: 3,
  },
  log: [],
  players: [{
    coordinate: {
      first: 1,
      second: 1,
    },
    color: Color.BLUE,
    direction: Direction.WEST,
    markers: [],
  }],
  exits: [{
    first: 2,
    second: 1,
  }],
},
];

export default states;
