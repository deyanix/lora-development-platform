<template>
  <div class="serial-monitor">
    <q-virtual-scroll
      virtual-scroll-item-size="21"
      :items="messages"
      v-slot="{ item, index }"
      style="height: 200px; max-width: 100%"
    >
      <div :key="index">
        {{ item.date.toISOString() }}
        {{ item.type === 'rx' ? '<' : '>' }}
        {{ item.message }}
      </div>
    </q-virtual-scroll>
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

function processSerialBuffer(type: 'tx' | 'rx') {
  const bufferRef = type === 'tx' ? txBuffer : rxBuffer;
  const altBufferRef = type === 'tx' ? rxBuffer : txBuffer;

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

    messages.value.unshift({
      type: type,
      date: new Date(),
      message: line,
    });
  }

  bufferRef.value = currentBuffer;
}

onLoRaEvent({
  onSerialTx(evt) {
    if (evt.portName === props.port.portName) {
      txBuffer.value += evt.message;
      processSerialBuffer('tx');
    }
  },
  onSerialRx(evt) {
    if (evt.portName === props.port.portName) {
      rxBuffer.value += evt.message;
      processSerialBuffer('rx');
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
}
</style>
