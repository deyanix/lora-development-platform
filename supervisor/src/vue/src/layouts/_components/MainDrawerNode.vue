<template>
  <q-item
    clickable
    dense
    :to="{ name: 'Node', params: { id: node.id } }"
    exact
    class="main-drawer-node"
    :class="{ 'main-drawer-node--persistent': model.length > 0 }"
  >
    <q-item-section avatar>
      <q-avatar>
        <q-checkbox v-model="model" dense :val="node.id" />
        <q-icon name="mdi-server-network" />
      </q-avatar>
    </q-item-section>
    <q-item-section>
      <q-item-label>
        {{ NodeUtilities.formatId(node.id) }}
      </q-item-label>
    </q-item-section>
    <q-space />
    <q-item-section class="col-auto q-px-md">
      <StatusIndicator :status="node.connected" />
    </q-item-section>
  </q-item>
</template>
<script setup lang="ts">
import StatusIndicator from 'components/StatusIndicator.vue';
import { NodeModel } from 'src/api/NodeService';
import { NodeUtilities } from 'src/utilities/NodeUtilities';

defineProps<{ node: NodeModel }>();
const model = defineModel<string[]>({ required: true });
</script>
<style lang="scss">
.main-drawer-node {
  .q-checkbox {
    display: none;
  }

  &--persistent,
  &:hover {
    .q-checkbox {
      display: block;
    }

    .q-icon {
      display: none;
    }
  }
}
</style>
