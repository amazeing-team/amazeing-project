<template>
  <v-app-bar app clipped-right color="blue-grey" dark dense>
    <router-link :to="{ name: 'home' }">
      <v-btn icon>
        <v-icon>home</v-icon>
      </v-btn>
    </router-link>
    <v-toolbar-title>{{ $t('amazeing') }}</v-toolbar-title>
    <v-spacer />
    <HelpDialog
      :buttontext='buttonText'
      :title="task.description"
      :helptext="task.helptext"
      :display-help.sync="displayHelp"
    />
    <v-spacer />
    <v-toolbar-items>
      <ServerSelect />
    </v-toolbar-items>
  </v-app-bar>
</template>


<script>
import { mapGetters } from 'vuex';
import ServerSelect from './ServerSelect.vue';
import HelpDialog from './HelpDialog.vue';

export default {
  components: { HelpDialog, ServerSelect },
  data() {
    return {
      displayHelp: true,
    };
  },
  beforeRouteUpdate(from, to, next) {
    this.displayHelp = true;
    next();
  },
  computed: {
    ...mapGetters('tasks', ['getTaskById']),
    task() {
      console.log(this.$route);
      return this.getTaskById(parseInt(this.$route.params.id, 10));
    },
    buttonText() {
      return `Task ${this.task.id}: ${this.task.description}`;
    },
  },
};
</script>
