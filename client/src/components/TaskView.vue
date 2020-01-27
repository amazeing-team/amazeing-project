<template>
  <v-row class="fill-height" justify="center"
         align="center" v-if="loadedTask && playerAvailable">
    <v-col :cols="5">
      <board :key="$route.params.id"/>
    </v-col>
    <v-col class="fill-height" :cols="7">
      <codemirror
        v-if="editor"
        v-model="code"
        :options="codeEditorOptions"
        class="ml-2 mr-2 mb-2"
        @input="onCodeChange" />
      <control-buttons
        :error-show="!valid"
        :has-next-step="hasNextStep"
        :instructions_sent="instructions_sent"
        :runing="run"
        @send-instructions="sendInstructionsLocal({
          instructions: code,
          taskID: task.id,
        })"
        @reset="reset()"
        @run="runInstructions()"
        @step="nextStep()"
        @pause="pause()" />
      <div v-if="showControl" class="mt-3 mb-3 ml-2 mr-2">
        <!-- Control for Playground -->
        <v-btn :disabled="instructions_sent" class="mr-2" @click="turnLeftLocal()">
          {{ $t('left') }}
        </v-btn>
        <v-btn :disabled="instructions_sent" @click="moveLocal()">
          {{ $t('move') }}
        </v-btn>
        <v-btn :disabled="instructions_sent" class="ml-2" @click="turnRightLocal()">
          {{ $t('right') }}
        </v-btn>
      </div>
      <div class="ml-2 mr-2">
        <v-sheet style="max-height: 200px; overflow-y: scroll;" class="mb-3">
          <div v-for="(logLine, i) in log" :key="i">
            {{ logLine }}
          </div>
        </v-sheet>
        <v-alert v-if="!valid" type="error" ><pre>{{ msg }}</pre></v-alert>
      </div>
    </v-col>
  </v-row>
</template>

<script>
import { codemirror } from 'vue-codemirror';
import 'codemirror/lib/codemirror.css';
import 'codemirror/theme/darcula.css';
import { mapGetters, mapState, mapActions } from 'vuex';
import Board from './Board.vue';
import ControlButtons from './ControlButtons.vue';

export default {
  name: 'TaskView',
  props: {
    // Used to disable the editor in the text
    editor: {
      type: Boolean,
      default: true,
    },
    // Task to display
    task: {
      type: Object,
      default: null,
    },
    // Show control button (playground)
    showControl: {
      type: Boolean,
      default: false,
    },
    initialCode: {
      type: String,
      default: '',
    },
  },
  components: {
    Board,
    codemirror,
    ControlButtons,
  },
  methods: {
    onCodeChange(newCode) {
      this.code = newCode;
      this.instructions_sent = false;
      this.reset();
    },
    reset() {
      clearInterval(this.interval);
      this.run = false;
      this.resetStep();
      if (!this.valid) {
        this.instructions_sent = false;
      }
    },
    sendInstructionsLocal(obj) {
      this.instructions_sent = true;
      this.sendInstructions(obj);
      this.$emit('submit', obj.instructions);
    },
    runInstructions() {
      this.run = true;
      this.interval = setInterval(() => {
        if (this.hasNextStep) {
          this.nextStep();
        } else {
          this.run = false;
          this.pause();
        }
      }, 500);
    },
    pause() {
      this.run = false;
      clearInterval(this.interval);
    },
    ...mapActions('game', [
      'chooseTask', 'sendInstructions', 'shutdown',
      'nextStep', 'resetStep', 'turnRight', 'turnLeft', 'move',
    ]),
    turnLeftLocal() {
      this.turnLeft();
    },
    turnRightLocal() {
      this.turnRight();
    },
    moveLocal() {
      this.move();
    },
  },
  computed: {
    ...mapState('tasks', ['loaded', 'tasks']),
    ...mapGetters('tasks', ['getTaskById']),
    ...mapGetters('game', ['hasNextStep']),
    ...mapState('game', ['log', 'msg', 'players', 'valid', 'win']),
    ...mapState('user', ['id']),
    /**
     * Check if player is available in model
     */
    playerAvailable() {
      for (const player of Object.values(this.players)) {
        if (player.uid === this.id) {
          return true;
        }
      }
      console.warn('received an request for another user???');
      return false;
    },
  },
  mounted() {
    this.code = this.initialCode;
    this.chooseTask({ task: this.task });
    this.loadedTask = true;
  },
  data() {
    return {
      run: false,
      instructions_sent: false,
      interval: null,
      loadedTask: false,
      code: '',
      codeEditorOptions: {
        tabSize: 2,
        styleActiveLine: true,
        lineNumbers: true,
        line: true,
        foldGutter: true,
        styleSelectedText: true,
        mode: 'text/javascript',
        matchBrackets: true,
        showCursorWhenSelecting: true,
        theme: 'darcula',
      },
    };
  },
};
</script>
