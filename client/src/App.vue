<template>
  <v-app id="inspire">
    <v-content v-if="loaded && socketConnected">
      <v-container
        class="fill-height"
        fluid
        v-if="id !== -1"
      >
        <router-view name="navbar"></router-view>
        <router-view></router-view>
        <router-view name="footer"></router-view>
      </v-container>
    </v-content>
    <v-content v-else>
      <LoadingScreen />
    </v-content>
  </v-app>
</template>

<script>
import { mapState, mapActions } from 'vuex';
import LoadingScreen from './components/LoadingScreen.vue';

export default {
  components: { LoadingScreen },
  props: {
    source: String,
  },
  data: () => ({
    drawer: null,
    drawerRight: null,
    left: true,
    code: '',
  }),
  computed: {
    ...mapState('tasks', ['loaded']),
    ...mapState('user', ['id']),
    ...mapState(['socketConnected']),
  },
  watch: {
    socketConnected(newValue) {
      if (newValue) {
        this.loadUserName({ wm: this });
        this.loadTaskSolved({ wm: this });
        this.loadOwlColor({ wm: this });
        this.register();
      }
    },
  },
  methods: {
    ...mapActions('game', ['register']),
    ...mapActions('user', ['loadUserName', 'loadOwlColor', 'loadTaskSolved']),
  },
  beforeCreate() {
    this.$connect();
    this.$store.dispatch('tasks/loadTasks');
  },
};
</script>
