<template>
  <v-dialog max-width="700" v-model="dialog">
    <template v-slot:activator="{ on }">
      <v-btn v-on="on" text>
        <v-icon>add</v-icon>
        {{ $t('create_new_room') }}
      </v-btn>
    </template>
    <v-card>
      <v-card-title
        class="headline grey lighten-2"
        primary-title>
        {{ $t('create_new_room') }}
      </v-card-title>
      <v-card-text>
        <v-container>
          <v-row>
            <v-col cols="12">
              <v-text-field :label="$t('name')" v-model="inputData.name" required></v-text-field>
            </v-col>
            <v-col cols="12">
              <v-text-field :label="$t('width')" v-model="inputData.width" required type="number"></v-text-field>
            </v-col>
            <v-col cols="12">
              <v-text-field :label="$t('height')" v-model="inputData.height" required type="number"></v-text-field>
            </v-col>
          </v-row>
        </v-container>
      </v-card-text>
      <v-card-actions>
        <v-spacer></v-spacer>
        <v-btn
          color="success"
          text
          :disabled="disabled"
          @click="submit()">
          {{ $t('create_room') }}
        </v-btn>
      </v-card-actions>
    </v-card>
  </v-dialog>
</template>

<script>
export default {
  name: 'NewRoomDialog',
  props: {
    inputData: Object,
  },
  data: () => ({
    dialog: false,
  }),
  computed: {
    disabled() {
      return this.inputData.name.length < 5
        || this.inputData.name.length > 100
        || this.inputData.width < 5
        || this.inputData.width > 200
        || this.inputData.height < 5
        || this.inputData.height > 200;
    },
  },
  methods: {
    submit() {
      this.dialog = false;
      this.$emit('submit');
    },
  },
};
</script>
