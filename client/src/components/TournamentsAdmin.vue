<template>
  <v-card class="fill-height mx-auto" width="90%" v-if="tournament.users">
    <v-toolbar>
      <v-toolbar-title>
        {{ $t('tournament') }}: {{ tournament.name }}
      </v-toolbar-title>
      <v-spacer />
      <v-toolbar-title>
        {{ $t('players') }}: {{ tournament.users.length }}
      </v-toolbar-title>
      <v-spacer />
      <v-toolbar-title>
        {{ $t('round') }}: {{ tournament.round }}
      </v-toolbar-title>
      <v-spacer />
      <v-btn v-if="!tournament.started" title="Start"
             @click="startTournament({id: tournament.id })">
        <v-icon>play_arrow</v-icon>
       {{ $t('start') }}
      </v-btn>
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
  </v-card>
</template>

<script>
import { mapActions, mapState } from 'vuex';
import { colorToImage } from '../enums';

export default {
  name: 'TournamentsAdmin',
  data() {
    return {
      roomName: '',
      dialogData: {
        name: '',
      },
    };
  },
  computed: {
    ...mapState('tournament', ['tournament']),
  },
  methods: {
    colorToImageLocal(color) {
      return `../${colorToImage(color)}`;
    },
    ...mapActions('tournaments', ['startTournament']),
  },
  mounted() {
    if (this.tournament.users) {
      this.$router.push({ name: 'tournamentsManagement' });
    }
  },
};
</script>
