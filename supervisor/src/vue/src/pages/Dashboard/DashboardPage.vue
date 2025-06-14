<template>
  <q-page padding>
    <q-card flat bordered>
      <q-list separator>
        <q-item v-for="port in ports" :key="port.port">
          <q-item-section>
            <q-item-label>
              {{ port.port }}
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
              <q-btn icon="mdi-power-plug" flat round dense @click="connect(port)" />
            </div>
            <div v-else class="row q-gutter-xs">
              <q-btn icon="mdi-power-plug-off" flat round dense @click="disconnect(port)" />
              <q-btn icon="mdi-led-on" flat round dense @click="led(port.nodeId)" />
              <q-btn icon="mdi-send" flat round dense @click="send(port.nodeId)" />
            </div>
          </q-item-section>
        </q-item>
      </q-list>
    </q-card>

    <div class="q-mt-md">
      Status połączenia:
      <b :style="`color: ${websocketStore.isConnected ? 'green' : 'red'}`">
        {{ websocketStore.isConnected ? 'Połączono' : 'Rozłączono' }}
      </b>
    </div>

    <div class="console q-mt-md">
      <q-scroll-area style="height: 200px; max-width: 100%">
        <div v-for="(msg, index) in websocketStore.messages" :key="index">
          {{ msg }}
        </div>
      </q-scroll-area>
    </div>
  </q-page>
</template>
<script setup lang="ts">
import { onBeforeMount, ref } from 'vue';
import { PortModel, PortService } from 'src/api/PortService';
import { NodeService } from 'src/api/NodeService';
import { useWebsocketStore } from 'stores/lora';

const websocketStore = useWebsocketStore();
const ports = ref<PortModel[]>([]);

async function fetch() {
  ports.value = await PortService.getPorts();
}

async function connect(port: PortModel) {
  await PortService.connect(port.port);
  await fetch();
}

async function disconnect(port: PortModel) {
  await PortService.disconnect(port.port);
  await fetch();
}

async function led(nodeId: string) {
  await NodeService.toggleFlashing(nodeId);
}

async function send(nodeId: string) {
  await NodeService.transmit(nodeId, 'abcdef');
}

onBeforeMount(async () => {
  await fetch();
});
</script>

<style lang="scss">
.console {
  background: black;
  color: white;
  font-family: 'Consolas', 'Noto Sans Mono', monospace;
  padding: 12px;
  border-radius: 4px;
}
</style>
