<template>
<div style="width: 100%; height: 100%;">
  <task-view v-if="loadedTask"
           :editor="true"
           :task="playground"
           :initialCode="code"
           :show-control="true"
           @submit="updateCode($event)" />

  <v-dialog v-model="win" max-width="500">
      <v-card>
        <v-card-title
          class="headline"
          primary-title
        >
          {{ $t('playground_solve_msg') }}
        </v-card-title>

        <v-card-actions>
          <v-spacer></v-spacer>
          <v-btn
            color="primary"
            depressed
            @click="$router.go()"
          >
            {{ $t('new_game') }}
          </v-btn>
        </v-card-actions>
      </v-card>
    </v-dialog>
  </div>
</template>

<script>
import { mapState, mapActions } from 'vuex';
import TaskView from './TaskView.vue';

export default {
  components: {
    TaskView,
  },
  methods: {
    /**
     * Update code in cookie (save)
     * @param {String} code
     */
    updateCode(code) {
      const LIFETIME = 30;
      this.$cookie.set('code-playground', code, LIFETIME);
    },
    ...mapActions('game', ['chooseTask', 'resetStep']),
  },
  computed: {
    ...mapState('tasks', ['playground']),
    ...mapState('game', ['win']),
  },
  mounted() {
    // ugly hack to force vue to reinitialize the values
    this.$forceUpdate();

    this.$store.commit('tasks/generatePlaygroundMap');
    const storedCode = this.$cookie.get('code-playground');
    if (storedCode && storedCode !== '') {
      this.code = storedCode;
    } else {
      this.code = this.playground.code;
    }
    this.chooseTask({ task: this.playground });
    this.loadedTask = true;
  },
  data() {
    return {
      code: '',
      loadedTask: false,
    };
  },
};
</script>
