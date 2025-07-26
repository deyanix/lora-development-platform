<template>
  <q-item class="bg-blue-grey-1">
    <q-item-section avatar>
      <q-avatar>
        <q-icon name="mdi-server-network" />
      </q-avatar>
    </q-item-section>
    <q-item-section v-if="node">
      <q-item-label class="text-bold">
        {{ NodeUtilities.formatId(node?.id) }}
      </q-item-label>
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
        <q-btn
          icon="mdi-signal-distance-variant"
          flat
          round
          dense
          :disable="!node"
          @click="openTimeOnAirCalculator"
        >
          <q-tooltip>Time on Air</q-tooltip>
        </q-btn>
        <q-btn icon="mdi-send" flat round dense :disable="!node" @click="send">
          <q-tooltip>Wysyłanie wiadomości</q-tooltip>
        </q-btn>
        <q-btn
          :icon="!node?.flashing ? 'mdi-led-off' : 'mdi-led-on'"
          flat
          round
          dense
          :disable="!node"
          @click="toggleFlashing"
        >
          <q-tooltip>Wskaźnik LED</q-tooltip>
        </q-btn>
        <q-btn icon="mdi-refresh" flat dense round :disable="!node" @click="resetAuto">
          <q-tooltip>Reset algorytmu</q-tooltip>
        </q-btn>
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
import { Dialog, Loading } from 'quasar';
import NodeRadioConfigurationDialog from 'components/Node/NodeRadioConfigurationDialog.vue';
import { usePlatformStore } from 'stores/platform';
import NodeConfigurationDialog from 'components/Node/NodeConfigurationDialog.vue';
import { NodeUtilities } from 'src/utilities/NodeUtilities';
import NodeTimeOnAirDialog from 'pages/Node/_dialogs/NodeTimeOnAirDialog.vue';
import NodeSendDialog from 'pages/Node/_dialogs/NodeSendDialog.vue';

const platform = usePlatformStore();
const props = defineProps<{ node?: NodeModel }>();

async function toggleFlashing() {
  if (!props.node) {
    return;
  }

  await NodeService.updateFlashing(props.node.id, !props.node.flashing);
  await platform.fetch();
}

function send() {
  Dialog.create({
    component: NodeSendDialog,
    componentProps: {
      node: props.node,
    },
  });
}

async function resetAuto() {
  if (!props.node) {
    return;
  }

  Loading.show({ group: 'node-reset-auto' });
  try {
    await NodeService.resetAuto(props.node?.id);
  } finally {
    await platform.fetch();
    Loading.hide('node-reset-auto');
  }
}

function openRadioConfiguration() {
  Dialog.create({
    component: NodeRadioConfigurationDialog,
    componentProps: {
      nodes: [props.node],
    },
  });
}

function openNodeConfiguration() {
  Dialog.create({
    component: NodeConfigurationDialog,
    componentProps: {
      nodes: [props.node],
    },
  });
}

function openTimeOnAirCalculator() {
  Dialog.create({
    component: NodeTimeOnAirDialog,
    componentProps: {
      node: props.node,
    },
  });
}
</script>
