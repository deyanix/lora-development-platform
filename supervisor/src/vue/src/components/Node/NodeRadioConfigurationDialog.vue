<template>
  <q-dialog ref="dialogRef" persistent>
    <q-card style="width: 640px">
      <q-form @submit="onSubmit">
        <q-card-section class="row justify-between items-center q-pb-none">
          <div>
            <div class="text-h6">
              Konfiguracje radiowe
              <q-badge v-if="nodes.length > 1" align="middle">
                {{ nodes.length }}
                <q-tooltip>
                  <div v-for="node in nodes" :key="node.id">
                    {{ NodeUtilities.formatId(node.id) }}
                  </div>
                </q-tooltip>
              </q-badge>
            </div>
            <div class="text-subtitle2 text-grey-8">
              <template v-if="nodes.length > 1"> Ustawienia dotyczą wielu urządzeń </template>
              <template v-else> {{ NodeUtilities.formatId(nodes[0]?.id) }} </template>
            </div>
          </div>
          <q-btn icon="mdi-close" flat dense round v-close-popup />
        </q-card-section>
        <q-card-section class="row q-col-gutter-sm">
          <div class="col-12 col-md-6">
            <q-input
              v-model.number="configuration.frequency"
              label="Częstotliwość"
              dense
              outlined
              suffix="Hz"
            />
          </div>
          <div class="col-12 col-md-6">
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
              clearable
            />
          </div>
          <div class="col-12 col-md-6">
            <q-input v-model.number="configuration.power" label="Moc" dense outlined suffix="dBm" />
          </div>
          <div class="col-12 col-md-6">
            <q-select
              v-model.number="configuration.iqInverted"
              label="IQ"
              :options="[
                { label: 'Odwrócone IQ', value: true },
                { label: 'Standardowe IQ', value: false },
              ]"
              emit-value
              map-options
              dense
              outlined
              clearable
            />
          </div>
          <div class="col-12 col-md-6">
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
              clearable
            />
          </div>
          <div class="col-12 col-md-6">
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
              clearable
            />
          </div>
          <div class="col-12 col-md-6">
            <q-input
              v-model.number="configuration.payloadLength"
              label="Rozmiar ładunku"
              dense
              outlined
              suffix="B"
            />
          </div>
          <div class="col-12 col-md-6">
            <q-input
              v-model.number="configuration.preambleLength"
              label="Rozmiar preambuły"
              dense
              outlined
              suffix="symb."
            />
          </div>
          <div class="col-12 col-md-6">
            <q-input
              v-model.number="configuration.rxSymbolTimeout"
              label="Limit czasu RX"
              dense
              outlined
              suffix="symb."
            />
          </div>
          <div class="col-12 col-md-6">
            <q-input
              v-model.number="configuration.txTimeout"
              label="Limit czasu TX"
              dense
              outlined
              suffix="ms"
            />
          </div>
          <div class="col-12 col-md-6">
            <q-select
              v-model.number="configuration.enableCrc"
              label="CRC"
              :options="[
                { label: 'Włączone', value: true },
                { label: 'Wyłączone', value: false },
              ]"
              emit-value
              map-options
              dense
              outlined
              clearable
            />
          </div>
        </q-card-section>
        <q-card-actions align="right">
          <q-btn type="submit" label="Zapisz" color="primary" rounded unelevated> </q-btn>
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
import { NodeUtilities } from 'src/utilities/NodeUtilities';
import { ObjectUtilities } from 'src/utilities/ObjectUtilities';

const { dialogRef, onDialogOK } = useDialogPluginComponent();
const platform = usePlatformStore();
const props = defineProps<{ nodes: NodeModel[] }>();

const configuration = ref<Partial<NodeRadioConfiguration>>(
  ObjectUtilities.common(props.nodes.map((n) => n.radioConfiguration)),
);

async function onSubmit() {
  Loading.show({ group: 'node-radio-configuration' });
  try {
    await Promise.all(
      props.nodes.map((node) => NodeService.updateRadioConfiguration(node.id, configuration.value)),
    );
    onDialogOK();
  } finally {
    await platform.fetch();
    Loading.hide('node-radio-configuration');
  }
}
</script>
