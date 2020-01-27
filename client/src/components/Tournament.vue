<template>
  <v-row v-if="room" class="fill-height" justify="center" align="center">
    <v-col class="fill-height">
      <board class="fill-height" :key="`tournament${tournament.round}`" />
    </v-col>
    <v-col class="fill-height">
      <v-list shaped>
        <v-subheader> {{ $t('lobby_stats') }}</v-subheader>
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
        <v-subheader>{{ $t('players') }}</v-subheader>
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
      <v-btn @click="tournamentAction('TURN_LEFT_TOURNAMENT')">{{ $t('left') }}</v-btn>
      <v-btn @click="tournamentAction('MOVE_TOURNAMENT')">{{ $t('move') }}</v-btn>
      <v-btn @click="tournamentAction('TURN_RIGHT_TOURNAMENT')">{{ $t('right') }}</v-btn>
      <v-dialog v-model="win" max-width="500" :hide-overlay="true">
        <v-card>
          <v-card-title
            class="headline"
            primary-title
          >
            {{ $t('round_won') }}
          </v-card-title>

          <v-card-actions>
            <v-spacer />
            <v-btn
              color="primary"
              depressed
              @click="$router.push({ name: 'tournament-wait', params: $router.params })"
            >
              Continue
            </v-btn>
          </v-card-actions>
        </v-card>
      </v-dialog>
      <v-dialog v-model="loss" max-width="500" :hide-overlay="true">
        <v-card>
          <v-card-title
            class="headline"
            primary-title
          >
            {{ $t('loss_msg') }}
          </v-card-title>

          <v-card-actions>
            <v-spacer />
            <v-btn
              color="primary"
              depressed
              @click="$router.push({ name: 'multiplayer' })">
            {{ $t('new_game') }}
            </v-btn>
          </v-card-actions>
        </v-card>
      </v-dialog>
      <v-dialog max-width="700" v-model="winTournament" :hide-overlay="true">
        <v-card>
          <v-card-title
            class="headline grey lighten-2"
            primary-title>
            {{ $t('won_tournament') }}
          </v-card-title>
          <v-card-text>
            {{ $t('won_tournament_text') }}
          </v-card-text>
          <v-card-actions>
            <v-spacer />
            <v-btn
              color="success"
              text
              :to="{ name: 'home' }">
              {{ $t('new_game') }}
            </v-btn>
          </v-card-actions>
        </v-card>
      </v-dialog>
    </v-col>
  </v-row>
</template>

<script>
import { mapState, mapActions } from 'vuex';
import Board from './Board.vue';
import { colorToImage } from '../enums';

export default {
  name: 'Tournament',
  components: {
    Board,
  },
  computed: {
    ...mapState('rooms', ['id', 'rooms', 'win', 'winner']),
    ...mapState('tournament', ['loaded', 'play', 'tournament', 'winTournament']),
    room() {
      return this.rooms.find(room => room.id === this.id);
    },
    loss() {
      return !this.play;
    },
  },
  methods: {
    ...mapActions('tournament', ['tournamentAction']),
    colorToImageLocal(color) {
      return `../${colorToImage(color)}`;
    },
  },
};
</script>
