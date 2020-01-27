<template>
  <v-app-bar app clipped-right color="blue-grey" dark dense >
    <router-link to="/"><v-btn icon><v-icon>home</v-icon></v-btn></router-link>
    <v-toolbar-title>
      {{ $t('amazeing') }}
    </v-toolbar-title>
    <v-spacer></v-spacer>

    <v-spacer></v-spacer>
    <v-toolbar-items>
      <ServerSelect />

      <v-divider inset vertical />

      <UserButton />
    </v-toolbar-items>
  </v-app-bar>
</template>

<script>
import ServerSelect from './ServerSelect.vue';
import UserButton from './UserButton.vue';

export default {
  components: { ServerSelect, UserButton },
  data() {
    return {
    };
  },
};

</script>
