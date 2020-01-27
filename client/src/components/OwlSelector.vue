<template>
  <v-carousel
    :continuous=true
    :cycle=false
    hide-delimiters
    height="300"
    v-model="selected"
  >
    <v-carousel-item
      v-for="(owl) in owls"
      :key="owl.color"
    >
      <v-row
        class="fill-height"
        align="center"
        justify="center"
      >
        <img :src="owl.src" alt="Owl"/>
      </v-row>
    </v-carousel-item>
  </v-carousel>
</template>

<script>
import { mapState, mapActions } from 'vuex';

import { Color } from '../enums';

import pinkOwl from '../assets/img/owl-pink-front.png';
import blueOwl from '../assets/img/owl-blue-front.png';
import purpleOwl from '../assets/img/owl-purple-front.png';
import orangeOwl from '../assets/img/owl-orange-front.png';

export default {
  name: 'OwlSelector',
  data() {
    return {
      selected: 0,
      owls: [
        {
          src: blueOwl,
          color: Color.BLUE,
        },
        {
          src: pinkOwl,
          color: Color.PINK,
        },
        {
          src: orangeOwl,
          color: Color.ORANGE,
        },
        {
          src: purpleOwl,
          color: Color.PURPLE,
        },
      ],
    };
  },
  computed: mapState('user', ['owlColor']),
  methods: {
    ...mapActions('user', ['saveOwlColor']),
    ...mapActions('game', ['updateUser']),
  },
  watch: {
    selected(newValue) {
      this.saveOwlColor({ value: this.owls[newValue].color, wm: this });
      this.updateUser();
    },
  },
  mounted() {
    this.owls.forEach((owl, i) => {
      if (owl.color === this.owlColor) {
        this.selected = i;
      }
    });
  },
};
</script>
