<template>
  <q-dialog ref="dialogRef" persistent>
    <q-card style="width: 420px">
      <q-form @submit="onSubmit">
        <q-card-section class="row justify-between items-center q-pb-none">
          <div class="text-h6">Konfiguracja węzła</div>
          <q-btn icon="mdi-close" flat dense round v-close-popup />
        </q-card-section>
        <q-card-section class="q-gutter-sm">
          <q-select
            v-model="configuration.mode"
            label="Tryb"
            :options="platform.options?.mode"
            emit-value
            map-options
            dense
            outlined
          />
          <q-select
            v-model="configuration.auto"
            label="Algorytm"
            :options="platform.options?.auto"
            emit-value
            map-options
            dense
            outlined
          />
          <q-input
            v-model.number="configuration.initialBackoffMax"
            label="Maksymalne początkowe opóźnienie ponowienia"
            type="number"
            dense
            outlined
            suffix="ms"
          />
          <q-input
            v-model.number="configuration.interval"
            label="Interwał"
            type="number"
            dense
            outlined
            suffix="ms"
          />
          <q-input
            v-model.number="configuration.ackLifetime"
            label="Długość życia ACK"
            type="number"
            dense
            outlined
          />
          <q-toggle v-model="configuration.ackRequired" label="Wymagane ACK" dense />
          <q-toggle
            v-model="configuration.backoffIncrease"
            label="Wydłużanie opóźnienia ponowienia"
            dense
          />
        </q-card-section>
        <q-card-actions align="right">
          <q-btn type="submit" label="Zapisz" color="primary" rounded unelevated />
        </q-card-actions>
      </q-form>
    </q-card>
  </q-dialog>
</template>

<script setup lang="ts">
import { ref } from 'vue';
import { NodeModel, NodeConfiguration, NodeService } from 'src/api/NodeService';
import { usePlatformStore } from 'stores/platform';
import { Loading, useDialogPluginComponent } from 'quasar';

const { dialogRef, onDialogOK } = useDialogPluginComponent();
const platform = usePlatformStore();
const props = defineProps<{ node?: NodeModel }>();

const configuration = ref<Partial<NodeConfiguration>>({ ...props.node?.configuration });

async function onSubmit() {
  if (!props.node) {
    return;
  }

  Loading.show({ group: 'node-configuration' });
  try {
    await NodeService.updateConfiguration(props.node.id, configuration.value);
    onDialogOK();
  } finally {
    await platform.fetch();
    Loading.hide('node-configuration');
  }
}
</script>
