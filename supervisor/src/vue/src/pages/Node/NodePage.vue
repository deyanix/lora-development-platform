<template>
  <q-page class="column">
    <NodeHeader :node="node" />
    <q-separator />
    <div v-if="node" class="row col-md">
      <div class="col-12 col-md-9 relative-position">
        <div class="column absolute full-height full-width">
          <q-virtual-scroll
            virtual-scroll-item-size="21"
            :items="events"
            v-slot="{ item }"
            class="col-auto"
            style="flex-grow: 0; flex-shrink: 1"
          >
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
        </div>
      </div>
      <div class="node-panel col-12 col-md-3 bg-white">
        <NodePanel :node="node" />
      </div>
    </div>
  </q-page>
</template>
<script setup lang="ts">
import { usePlatformStore } from 'stores/platform';
import { computed, onBeforeMount } from 'vue';
import { useRoute } from 'vue-router';
import NodeHeader from 'pages/Node/_components/NodeHeader.vue';
import NodePanel from 'pages/Node/_components/NodePanel.vue';
import { NodeModel } from 'src/api/NodeService';
import { LoRaEvent } from 'stores/websocket';

const $route = useRoute();
const platform = usePlatformStore();
const node = computed<NodeModel | undefined>(() =>
  platform.nodes.find((n) => n.id === $route.params.id),
);
const events = computed<LoRaEvent[]>(
  () => (node.value ? platform.events[node.value?.id] : undefined) ?? [],
);

onBeforeMount(async () => {
  await platform.fetch();
});
</script>
<style lang="scss">
.node-panel {
  border-left: 1px solid $separator-color;
}
</style>
