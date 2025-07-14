<template>
  <q-card flat bordered>
    <q-item>
      <q-item-section>
        <q-item-label>
          {{ port.portName }}
        </q-item-label>
        <q-item-label caption>
          <template v-if="port.nodeId">
            {{ NodeUtilities.formatId(port.nodeId) }}
          </template>
          <template v-else> (niepodłączone) </template>
        </q-item-label>
      </q-item-section>
    </q-item>
    <SerialMonitor :port="port" />
  </q-card>
</template>
<script setup lang="ts">
import { PortModel } from 'src/api/PortService';
import SerialMonitor from 'pages/Dashboard/_components/SerialMonitor.vue';
import { NodeUtilities } from 'src/utilities/NodeUtilities';

defineProps<{ port: PortModel }>();
</script>
