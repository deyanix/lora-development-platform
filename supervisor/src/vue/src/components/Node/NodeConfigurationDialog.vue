<template>
  <q-dialog ref="dialogRef" persistent>
    <q-card style="width: 640px">
      <q-form @submit="onSubmit">
        <q-card-section class="row justify-between items-center q-pb-none">
          <div>
            <div class="text-h6">
              Konfiguracje węzła
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
          <div class="col-12">
            <q-select
              v-model="configuration.mode"
              label="Tryb"
              :options="platform.options?.mode"
              emit-value
              map-options
              dense
              outlined
              clearable
            />
          </div>
          <div class="col-12 col-md-6">
            <q-select
              v-model="configuration.auto"
              label="Algorytm"
              :options="platform.options?.auto"
              emit-value
              map-options
              dense
              outlined
              clearable
            />
          </div>
          <div class="col-12 col-md-6">
            <q-input
              v-model.number="configuration.interval"
              label="Interwał"
              type="number"
              dense
              outlined
              suffix="ms"
            />
          </div>
          <div class="col-12 col-md-6">
            <q-select
              v-model.number="configuration.backoffIncrease"
              label="Opóźnienie ponowienia"
              :options="[
                { label: 'Wydłużane', value: true },
                { label: 'Stałe', value: false },
              ]"
              emit-value
              map-options
              dense
              outlined
              clearable
            />
          </div>
          <div class="col-12 col-md-6">
            <q-input
              v-model.number="configuration.initialBackoffMax"
              label="Maksymalne początkowe opóźnienie ponowienia"
              type="number"
              dense
              outlined
              suffix="ms"
            />
          </div>
          <div class="col-12 col-md-6">
            <q-select
              v-model.number="configuration.ackRequired"
              label="ACK"
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
          <div class="col-12 col-md-6">
            <q-input
              v-model.number="configuration.ackLifetime"
              label="Długość życia ACK"
              type="number"
              dense
              outlined
            />
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
import { ref } from 'vue';
import { NodeModel, NodeConfiguration, NodeService } from 'src/api/NodeService';
import { usePlatformStore } from 'stores/platform';
import { Loading, useDialogPluginComponent } from 'quasar';
import { ObjectUtilities } from 'src/utilities/ObjectUtilities';
import { NodeUtilities } from 'src/utilities/NodeUtilities';

const { dialogRef, onDialogOK } = useDialogPluginComponent();
const platform = usePlatformStore();
const props = defineProps<{ nodes: NodeModel[] }>();

const configuration = ref<Partial<NodeConfiguration>>(
  ObjectUtilities.common(props.nodes.map((n) => n.configuration)),
);

async function onSubmit() {
  Loading.show({ group: 'node-configuration' });
  try {
    await Promise.all(
      props.nodes.map((node) => NodeService.updateConfiguration(node.id, configuration.value)),
    );
    onDialogOK();
  } finally {
    await platform.fetch();
    Loading.hide('node-configuration');
  }
}
</script>
