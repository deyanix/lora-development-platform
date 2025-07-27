<template>
  <q-dialog ref="dialogRef" persistent>
    <q-card style="width: 420px">
      <q-form @submit="onSubmit">
        <q-card-section class="row justify-between items-center q-pb-none">
          <div class="text-h6">Wysyłanie wiadomości</div>
          <q-btn icon="mdi-close" flat dense round v-close-popup />
        </q-card-section>
        <q-card-section class="q-gutter-sm">
          <q-input v-model="payload" label="Treść ładunku" dense outlined />

          <q-input
            v-model.number="payloadLength"
            label="Długość ładunku"
            dense
            outlined
            suffix="B"
            readonly
          />
        </q-card-section>
        <q-card-actions align="right">
          <q-btn type="submit" label="Zapisz" color="primary" rounded unelevated> </q-btn>
        </q-card-actions>
      </q-form>
    </q-card>
  </q-dialog>
</template>

<script setup lang="ts">
import { computed, ref, watch } from 'vue';
import { NodeModel, NodeService } from 'src/api/NodeService';
import { useDialogPluginComponent } from 'quasar';

const { dialogRef, onDialogOK } = useDialogPluginComponent();
const props = defineProps<{ node?: NodeModel }>();

const loading = ref<boolean>(false);
const payload = ref<string>('');
const payloadLength = computed(() => new TextEncoder().encode(payload.value).byteLength);

async function onSubmit() {
  if (!props.node) {
    return;
  }

  loading.value = true;
  try {
    await NodeService.transmit(props.node.id, payload.value);
  } finally {
    onDialogOK();
    loading.value = false;
  }
}
</script>
