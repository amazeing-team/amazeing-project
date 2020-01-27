<template>
  <v-row class="fill-height" justify="center" align="center">
    <v-col class="fill-height">
      <v-list>
        <v-subheader> {{ $t('room') }} "{{ room.name }}" <v-btn @click="ready()">{{ $t('ready?') }}</v-btn> </v-subheader>
        <v-list-item-group color="primary">
          <v-list-item v-for="player in room.game.players" :key="player[0].uid">
            <v-list-item-avatar>
              <v-img :src="colorToImageLocal(player[0].color)"></v-img>
            </v-list-item-avatar>
            <v-list-item-content>
              <v-list-item-title>
                {{ player[0].name }} {{ player[1].ready ? $t('ready') : $t('not_ready') }}
              </v-list-item-title>
            </v-list-item-content>
          </v-list-item>
        </v-list-item-group>
      </v-list>
    </v-col>
  </v-row>
</template>

<script>
import { mapState, mapActions } from 'vuex';
import { colorToImage } from '../enums';

export default {
  methods: {
    ...mapActions('rooms', ['readyRoom', 'reset']),
    ready() {
      this.readyRoom({ id: this.room.id });
    },
    colorToImageLocal(color) {
      return `../${colorToImage(color)}`;
    },
  },
  computed: {
    ...mapState('rooms', ['rooms', 'play']),
    room() {
      return this.rooms.find(room => room.id === parseInt(this.$route.params.id, 10));
    },
  },
  mounted() {
    this.reset();
  },
  watch: {
    play(newValue) {
      if (newValue) {
        this.$router.push({ name: 'multiplayer' });
      }
    },
  },
};
</script>
