<template>
  <q-virtual-scroll virtual-scroll-item-size="21" :items="events" v-slot="{ item }">
    <q-item :key="item.id">
      <q-item-section class="col-auto">
        <q-item-label caption>
          {{ item.date }}
        </q-item-label>
        <q-item-label>
          {{ item.name }}
          {{ item.rssi }}
          {{ item.snr }}
          {{ item.data }}
        </q-item-label>
      </q-item-section>
    </q-item>
  </q-virtual-scroll>
</template>
<script setup lang="ts">
import { computed } from 'vue';
import { LoRaEvent } from 'stores/websocket';
import { NodeModel } from 'src/api/NodeService';
import { usePlatformStore } from 'stores/platform';

const platform = usePlatformStore();

const props = defineProps<{ node: NodeModel }>();

const events = computed<LoRaEvent[]>(() => platform.events[props.node.id] ?? []);
</script>
