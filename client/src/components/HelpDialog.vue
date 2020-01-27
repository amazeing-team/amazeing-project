<template>
  <div class="text-center">
    <v-dialog v-model="displayHelp" width="500">
      <template v-slot:activator="{ on }">
        <v-btn color="blue-grey lighten-1" dark v-on="on">{{ buttontext }}</v-btn>
      </template>

      <v-card>
        <v-card-title class="justify-center headline grey lighten-2" primary-title>
          {{ title }}
        </v-card-title>

        <v-card-text class="text-center pt-5" style="font-size:20px">{{ helptext }}</v-card-text>

         <v-divider></v-divider>
        <v-card-actions>
          <v-spacer></v-spacer>
          <v-btn color="primary" text @click="displayHelp = false">
              {{ $t('ok') }}
          </v-btn>
        </v-card-actions>
      </v-card>
    </v-dialog>
  </div>
</template>

<script>
export default {
  name: 'HelpDialog',
  props: {
    buttontext: { type: String, default: 'Help' },
    title: { type: String, default: 'Task title' },
    helptext: { type: String, default: 'Task help text' },
    displayHelp: {
      type: Boolean,
      default: true,
    },
  },
};
</script>
