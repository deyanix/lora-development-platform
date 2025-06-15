<template>
  <q-dialog ref="dialogRef" persistent>
    <q-card style="width: 420px">
      <q-card-section class="row justify-between items-center">
        <div class="text-h6">Porty szeregowe</div>
        <q-btn icon="mdi-close" flat dense round v-close-popup />
      </q-card-section>
      <q-list v-if="platformStore.ports.length > 0" separator>
        <q-item v-for="port in platformStore.ports" :key="port.portName">
          <q-item-section side>
            <q-checkbox v-model="selected" :val="port.portName" dense />
          </q-item-section>
          <q-item-section>
            <q-item-label>
              {{ port.portName }}
            </q-item-label>
            <q-item-label caption>
              {{ port.nodeId ?? '(niepodłączony)' }}
            </q-item-label>
          </q-item-section>
          <q-space />
          <q-item-section class="col-auto q-px-sm">
            <StatusIndicator :status="port.connected" />
          </q-item-section>
        </q-item>
        <q-item>
          <q-item-section side>
            <q-checkbox v-model="selectedAll" dense />
          </q-item-section>
          <q-item-section> Zaznacz wszystko </q-item-section>
          <q-space />
          <q-item-section class="col-auto">
            <div class="row q-gutter-xs">
              <q-btn
                icon="mdi-power-plug-off"
                flat
                dense
                round
                @click="disconnect"
                :disable="selectedAll === false"
              >
                <q-tooltip>Rozłącz</q-tooltip>
              </q-btn>
              <q-btn
                icon="mdi-power-plug"
                flat
                dense
                round
                @click="connect"
                :disable="selectedAll === false"
              >
                <q-tooltip>Podłącz</q-tooltip>
              </q-btn>
            </div>
          </q-item-section>
        </q-item>
      </q-list>
      <q-card-section v-else class="text-center q-py-xl">
        Nie znaleziono podłączonych węzłów
      </q-card-section>
    </q-card>
  </q-dialog>
</template>
<script setup lang="ts">
import { Loading, useDialogPluginComponent } from 'quasar';
import { computed, onBeforeMount, ref } from 'vue';
import { usePlatformStore } from 'stores/platform';
import { PortService } from 'src/api/PortService';
import StatusIndicator from 'components/StatusIndicator.vue';

const { dialogRef } = useDialogPluginComponent();
const platformStore = usePlatformStore();
const selected = ref<string[]>([]);

const selectedAll = computed<boolean | null>({
  get: () => {
    if (selected.value.length === 0) {
      return false;
    } else if (platformStore.ports.every((p) => selected.value.includes(p.portName))) {
      return true;
    } else {
      return null;
    }
  },
  set: (val) => {
    if (val === true) {
      selected.value = platformStore.ports.map((p) => p.portName);
    } else if (val === false) {
      selected.value = [];
    }
  },
});

async function connect() {
  Loading.show();
  try {
    await Promise.all(selected.value.map((port) => PortService.connect(port)));
  } finally {
    Loading.hide();
  }
}

async function disconnect() {
  Loading.show();
  try {
    await Promise.all(selected.value.map((port) => PortService.disconnect(port)));
  } finally {
    Loading.hide();
  }
}

onBeforeMount(async () => {
  await platformStore.fetch();
  selectedAll.value = true;
});
</script>
