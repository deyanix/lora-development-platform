<template>
  <q-page class="column">
    <NodeHeader :node="node" />
    <q-separator />
    <div v-if="node" class="row col-md">
      <div class="col-12 col-md-9"></div>
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

const $route = useRoute();
const platform = usePlatformStore();
const node = computed<NodeModel | undefined>(() =>
  platform.nodes.find((n) => n.id === $route.params.id),
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
