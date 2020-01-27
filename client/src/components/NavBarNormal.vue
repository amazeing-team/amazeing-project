<template>
   <v-app-bar
    app
    clipped-right
    color="blue-grey"
    dark
    dense
  >
    <router-link to="/"><v-btn icon><v-icon>home</v-icon></v-btn></router-link>
    <v-toolbar-title>{{ $t('amazeing') }}</v-toolbar-title>
    <v-spacer />
  <v-toolbar-items>
   <ServerSelect />
  </v-toolbar-items>
  </v-app-bar>
</template>

<script>
import ServerSelect from './ServerSelect.vue';

export default {
  components: { ServerSelect },
};

</script>
