<template>
  <q-page padding>
    <div>
      Status połączenia:
      <b :style="`color: ${websocketStore.isConnected ? 'green' : 'red'}`">
        {{ websocketStore.isConnected ? 'Połączono' : 'Rozłączono' }}
      </b>
    </div>

    <div class="row q-col-gutter-md">
      <div v-for="port in platformStore.ports" :key="port.port" class="col-12 col-md-6">
        <PortView :port="port" />
      </div>
    </div>
  </q-page>
</template>
<script setup lang="ts">
import { PortModel, PortService } from 'src/api/PortService';
import { NodeService } from 'src/api/NodeService';
import { useWebsocketStore } from 'stores/websocket';
import { usePlatformStore } from 'stores/platform';
import PortView from 'pages/Dashboard/_components/PortView.vue';

const websocketStore = useWebsocketStore();
const platformStore = usePlatformStore();
</script>
