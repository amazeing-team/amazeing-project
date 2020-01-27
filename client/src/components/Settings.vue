<template>
  <v-row
    class="fill-height"
    justify="space-around">
    <v-col class="fill-height col-12 col-md-6">
      <h1>Settings</h1>
      <h2>Performance</h2>
      <v-slider
        v-model="animationSpeed"
        max=10
        min=1
        label="Animation"
      >
      </v-slider>
      <h2>Layout for keyboard</h2>
      <v-radio-group v-model="keyboardLayout">
        <v-radio :value="KeyboardLayout.QWERTZ" label="QWERTZ"></v-radio>
        <v-radio :value="KeyboardLayout.QWERTY" label="QWERTY"></v-radio>
        <v-radio :value="KeyboardLayout.AZERTY" label="AZERTY"></v-radio>
      </v-radio-group>
      <h2>Username</h2>
        <v-text-field v-model="username"

          ></v-text-field>
    </v-col>
  </v-row>
</template>

<script>
import KeyboardLayout from '../enums';

export default {
  data() {
    return {
      KeyboardLayout,
    };
  },
  computed: {
    animationSpeed: {
      get() {
        return this.$store.state.settings.animationSpeed;
      },
      set(value) {
        this.$store.commit('settings/setAnimationSpeed', value);
      },
    },
    keyboardLayout: {
      get() {
        return this.$store.state.settings.keyboardLayout;
      },
      set(value) {
        this.$store.commit('settings/setKeyboardLayout', value);
      },
    },
    username: {
      get() {
        return this.$store.state.user.name;
      },
      set(value) {
        this.$store.commit('user/setUsername', value);
      },

    },
  },
};
</script>
