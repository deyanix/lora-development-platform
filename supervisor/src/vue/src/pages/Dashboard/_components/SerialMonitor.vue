<template>
  <div class="serial-monitor">
    <q-scroll-area style="height: 200px; max-width: 100%">
      <div v-for="(msg, index) in messages" :key="index">
        {{ msg.date.toISOString() }}
        {{ msg.type === 'rx' ? '<' : '>' }}
        {{ msg.message }}
      </div>
    </q-scroll-area>
  </div>
</template>
<script setup lang="ts">
import { Ref, ref } from 'vue';
import { onLoRaEvent } from 'src/composables/onLoRaEvent';
import { PortModel } from 'src/api/PortService';

interface SerialMonitorMessage {
  type: 'rx' | 'tx';
  date: Date;
  message: string;
}

const props = defineProps<{ port: PortModel }>();

const messages = ref<SerialMonitorMessage[]>([]);

const txBuffer = ref<string>('');
const rxBuffer = ref<string>('');

function processSerialBuffer(
  bufferRef: Ref<string>,
  type: 'tx' | 'rx',
  allMessages: typeof messages,
) {
  let currentBuffer = bufferRef.value;
  let terminatorIndex;

  while (currentBuffer.includes('\n') || currentBuffer.includes('\r')) {
    terminatorIndex = currentBuffer.search(/[\r\n]/);
    if (terminatorIndex === -1) {
      break;
    }

    const line = currentBuffer.substring(0, terminatorIndex).trim();
    currentBuffer = currentBuffer.substring(terminatorIndex + 1).replace(/^[\r\n]+/, '');
    if (line.length === 0) continue;

    allMessages.value.push({
      type: type,
      date: new Date(),
      message: line,
    });
  }

  bufferRef.value = currentBuffer;
}

onLoRaEvent({
  onSerialTx(evt) {
    if (evt.port === props.port.port) {
      txBuffer.value += evt.message;
      processSerialBuffer(txBuffer, 'tx', messages);
    }
  },
  onSerialRx(evt) {
    if (evt.port === props.port.port) {
      rxBuffer.value += evt.message;
      processSerialBuffer(rxBuffer, 'rx', messages);
    }
  },
});
</script>

<style lang="scss">
.serial-monitor {
  background: black;
  color: white;
  font-family: 'Consolas', 'Noto Sans Mono', monospace;
  padding: 12px;
  border-radius: 4px;
}
</style>
