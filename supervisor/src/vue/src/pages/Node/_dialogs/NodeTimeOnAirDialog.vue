<template>
  <q-dialog ref="dialogRef" persistent>
    <q-card style="width: 420px">
      <q-form @submit="onSubmit">
        <q-card-section class="row justify-between items-center q-pb-none">
          <div class="text-h6">Time on Air</div>
          <q-btn icon="mdi-close" flat dense round v-close-popup />
        </q-card-section>
        <q-card-section class="q-gutter-sm">
          <q-input
            v-model.number="payloadLength"
            label="Długość ładunku"
            dense
            outlined
            suffix="B"
            debounce="500"
          />

          <q-input
            v-model.number="timeOnAir"
            label="Time on Air"
            dense
            outlined
            suffix="ms"
            readonly
          />
        </q-card-section>
      </q-form>
    </q-card>
  </q-dialog>
</template>

<script setup lang="ts">
import { ref, watch } from 'vue';
import { NodeModel, NodeService } from 'src/api/NodeService';
import { useDialogPluginComponent } from 'quasar';

const { dialogRef } = useDialogPluginComponent();
const props = defineProps<{ node?: NodeModel }>();

const loading = ref<boolean>(false);
const payloadLength = ref<number>(10);
const timeOnAir = ref<number>();

async function onSubmit() {
  if (!props.node) {
    return;
  }

  loading.value = true;
  try {
    timeOnAir.value = await NodeService.getTimeOnAir(props.node.id, payloadLength.value);
  } catch {
    timeOnAir.value = undefined;
  } finally {
    loading.value = false;
  }
}

watch(payloadLength, () => onSubmit(), { immediate: true });
</script>
