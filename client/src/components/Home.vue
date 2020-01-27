<template>
  <v-content>
    <v-container fill-width>
      <v-layout align-center column>
        <v-row class="mb-10">
           <v-btn rounded block elevation="5" x-large to="/playground">
            {{ $t('playground') }}
           </v-btn>
        </v-row>

        <v-row class="mb-10">
          <v-btn rounded block elevation="5" x-large to="/tasks" :disabled="tasks.length === 0">
            {{ $t('tasks') }}
          </v-btn>
        </v-row>

        <v-row class="mb-10">
          <v-btn rounded block elevation="5" x-large :to="{ name: 'lobby' }">
            {{ $t('multiplayer') }}
          </v-btn>
        </v-row>
        <v-text-field
          :label="$t('username')"
          v-model="username"
        />
        <owl-selector />
      </v-layout>
    </v-container>
  </v-content>
</template>

<script>
import { mapActions, mapState } from 'vuex';
import OwlSelector from './OwlSelector.vue';

export default {
  name: 'Home',
  components: { OwlSelector },
  computed: {
    username: {
      get() {
        return this.$store.state.user.name;
      },
      set(value) {
        this.saveUserName({ value, wm: this });
        this.updateUser();
      },
    },
    ...mapState('tasks', ['tasks']),
  },
  methods: {
    ...mapActions('user', ['loadUserName', 'saveUserName']),
    ...mapActions('game', ['updateUser']),
  },
};
</script>
