<template>
<div style="padding: 6px;">
  <v-menu offset-y>
    <template v-slot:activator="{ on }">
      <v-btn
        text
        v-on="on"
      >
        Server: localhost
      </v-btn>
    </template>
    <v-list>
      <v-list-item>
        <v-list-item-title @click="connectDialog = true">{{ $t('connect_server') }}</v-list-item-title>
      </v-list-item>

      <v-list-item>
        <v-list-item-title @click="reconnect()">{{ $t('reconnect') }}</v-list-item-title>
      </v-list-item>

    </v-list>
    </v-menu>
    <v-dialog 
        v-model="connectDialog"
        width="500"
      >

        <v-card>
          <v-card-title
            class="headline grey lighten-2"
            primary-title
          >
            {{ $t('no_server_title') }}
          </v-card-title>

          <v-card-text>
            {{ $t('no_server_sub') }}
          </v-card-text>

          <v-divider></v-divider>

          <v-card-actions>
            <v-spacer></v-spacer>
            <v-btn
              color="primary"
              text
              @click="connectDialog = false"
            >
              {{ $t('ok') }}
            </v-btn>
          </v-card-actions>
        </v-card>
      </v-dialog>
  </div>
</template>

<script>
import { mapState } from 'vuex';

export default {
  props: {
    vueWebsocket: {
      type: Boolean,
      default: true,
    },
  },
  methods: {
    reconnect() {
      this.$store.state.isConnecting = false;
      if (this.vueWebsocket) {
        this.$disconnect();
        this.$connect();
      }
    },
  },
  computed: mapState(['socketConnecting']),
  watch: {
    socketConnecting(newValue) {
      if (newValue) {
        this.$store.dispatch('game/register');
      }
    },
  },
  data() {
    return {
      connectDialog: false,
    }
  }
};
</script>
