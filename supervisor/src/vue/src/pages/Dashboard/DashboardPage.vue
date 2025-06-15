<template>
  <q-page class="q-pa-lg">
    <div>
      Status połączenia:
      <b :style="`color: ${websocketStore.isConnected ? 'green' : 'red'}`">
        {{ websocketStore.isConnected ? 'Połączono' : 'Rozłączono' }}
      </b>
    </div>

    <div class="row q-col-gutter-md">
      <div v-for="port in platformStore.ports" :key="port.portName" class="col-12 col-md-6">
        <DashboardPortView :port="port" />
      </div>
    </div>
  </q-page>
</template>
<script setup lang="ts">
import { useWebsocketStore } from 'stores/websocket';
import { usePlatformStore } from 'stores/platform';
import DashboardPortView from 'pages/Dashboard/_components/DashboardPortView.vue';

const websocketStore = useWebsocketStore();
const platformStore = usePlatformStore();
</script>
