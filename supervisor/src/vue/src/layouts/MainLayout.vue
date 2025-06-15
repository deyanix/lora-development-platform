<template>
  <q-layout view="lHh Lpr lFf">
    <q-header elevated>
      <q-toolbar>
        <q-toolbar-title> Quasar App </q-toolbar-title>

        <q-btn icon="mdi-connection" round flat @click="openSerialConnector">
          <q-badge v-if="portConnectedCount > 0" color="positive" floating rounded align="top">
            {{ portConnectedCount }}
          </q-badge>
        </q-btn>
      </q-toolbar>
    </q-header>

    <q-page-container>
      <router-view />
    </q-page-container>
  </q-layout>
</template>

<script setup lang="ts">
import { Dialog } from 'quasar';
import SerialConnectorDialog from 'components/SerialConnector/SerialConnectorDialog.vue';
import { usePlatformStore } from 'stores/platform';
import { computed } from 'vue';

const platform = usePlatformStore();

const portConnectedCount = computed(() => platform.ports.filter((p) => p.connected).length);

function openSerialConnector() {
  Dialog.create({
    component: SerialConnectorDialog,
  });
}
</script>
