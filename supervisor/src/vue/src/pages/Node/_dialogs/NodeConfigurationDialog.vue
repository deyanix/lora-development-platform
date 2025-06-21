<template>
  <q-dialog ref="dialogRef" persistent>
    <q-card style="width: 420px">
      <q-form @submit="onSubmit">
        <q-card-section class="row justify-between items-center q-pb-none">
          <div class="text-h6">Konfiguracja węzła</div>
          <q-btn icon="mdi-close" flat dense round v-close-popup />
        </q-card-section>
        <q-card-section class="q-gutter-sm">
          <div class="row items-center">
            <div class="col-auto q-pr-sm">
              <q-checkbox v-model="changed.mode" dense />
            </div>
            <div class="col">
              <q-select
                v-model="configuration.mode"
                label="Tryb"
                :options="platform.options?.mode"
                emit-value
                map-options
                dense
                outlined
                :disable="!changed.mode"
              />
            </div>
          </div>
          <div class="row items-center">
            <div class="col-auto q-pr-sm">
              <q-checkbox v-model="changed.auto" dense />
            </div>
            <div class="col">
              <q-select
                v-model="configuration.auto"
                label="Algorytm"
                :options="platform.options?.auto"
                emit-value
                map-options
                dense
                outlined
                :disable="!changed.auto"
              />
            </div>
          </div>
          <div class="row items-center">
            <div class="col-auto q-pr-sm">
              <q-checkbox v-model="changed.initialBackoffMax" dense />
            </div>
            <div class="col">
              <div class="row q-col-gutter-sm">
                <q-input
                  v-model.number="configuration.initialBackoffMax"
                  label="Maksymalne początkowe opóźnienie ponowienia"
                  type="number"
                  dense
                  outlined
                  :disable="!changed.initialBackoffMax"
                  class="col"
                />
              </div>
            </div>
          </div>
          <div class="row items-center">
            <div class="col-auto q-pr-sm">
              <q-checkbox v-model="changed.interval" dense />
            </div>
            <div class="col">
              <q-input
                v-model.number="configuration.interval"
                label="Interwał"
                type="number"
                dense
                outlined
                :disable="!changed.interval"
              />
            </div>
          </div>
          <div class="row items-center">
            <div class="col-auto q-pr-sm">
              <q-checkbox v-model="changed.ackLifetime" dense />
            </div>
            <div class="col">
              <q-input
                v-model.number="configuration.ackLifetime"
                label="Długość życia ACK"
                type="number"
                dense
                outlined
                :disable="!changed.ackLifetime"
              />
            </div>
          </div>
          <div class="row items-center">
            <div class="col-auto q-pr-sm">
              <q-checkbox v-model="changed.ackRequired" dense />
            </div>
            <div class="col">
              <q-toggle
                v-model="configuration.ackRequired"
                label="Wymagane ACK"
                dense
                :disable="!changed.ackRequired"
              />
            </div>
          </div>
          <div class="row items-center">
            <div class="col-auto q-pr-sm">
              <q-checkbox v-model="changed.backoffIncrease" dense />
            </div>
            <div class="col">
              <q-toggle
                v-model="configuration.backoffIncrease"
                label="Wydłużanie opóźnienia ponowienia"
                dense
                :disable="!changed.backoffIncrease"
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
import { NodeModel, NodeConfiguration, NodeService } from 'src/api/NodeService'; // Upewnij się, że ścieżka do NodeService jest poprawna
import { usePlatformStore } from 'stores/platform'; // Upewnij się, że ścieżka do platformy jest poprawna
import { Loading, useDialogPluginComponent } from 'quasar';

const { dialogRef, onDialogOK } = useDialogPluginComponent();
const platform = usePlatformStore();
const props = defineProps<{ node?: NodeModel }>();

const configuration = ref<Partial<NodeConfiguration>>({ ...props.node?.configuration });
const changed = ref<Record<keyof NodeConfiguration, boolean>>({
  mode: false,
  auto: false,
  initialBackoffMax: false,
  interval: false,
  ackRequired: false,
  ackLifetime: false,
  backoffIncrease: false
});

async function onSubmit() {
  if (!props.node) {
    return;
  }

  const changedConfigurationEntries = Object.entries(changed.value)
    .filter(([, value]) => value)
    .map(([key]) => [key, configuration.value[key as keyof NodeConfiguration]]);
  const changedConfiguration: Partial<NodeConfiguration> = Object.fromEntries(
    changedConfigurationEntries,
  );

  Loading.show({ group: 'node-configuration' });
  try {
    await NodeService.updateConfiguration(props.node.id, changedConfiguration);
    onDialogOK();
  } finally {
    await platform.fetch();
    Loading.hide('node-configuration');
  }
}

watch(
  changed,
  () => {
    Object.entries(changed.value).forEach(([key, value]) => {
      if (!value) {
        // @ts-ignore
        configuration.value[key] = props.node?.configuration[key];
      }
    });
  },
  { deep: true },
);
</script>
