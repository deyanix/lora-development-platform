<template>
  <q-page class="column">
    <div class="col-auto">
      <NodeHeader :node="node" />
      <q-separator />
    </div>
    <div v-if="node" class="node-page__main">
      <div class="node-page__panel scroll">
        <NodePanel :node="node" />
      </div>
      <div class="node-page__content">
        <NodeContent :node="node" class="full-height" />
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
import NodeContent from 'pages/Node/_components/NodeContent.vue';

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
.node-page {
  &__main {
    display: flex;
    flex-direction: column;
    justify-content: start;
    flex: 1 1 0;
    overflow: hidden;

    @media screen and (min-width: $breakpoint-md-min) {
      flex-direction: row-reverse;
    }
  }

  &__content {
    flex: 1 1 auto;
    overflow: hidden;

    @media screen and (min-width: $breakpoint-md-min) {
      width: 75%;
      height: 100% !important;
    }
  }

  &__panel {
    flex: 0 0 auto;

    @media screen and (max-width: $breakpoint-sm-max) {
      border-bottom: 1px solid $separator-color;
    }

    @media screen and (min-width: $breakpoint-md-min) {
      border-left: 1px solid $separator-color;
      width: 25%;
      height: 100% !important;
    }
  }
}
</style>
