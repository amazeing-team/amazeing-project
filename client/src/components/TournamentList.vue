<template>
  <v-content>
    <v-container fill-width>
      <v-layout align-center column>
        <h1 class="pb-12 task-list-title"> {{ $t('select_tournament') }}</h1>
        <v-row v-for="tournament in tournaments" :key="tournament.id" class="mb-10">
          <v-btn rounded block elevation="5" x-large
                 @click="join(tournament)">
           {{ $t('tournament') }} {{ tournament.name }}
          </v-btn>
        </v-row>
      </v-layout>
    </v-container>
  </v-content>
</template>

<style>
.task-list-title {
  font-size: 100px;
}
</style>

<script>
import { mapState, mapActions } from 'vuex';

export default {
  computed: mapState('tournaments', ['tournaments']),
  methods: {
    ...mapActions('tournaments', ['joinTournament']),
    join(tournament) {
      this.joinTournament({ id: tournament.id });
      this.$router.push({ name: 'tournament-wait', params: { id: tournament.id } });
    },
  }
};
</script>
