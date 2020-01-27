<template>
  <v-card class="fill-height mx-auto" width="90%">
    <v-toolbar>
      <v-toolbar-title>
        {{ $t('rooms') }}
      </v-toolbar-title>
      <v-spacer/>
      <v-btn text :to="{ name: 'tournaments'}">
        🤺Tournaments
      </v-btn>
      <v-toolbar-items class="hidden-sm-and-down">
        <new-room-dialog :inputData.sync="dialogData" @submit="validate()"/>
      </v-toolbar-items>
    </v-toolbar>
    <v-list>
      <v-list-item-group color="primary">
        <v-list-item v-for="room in roomValid" :key="room.id">
          <v-list-item-content>
            <v-list-item-title @click="join(room)">
              {{ room.name }}
              <span v-if="Object.keys(room.game.players).length === 0">
                (empty)
              </span>
              <span v-else>
                ({{ room.game.players.length }} {{ $t('players') }})
              </span>
            </v-list-item-title>
          </v-list-item-content>
        </v-list-item>
      </v-list-item-group>
    </v-list>
  </v-card>
</template>

<script>
import { mapState, mapActions } from 'vuex';
import NewRoomDialog from './NewRoomDialog.vue';

export default {
  name: 'Lobby',
  components: { NewRoomDialog },
  data() {
    return {
      roomName: '',
      dialogData: {
        name: '',
        width: 20,
        height: 20,
      },
    };
  },
  methods: {
    ...mapActions('rooms', ['createRoom', 'joinRoom']),
    join({ id }) {
      this.joinRoom({ id });
      this.$router.push({ name: 'room', params: { id } });
    },
    validate() {
      this.createRoom({
        name: this.dialogData.name,
        width: parseInt(this.dialogData.width, 10),
        height: parseInt(this.dialogData.height, 10),
      });
    },
  },
  computed: {
    ...mapState('rooms', ['rooms']),
    formValid() {
      return this.roomName.trim().length > 3;
    },
    roomValid() {
      return this.rooms.filter(room => !room.name.startsWith('Tournament'));
    },
  },
};
</script>
