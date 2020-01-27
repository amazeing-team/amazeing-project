<template>
  <v-row class="fill-height" justify="center" align="center">
    <v-col v-if="room" class="fill-height">
      <board class="fill-height" />
    </v-col>
    <v-col v-if="room" class="fill-height">
      <v-list shaped>
        <v-subheader>
          {{ $t('lobby_stats') }}
        </v-subheader>
        <v-list-item-group color="primary">
          <!-- <v-list-item >
            <v-list-item-content>
              <v-list-item-title>Current Map: Forest of Darkness</v-list-item-title>
            </v-list-item-content>
          </v-list-item>-->
          <v-list-item >
            <v-list-item-content>
              <v-list-item-title>
                {{ $t('num_players') }} {{ room.game.players.length }}
              </v-list-item-title>
            </v-list-item-content>
          </v-list-item>
        </v-list-item-group>
        <v-subheader>
          {{ $t('players') }}
        </v-subheader>
        <v-list-item-group color="primary">
          <v-list-item v-for="player in room.game.players" :key="player[0].uid">
            <v-list-item-icon>
              <img :src="colorToImageLocal(player[0].color)" alt="image player"
                   style="width:30px" />
            </v-list-item-icon>
            <v-list-item-content>
              <v-list-item-title>{{ player[0].name }}</v-list-item-title>
            </v-list-item-content>
          </v-list-item>
        </v-list-item-group>
      </v-list>
      <v-btn @click="multiplayerAction('TURN_LEFT_MULTI')">
        {{ $t('left') }}
      </v-btn>
      <v-btn @click="multiplayerAction('MOVE_MULTI')">
        {{ $t('move') }}
      </v-btn>
      <v-btn @click="multiplayerAction('TURN_RIGHT_MULTI')">
        {{ $t('right') }}
      </v-btn>
    </v-col>
    <v-dialog v-model="win" max-width="500">
      <v-card>
        <v-card-title
          class="headline"
          primary-title
        >
          {{ $t('win_msg') }}
        </v-card-title>

        <v-card-actions>
          <v-spacer></v-spacer>
          <v-btn
            color="primary"
            depressed
            @click="$router.push({ name: 'lobby' })"
          >
            {{ $t('new_game') }}
          </v-btn>
        </v-card-actions>
      </v-card>
    </v-dialog>
    <v-dialog v-model="lossDialog" max-width="500">
      <v-card>
        <v-card-title
          class="headline"
          primary-title
        >
          {{ $t('loss_msg') }}
        </v-card-title>
        <v-card-text v-if="winner">
          {{ $t('loss_sub') }} {{ winner.name }}.
        </v-card-text>

        <v-card-actions>
          <v-spacer></v-spacer>
          <v-btn
            color="primary"
            depressed
            @click="$router.push({ name: 'lobby' })"
          >
            {{ $t('new_game') }}
          </v-btn>
        </v-card-actions>
      </v-card>
    </v-dialog>
  </v-row>
</template>

<script>
  import { mapState, mapActions } from 'vuex';
  import Board from './Board.vue';
  import { colorToImage } from '../enums';

export default {
  name: 'Multiplayer',
  components: {
    Board,
  },
  data() {
    return {
      lossDialog: false,
    };
  },
  computed: {
    ...mapState('rooms', ['id', 'rooms', 'win', 'winner']),
    room() {
      return this.rooms.find(room => room.id === this.id);
    },
  },
  methods: {
    ...mapActions('rooms', ['multiplayerAction']),
    colorToImageLocal(color) {
      return `../${colorToImage(color)}`;
    },
  },
  watch: {
    winner(newValue) {
      if (newValue) {
        this.lossDialog = true;
      }
    },
  },
};
</script>
