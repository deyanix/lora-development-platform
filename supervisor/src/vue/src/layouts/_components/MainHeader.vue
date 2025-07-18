<template>
  <q-header bordered>
    <q-toolbar>
      <q-btn icon="mdi-menu" flat round dense @click="opened = !opened" />
      <q-toolbar-title> LoRa Supervisor </q-toolbar-title>

      <q-btn icon="mdi-connection" round flat @click="openSerialConnector">
        <q-badge v-if="portConnectedCount > 0" color="positive" floating rounded align="top">
          {{ portConnectedCount }}
        </q-badge>
      </q-btn>
    </q-toolbar>
  </q-header>
</template>
<script setup lang="ts">
import { computed } from 'vue';
import SerialConnectorDialog from 'layouts/_components/SerialConnectorDialog.vue';
import { Dialog } from 'quasar';
import { usePlatformStore } from 'stores/platform';

const platform = usePlatformStore();

const opened = defineModel<boolean>('opened', { required: true });
const portConnectedCount = computed(() => platform.ports.filter((p) => p.connected).length);

function openSerialConnector() {
  Dialog.create({
    component: SerialConnectorDialog,
  });
}
</script>
