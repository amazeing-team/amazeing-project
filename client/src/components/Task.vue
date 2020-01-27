<template>
  <div style="width: 100%; height: 100%;">
    <task-view v-if="task"
               :editor="true"
               :task="task"
               :initialCode="code"
               :show-control="false"
               @submit="updateCode($event)"
               :key="task.id" />
    <v-dialog v-model="dialog" max-width="500">
      <v-card>
        <v-card-title
          class="headline"
          primary-title
        >
          {{ $t('task_solved') }}
        </v-card-title>

        <v-card-text v-if="nextTaskAvailable">
          {{ $t('task_sub') }}
        </v-card-text>

        <v-card-text v-else>
          {{ $t('task_no_next') }}
        </v-card-text>

        <v-card-actions>
          <v-spacer></v-spacer>
          <v-btn
            color="primary"
            depressed
            @click="$router.push({ name: 'home' })"
          >
            <v-icon>home</v-icon>
            {{ $t('home') }}
          </v-btn>
          <v-btn
            color="success"
            depressed
            @click="next"
            :disabled="!nextTaskAvailable"
          >
            <v-icon>play_arrow</v-icon>
            {{ $t('continue') }}
          </v-btn>
        </v-card-actions>
      </v-card>
    </v-dialog>
  </div>
</template>

<script>
import {
  mapActions, mapGetters, mapMutations, mapState,
} from 'vuex';
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
      this.$cookie.set(`code-task-${this.$route.params.id}`, code, LIFETIME);
    },
    next() {
      this.$router.push({ name: 'task', params: { id: this.task.id + 1 } });
    },
    ...mapMutations('game', ['setWin']),
    ...mapActions('user', ['complete']),
    ...mapActions('game', ['resetStep']),
  },
  computed: {
    ...mapGetters('tasks', ['getTaskById']),
    ...mapGetters('game', ['hasNextStep']),
    ...mapState('tasks', ['loaded', 'tasks']),
    ...mapState('game', ['valid', 'win']),
    ...mapState('user', ['id', 'taskSolved']),
    nextTaskAvailable() {
      return this.task && this.tasks.length > this.task.id;
    },
    dialog() {
      return this.win && !this.hasNextStep;
    },
    task() {
      return this.getTaskById(parseInt(this.$route.params.id, 10));
    },
    code() {
      const storedCode = this.$cookie.get(`code-task-${this.$route.params.id}`);
      if (storedCode && storedCode !== '') {
        return storedCode;
      }
      return this.task.code;
    },
  },
  beforeRouteUpdate(to, from, next) {
    this.setWin(false);
    this.resetStep();
    next();
  },
  beforeRouteEnter(to, from, next) {
    next((wm) => {
      wm.setWin(false);
      wm.resetStep();
    });
  },
  mounted() {
    this.setWin(false);
    this.resetStep();
  },
  watch: {
    win(newValue) {
      if (newValue) {
        this.$store.dispatch('user/complete', { taskSolvedId: this.task.id, wm: this });
      }
    },
  },
  data() {
    return {
      console,
    };
  },
};
</script>
