<template>
  <q-virtual-scroll virtual-scroll-item-size="51" :items="events" v-slot="{ item }" class="bg-dark">
    <NodeContentItem :item="item" :key="item.id" />
  </q-virtual-scroll>
</template>
<script setup lang="ts">
import { computed } from 'vue';
import { LoRaEvent } from 'stores/websocket';
import { NodeModel } from 'src/api/NodeService';
import { usePlatformStore } from 'stores/platform';
import NodeContentItem from 'pages/Node/_components/NodeContentItem.vue';

const platform = usePlatformStore();

const props = defineProps<{ node: NodeModel }>();

const events = computed<LoRaEvent[]>(() => platform.events[props.node.id] ?? []);
</script>
