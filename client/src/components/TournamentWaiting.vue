<template>
  <v-card class="fill-height mx-auto" width="90%">
    <v-toolbar>
      <v-toolbar-title>
        {{ $t('wait_start') }}        
      </v-toolbar-title>
    </v-toolbar>
    <v-list>
      <v-list-item-group color="primary">
        <v-list-item v-for="user in tournament.users" :key="user.uid">
          <v-list-item-avatar>
            <v-img :src="colorToImageLocal(user.color)" />
          </v-list-item-avatar>
          <v-list-item-content>
            <v-list-item-title>
              {{ user.name }}
            </v-list-item-title>
          </v-list-item-content>
        </v-list-item>
      </v-list-item-group>
    </v-list>
    <v-dialog max-width="700" v-model="winTournament">
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
  </v-card>
</template>

<script>
  import { mapState, mapMutations } from 'vuex';
  import { colorToImage } from '../enums';

export default {
  name: 'TournamentWaiting',
  computed: {
    ...mapState('tournament', ['tournament', 'play', 'winTournament']),
  },
  methods: {
    colorToImageLocal(color) {
      return `../${colorToImage(color)}`;
    },
    ...mapMutations('tournament', ['stopPlay']),
  },
  mounted() {
    this.stopPlay();
  },
  beforeRouteEnter(from, to, next) {
    next((wm) => {
      wm.stopPlay();
    });
  },
  beforeRouteUpdate(from, to, next) {
    this.stopPlay();
    next();
  },
  watch: {
    play(newValue) {
      if (newValue) {
        this.$router.push({ name: 'tournament' });
      }
    },
  },
};
</script>
