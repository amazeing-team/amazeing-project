<template>
  <v-card class="fill-height mx-auto" width="90%">
    <v-toolbar>
      <v-toolbar-title>
        {{ $t('tournaments_list') }}
      </v-toolbar-title>
      <v-spacer />
      <new-tournament-dialog :inputData.sync="dialogData" @submit="validate()"/>
    </v-toolbar>
    <v-list>
      <v-list-item-group color="primary">
        <v-list-item v-for="tournament in tournaments" :key="tournament.id">
          <v-list-item-content>
            <v-list-item-title>
              {{ tournament.name }}
            </v-list-item-title>
          </v-list-item-content>
          <v-list-item-action>
            <v-btn icon @click="track(tournament)" title="Start">
              <v-icon color="grey lighten-1">remove_red_eyes</v-icon>
            </v-btn>
          </v-list-item-action>
          <v-list-item-action>
            <v-btn icon @click="remove(tournament)" title="Start">
              <v-icon color="grey lighten-1">delete</v-icon>
            </v-btn>
          </v-list-item-action>
          <v-list-item-action>
            <v-btn icon @click="start(tournament)" title="Start">
              <v-icon color="grey lighten-1">play_arrow</v-icon>
            </v-btn>
          </v-list-item-action>
        </v-list-item>
      </v-list-item-group>
    </v-list>
  </v-card>
</template>

<script>
import { mapActions, mapState } from 'vuex';
import NewTournamentDialog from './NewTournamentDialog.vue';

export default {
  name: 'TournamentsManagement',
  components: { NewTournamentDialog },
  data() {
    return {
      roomName: '',
      dialogData: {
        name: '',
      },
    };
  },
  methods: {
    ...mapActions('tournaments', ['createTournament', 'startTournament', 'deleteTournament', 'trackTournament']),
    validate() {
      this.createTournament({ name: this.dialogData.name });
    },
    start(tournament) {
      this.startTournament({ id: tournament.id });
    },
    remove(tournament) {
      this.deleteTournament({ id: tournament.id });
    },
    track(tournament) {
      this.trackTournament({ id: tournament.id });
      this.$router.push({ name: 'tournament-view' });
    },
  },
  computed: mapState('tournaments', ['tournaments']),
};
</script>
