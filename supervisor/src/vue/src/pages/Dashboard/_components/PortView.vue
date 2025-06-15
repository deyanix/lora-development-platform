<template>
  <q-card flat bordered>
    <q-item>
      <q-item-section>
        <q-item-label>
          {{ port.portName }}
        </q-item-label>
        <q-item-label caption>
          <template v-if="port.nodeId">
            {{ port.nodeId }}
          </template>
          <template v-else> (niepodłączone) </template>
        </q-item-label>
      </q-item-section>
      <q-space />
      <q-item-section class="col-auto">
        <div v-if="!port.nodeId">
          <q-btn icon="mdi-power-plug" flat round dense @click="connect()" />
        </div>
        <div v-else class="row q-gutter-xs">
          <q-btn icon="mdi-power-plug-off" flat round dense @click="disconnect()" />
          <q-btn icon="mdi-led-on" flat round dense @click="led()" />
          <q-btn icon="mdi-send" flat round dense @click="send()" />
        </div>
      </q-item-section>
    </q-item>
    <SerialMonitor :port="port" />
  </q-card>
</template>
<script setup lang="ts">
import { PortModel, PortService } from 'src/api/PortService';
import { NodeService } from 'src/api/NodeService';
import SerialMonitor from 'pages/Dashboard/_components/SerialMonitor.vue';

const props = defineProps<{ port: PortModel }>();

async function connect() {
  await PortService.connect(props.port.portName);
}

async function disconnect() {
  await PortService.disconnect(props.port.portName);
}

async function led() {
  if (!props.port.nodeId) return;

  await NodeService.toggleFlashing(props.port.nodeId);
}

async function send() {
  if (!props.port.nodeId) return;

  await NodeService.transmit(props.port.nodeId, 'abcdef');
}
</script>
