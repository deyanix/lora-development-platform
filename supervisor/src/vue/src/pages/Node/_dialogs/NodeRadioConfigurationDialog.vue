<template>
  <q-dialog ref="dialogRef" persistent>
    <q-card style="width: 420px">
      <q-form @submit="onSubmit">
        <q-card-section class="row justify-between items-center q-pb-none">
          <div class="text-h6">Konfiguracje radiowe</div>
          <q-btn icon="mdi-close" flat dense round v-close-popup />
        </q-card-section>
        <q-card-section class="q-gutter-sm">
          <div class="row items-center">
            <div class="col-auto q-pr-sm">
              <q-checkbox v-model="changed.frequency" dense />
            </div>
            <div class="col">
              <q-input
                v-model.number="configuration.frequency"
                label="Częstotliwość"
                dense
                outlined
                :disable="!changed.frequency"
                suffix="Hz"
              />
            </div>
          </div>
          <div class="row items-center">
            <div class="col-auto q-pr-sm">
              <q-checkbox v-model="changed.bandwidth" dense />
            </div>
            <div class="col">
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
                :disable="!changed.bandwidth"
              />
            </div>
          </div>
          <div class="row items-center">
            <div class="col-auto q-pr-sm">
              <q-checkbox v-model="changed.power" dense />
            </div>
            <div class="col">
              <q-input
                v-model.number="configuration.power"
                label="Moc"
                dense
                outlined
                :disable="!changed.power"
                suffix="dBm"
              />
            </div>
          </div>
          <div class="row items-center">
            <div class="col-auto q-pr-sm">
              <q-checkbox v-model="changed.codingRate" dense />
            </div>
            <div class="col">
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
                :disable="!changed.codingRate"
              />
            </div>
          </div>
          <div class="row items-center">
            <div class="col-auto q-pr-sm">
              <q-checkbox v-model="changed.spreadingFactor" dense />
            </div>
            <div class="col">
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
                :disable="!changed.spreadingFactor"
              />
            </div>
          </div>
          <div class="row items-center">
            <div class="col-auto q-pr-sm">
              <q-checkbox v-model="changed.payloadLength" dense />
            </div>
            <div class="col">
              <q-input
                v-model.number="configuration.payloadLength"
                label="Rozmiar ładunku"
                dense
                outlined
                :disable="!changed.payloadLength"
                suffix="bit"
              />
            </div>
          </div>
          <div class="row items-center">
            <div class="col-auto q-pr-sm">
              <q-checkbox v-model="changed.preambleLength" dense />
            </div>
            <div class="col">
              <q-input
                v-model.number="configuration.preambleLength"
                label="Rozmiar preambuły"
                dense
                outlined
                :disable="!changed.preambleLength"
                suffix="bit"
              />
            </div>
          </div>
          <div class="row items-center">
            <div class="col-auto q-pr-sm">
              <q-checkbox v-model="changed.rxSymbolTimeout" dense />
            </div>
            <div class="col">
              <q-input
                v-model.number="configuration.rxSymbolTimeout"
                label="Limit czasu RX"
                dense
                outlined
                :disable="!changed.rxSymbolTimeout"
                suffix="symb."
              />
            </div>
          </div>
          <div class="row items-center">
            <div class="col-auto q-pr-sm">
              <q-checkbox v-model="changed.txTimeout" dense />
            </div>
            <div class="col">
              <q-input
                v-model.number="configuration.txTimeout"
                label="Limit czasu TX"
                dense
                outlined
                :disable="!changed.txTimeout"
                suffix="ms"
              />
            </div>
          </div>
          <div class="row items-center">
            <div class="col-auto q-pr-sm">
              <q-checkbox v-model="changed.enableCrc" dense />
            </div>
            <div class="col">
              <q-toggle
                v-model="configuration.enableCrc"
                label="CRC"
                dense
                :disable="!changed.enableCrc"
              />
            </div>
          </div>
          <div class="row items-center">
            <div class="col-auto q-pr-sm">
              <q-checkbox v-model="changed.iqInverted" dense />
            </div>
            <div class="col">
              <q-toggle
                v-model="configuration.iqInverted"
                label="Odwrócone IQ"
                dense
                :disable="!changed.iqInverted"
              />
            </div>
          </div>
        </q-card-section>
        <q-card-actions align="right">
          <q-btn type="submit" label="Zapisz" color="primary" rounded unelevated />
        </q-card-actions>
      </q-form>
    </q-card>
  </q-dialog>
</template>
<script setup lang="ts">
import { ref, watch } from 'vue';
import { NodeModel, NodeRadioConfiguration, NodeService } from 'src/api/NodeService';
import { usePlatformStore } from 'stores/platform';
import { Loading, useDialogPluginComponent } from 'quasar';

const { dialogRef, onDialogOK } = useDialogPluginComponent();
const platform = usePlatformStore();
const props = defineProps<{ node?: NodeModel }>();

const configuration = ref<Partial<NodeRadioConfiguration>>({ ...props.node?.radioConfiguration });
const changed = ref<Record<keyof NodeRadioConfiguration, boolean>>({
  enableCrc: false,
  iqInverted: false,
  frequency: false,
  bandwidth: false,
  codingRate: false,
  payloadLength: false,
  power: false,
  preambleLength: false,
  rxSymbolTimeout: false,
  spreadingFactor: false,
  txTimeout: false,
});

async function onSubmit() {
  if (!props.node) {
    return;
  }

  const changedConfigurationEntries = Object.entries(changed.value)
    .filter(([, value]) => value) //@ts-expect-error
    .map(([key]) => [key, configuration.value[key]]);
  const changedConfiguration: Partial<NodeRadioConfiguration> = Object.fromEntries(
    changedConfigurationEntries,
  );

  Loading.show({ group: 'node-radio-configuration' });
  try {
    await NodeService.updateRadioConfiguration(props.node.id, changedConfiguration);
    onDialogOK();
  } finally {
    await platform.fetch();
    Loading.hide('node-radio-configuration');
  }
}

watch(
  changed,
  () => {
    Object.entries(changed.value).forEach(([key, value]) => {
      if (!value) {
        //@ts-expect-error
        configuration.value[key] = props.node?.radioConfiguration[key];
      }
    });
  },
  { deep: true },
);
</script>
