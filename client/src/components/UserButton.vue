<template>
  <v-btn text>
    {{ $t('user') }}: {{ name }}
  </v-btn>
</template>

<script>
import { mapState } from 'vuex';

export default {
  name: 'UserButton',
  computed: mapState('user', ['name']),
};
</script>
