<template>
  <q-dialog ref="dialogRef" persistent>
    <q-card style="width: 420px">
      <q-form @submit="onSubmit">
        <q-card-section class="row justify-between items-center q-pb-none">
          <div class="text-h6">Konfiguracje radiowe</div>
          <q-btn icon="mdi-close" flat dense round v-close-popup />
        </q-card-section>
        <q-card-section class="q-gutter-sm">
          <q-input
            v-model.number="configuration.frequency"
            label="Częstotliwość"
            dense
            outlined
            suffix="Hz"
          />
          <q-select
            v-model="configuration.bandwidth"
            label="Pasmo"
            :options="platform.options?.bandwidth"
            option-label="name"
            option-value="value"
            emit-value
            map-options
            dense
            outlined
          />
          <q-input v-model.number="configuration.power" label="Moc" dense outlined suffix="dBm" />
          <q-select
            v-model.number="configuration.codingRate"
            label="Korekcja błędów"
            :options="platform.options?.codingRate"
            option-label="name"
            option-value="value"
            emit-value
            map-options
            dense
            outlined
          />
          <q-select
            v-model.number="configuration.spreadingFactor"
            label="Współczynnik rozpraszania"
            :options="platform.options?.spreadingFactor"
            option-label="name"
            option-value="value"
            emit-value
            map-options
            dense
            outlined
          />
          <q-input
            v-model.number="configuration.payloadLength"
            label="Rozmiar ładunku"
            dense
            outlined
            suffix="bit"
          />
          <q-input
            v-model.number="configuration.preambleLength"
            label="Rozmiar preambuły"
            dense
            outlined
            suffix="symb."
          />
          <q-input
            v-model.number="configuration.rxSymbolTimeout"
            label="Limit czasu RX"
            dense
            outlined
            suffix="symb."
          />
          <q-input
            v-model.number="configuration.txTimeout"
            label="Limit czasu TX"
            dense
            outlined
            suffix="ms"
          />
          <q-toggle v-model="configuration.enableCrc" label="CRC" dense />
          <q-toggle v-model="configuration.iqInverted" label="Odwrócone IQ" dense />
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
import { NodeModel, NodeRadioConfiguration, NodeService } from 'src/api/NodeService';
import { usePlatformStore } from 'stores/platform';
import { Loading, useDialogPluginComponent } from 'quasar';

const { dialogRef, onDialogOK } = useDialogPluginComponent();
const platform = usePlatformStore();
const props = defineProps<{ node?: NodeModel }>();

const configuration = ref<Partial<NodeRadioConfiguration>>({ ...props.node?.radioConfiguration });

async function onSubmit() {
  if (!props.node) {
    return;
  }

  Loading.show({ group: 'node-radio-configuration' });
  try {
    await NodeService.updateRadioConfiguration(props.node.id, configuration.value);
    onDialogOK();
  } finally {
    await platform.fetch();
    Loading.hide('node-radio-configuration');
  }
}
</script>
