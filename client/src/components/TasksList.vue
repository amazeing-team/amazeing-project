<template>
    <v-content>
    <v-container fill-width>
      <v-layout align-center column>
        <h1 class="pb-12 task-list-title">
            {{ $t('select_task') }}
        </h1>
        <v-row v-for="task in tasksWithStatus" :key="task.id" class="mb-10">
          <v-btn rounded block elevation="5" :disabled="task.locked" x-large
            :to="'/tasks/' + task.id ">
            <v-icon v-if="task.done" size="25" left color="green darken-3">done_outline</v-icon>
             Task {{ task.id }}: {{ task.description }}</v-btn>
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
import { mapGetters } from 'vuex';

export default {
  computed: mapGetters('user', ['tasksWithStatus']),
};
</script>
