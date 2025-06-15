<template>
  <q-item class="bg-blue-grey-1">
    <q-item-section avatar>
      <q-avatar>
        <q-icon name="mdi-server-network" />
      </q-avatar>
    </q-item-section>
    <q-item-section v-if="node">
      <q-item-label class="text-bold"> {{ node?.id }} </q-item-label>
      <q-item-label caption>
        {{ node?.portName }}
      </q-item-label>
    </q-item-section>
    <q-item-section v-else>
      <q-item-label class="text-bold text-negative"> Nie znaleziono węzła </q-item-label>
    </q-item-section>
    <q-space />
    <q-item-section class="col-auto">
      <div class="row q-gutter-xs">
        <q-btn icon="mdi-send" flat round dense :disable="!node" @click="send" />
        <q-btn
          :icon="!node?.flashing ? 'mdi-led-off' : 'mdi-led-on'"
          flat
          round
          dense
          :disable="!node"
          @click="toggleFlashing"
        />
        <q-btn icon="mdi-antenna" flat dense round :disable="!node" @click="openRadioConfiguration">
          <q-tooltip>Konfiguracje radiowe</q-tooltip>
        </q-btn>
        <q-btn icon="mdi-cog" flat dense round :disable="!node" @click="openNodeConfiguration">
          <q-tooltip>Konfiguracje węzła</q-tooltip>
        </q-btn>
      </div>
    </q-item-section>
  </q-item>
</template>
<script setup lang="ts">
import { NodeModel, NodeService } from 'src/api/NodeService';
import { Dialog } from 'quasar';
import NodeRadioConfigurationDialog from 'pages/Node/_dialogs/NodeRadioConfigurationDialog.vue';
import { usePlatformStore } from 'stores/platform';
import NodeConfigurationDialog from 'pages/Node/_dialogs/NodeConfigurationDialog.vue';

const platform = usePlatformStore();
const props = defineProps<{ node?: NodeModel }>();

async function toggleFlashing() {
  if (!props.node) {
    return;
  }

  await NodeService.updateFlashing(props.node.id, !props.node.flashing);
  await platform.fetch();
}

async function send() {
  if (!props.node) return;

  await NodeService.transmit(props.node.id, 'abc\ndef');
}

function openRadioConfiguration() {
  Dialog.create({
    component: NodeRadioConfigurationDialog,
    componentProps: {
      node: props.node,
    },
  });
}

function openNodeConfiguration() {
  Dialog.create({
    component: NodeConfigurationDialog,
    componentProps: {
      node: props.node,
    },
  });
}
</script>
