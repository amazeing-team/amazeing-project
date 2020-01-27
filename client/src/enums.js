import pinkOwl from './assets/img/owl-pink-front.png';
import blueOwl from './assets/img/owl-blue-front.png';
import purpleOwl from './assets/img/owl-purple-front.png';
import orangeOwl from './assets/img/owl-orange-front.png';

export const KeyboardLayout = {
  QWERTZ: 0,
  QWERTY: 1,
  AZERTY: 2,
};

export const Direction = {
  NORTH: 'NORTH',
  WEST: 'WEST',
  EAST: 'EAST',
  SOUTH: 'SOUTH',
};

export const Color = {
  BLUE: 'BLUE',
  PINK: 'PINK',
  ORANGE: 'ORANGE',
  PURPLE: 'PURPLE',
};

export const Item = {
  GOGGLES: 'GOGGLES',
  CARROT: 'CARROT',
  TELEPORT: 'TELEPORT',
};

export function colorToImage(color) {
  switch (color) {
    case Color.BLUE:
      return blueOwl;
    case Color.PINK:
      return pinkOwl;
    case Color.ORANGE:
      return orangeOwl;
    case Color.PURPLE:
      return purpleOwl;
    default:
      console.error('should not happen');
  }
  console.error('should not happen');
  return null;
}

export default { KeyboardLayout, Direction, Color };
