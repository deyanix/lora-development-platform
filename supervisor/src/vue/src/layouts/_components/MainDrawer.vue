<template>
  <q-drawer v-model="opened" bordered class="main-drawer">
    <q-list class="full-height column no-wrap">
      <q-item clickable :to="{ name: 'Dashboard' }" exact>
        <q-item-section avatar>
          <q-avatar icon="mdi-view-dashboard" />
        </q-item-section>
        <q-item-section> Dashboard </q-item-section>
      </q-item>
      <q-item clickable :to="{ name: 'Statistics' }" exact>
        <q-item-section avatar>
          <q-avatar icon="mdi-poll" />
        </q-item-section>
        <q-item-section> Statystyki </q-item-section>
      </q-item>
      <q-separator />
      <MainDrawerNode
        v-for="node in platform.nodes"
        :key="node.id"
        v-model="selected"
        :node="node"
      />
      <q-space />
      <q-item>
        <q-item-section avatar class="col">
          <div class="row items-center q-gutter-xs">
            <q-checkbox
              v-model="allSelected"
              dense
              :label="selected.length === 0 ? 'Zaznacz wszystko' : `Wybrano ${selected.length}`"
            />
          </div>
        </q-item-section>
        <q-item-section class="col-auto">
          <div class="row q-gutter-xs">
            <q-btn
              icon="mdi-antenna"
              dense
              flat
              round
              :disable="selected.length === 0"
              @click="openRadioConfiguration"
            />
            <q-btn
              icon="mdi-cog"
              dense
              flat
              round
              :disable="selected.length === 0"
              @click="openNodeConfiguration"
            />
          </div>
        </q-item-section>
      </q-item>
    </q-list>
  </q-drawer>
</template>
<script setup lang="ts">
import { usePlatformStore } from 'stores/platform';
import { computed, ref } from 'vue';
import MainDrawerNode from 'layouts/_components/MainDrawerNode.vue';
import { Dialog } from 'quasar';
import NodeRadioConfigurationDialog from 'components/Node/NodeRadioConfigurationDialog.vue';
import NodeConfigurationDialog from 'components/Node/NodeConfigurationDialog.vue';

const platform = usePlatformStore();

const opened = defineModel<boolean>('opened', { required: true });

const selected = ref<string[]>([]);
const selectedNodes = computed(() => platform.nodes.filter((n) => selected.value.includes(n.id)));

const allSelected = computed<boolean | null>({
  get: () => {
    if (selected.value.length === 0) {
      return false;
    }
    if (platform.nodes.every((n) => selected.value.includes(n.id))) {
      return true;
    }
    return null;
  },
  set: (val) => {
    if (val === true) {
      selected.value = platform.nodes.map((n) => n.id);
    } else if (val === false) {
      selected.value = [];
    }
  },
});

function openRadioConfiguration() {
  Dialog.create({
    component: NodeRadioConfigurationDialog,
    componentProps: {
      nodes: selectedNodes.value,
    },
  });
}

function openNodeConfiguration() {
  Dialog.create({
    component: NodeConfigurationDialog,
    componentProps: {
      nodes: selectedNodes.value,
    },
  });
}
</script>
<style lang="scss">
.main-drawer {
  .q-item {
    flex-shrink: 0;
  }

  .q-item:last-child {
    position: sticky;
    bottom: 0;
    background-color: white;
    border-top: 1px solid $separator-color;
  }
}
</style>
