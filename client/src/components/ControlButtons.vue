<template>
  <div>
    <v-btn
      v-if="showSendInstructions" class="ma-2" tile outlined color="success"
      id="send-instructions-btn"
      @click="sendInstructions()">
      <v-icon left>mdi-play</v-icon>
        {{ $t('send_instructions') }}
    </v-btn>
    <v-btn v-if="showRun" class="ma-2" tile outlined color="success"
      id="run-instructions-btn"
      @click="$emit('run')">
      <v-icon left>mdi-play</v-icon>
        {{ $t('run') }}
    </v-btn>
    <v-btn v-else-if="showPause" class="ma-2" tile outlined
      id="pause-btn"
      color="success"
      @click="$emit('pause')">
      <v-icon left>mdi-pause</v-icon>
        {{ $t('pause') }}
    </v-btn>
    <v-btn v-if="showStep" class="ma-2" tile outlined color="success"
      id="step-btn"
      @click="$emit('step')">
      <v-icon left>mdi-step-forward</v-icon>
        {{ $t('step') }}
    </v-btn>
    <v-btn v-if="showReset" class="ma-2" tile outlined color="error"
      id="reset-game-state-btn"
      @click="$emit('reset')">
      <v-icon left>mdi-skip-backward</v-icon>
        {{ $t('reset') }}
    </v-btn>
  </div>
</template>

<script>
export default {
  props: {
    runing: Boolean,
    errorShow: Boolean,
    instructions_sent: Boolean,
    hasNextStep: Boolean,
  },
  methods: {
    sendInstructions() {
      this.$emit('send-instructions');
    },
  },
  computed: {
    showStep() {
      if (!this.hasNextStep) {
        return false;
      }
      return !this.errorShown && this.instructions_sent;
    },
    showReset() {
      return !this.errorShown && this.instructions_sent;
    },
    showSendInstructions() {
      return this.errorShown || !this.instructions_sent;
    },
    showRun() {
      if (!this.hasNextStep) {
        return false;
      }
      return !this.errorShown && (this.instructions_sent && !this.runing);
    },
    showPause() {
      return !this.errorShown && (this.instructions_sent && this.runing);
    },
  },
};
</script>
