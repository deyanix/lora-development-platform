<template>
  <q-layout class="main-layout" view="hHh Lpr lFf">
    <q-header bordered>
      <q-toolbar>
        <q-toolbar-title> LoRa Supervisor </q-toolbar-title>

        <q-btn icon="mdi-connection" round flat @click="openSerialConnector">
          <q-badge v-if="portConnectedCount > 0" color="positive" floating rounded align="top">
            {{ portConnectedCount }}
          </q-badge>
        </q-btn>
      </q-toolbar>
    </q-header>

    <q-drawer bordered :model-value="true">
      <q-list>
        <q-item clickable :to="{ name: 'Dashboard' }" exact>
          <q-item-section avatar>
            <q-avatar icon="mdi-view-dashboard" />
          </q-item-section>
          <q-item-section> Dashboard </q-item-section>
        </q-item>
        <q-separator />
        <q-item
          v-for="node in platform.nodes"
          :key="node.id"
          clickable
          dense
          :to="{ name: 'Node', params: { id: node.id } }"
          exact
        >
          <q-item-section avatar>
            <q-avatar>
              <q-icon name="mdi-server-network" />
            </q-avatar>
          </q-item-section>
          <q-item-section>
            <q-item-label> {{ node.id }} </q-item-label>
          </q-item-section>
          <q-space />
          <q-item-section class="col-auto q-px-md">
            <StatusIndicator :status="node.connected" />
          </q-item-section>
        </q-item>
      </q-list>
    </q-drawer>

    <q-page-container>
      <router-view />
    </q-page-container>
  </q-layout>
</template>

<script setup lang="ts">
import { Dialog } from 'quasar';
import SerialConnectorDialog from 'layouts/_components/SerialConnectorDialog.vue';
import { usePlatformStore } from 'stores/platform';
import { computed } from 'vue';
import StatusIndicator from 'components/StatusIndicator.vue';

const platform = usePlatformStore();

const portConnectedCount = computed(() => platform.ports.filter((p) => p.connected).length);

function openSerialConnector() {
  Dialog.create({
    component: SerialConnectorDialog,
  });
}
</script>
<style lang="scss">
.main-layout .q-page-container {
  background: $grey-1;
}
</style>
