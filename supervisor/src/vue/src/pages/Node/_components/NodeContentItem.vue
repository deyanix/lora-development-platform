<template>
  <q-item :key="item.id" :dark="true">
    <q-item-section class="col-auto" style="font-family: 'Consolas', 'Noto Sans Mono', monospace">
      <q-item-label caption>
        {{ item.portName }}
        {{ date.formatDate(item.date, 'HH:mm:ss.SSS') }}
      </q-item-label>
      <q-item-label>
        <span class="text-bold">
          {{ item.name }}
        </span>
        {{ payload }}
      </q-item-label>
    </q-item-section>
  </q-item>
</template>
<script setup lang="ts">
import { date } from 'quasar';
import { LoRaEvent } from 'stores/websocket';
import { computed } from 'vue';

const props = defineProps<{ item: LoRaEvent }>();

const payload = computed(() => {
  const entries = Object.entries(props.item).filter(
    ([k]) => !['id', 'nodeId', 'portName', 'date', 'name'].includes(k),
  );
  if (entries.length === 0) return undefined;

  return Object.fromEntries(entries);
});
</script>
