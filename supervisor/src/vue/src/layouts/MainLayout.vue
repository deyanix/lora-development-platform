<template>
  <q-layout class="main-layout" view="hHh Lpr lFf">
    <MainHeader v-model:opened="opened" />
    <MainDrawer v-model:opened="opened" />
    <MainFooter />
    <q-page-container>
      <router-view />
    </q-page-container>
  </q-layout>
</template>

<script setup lang="ts">
import MainDrawer from 'layouts/_components/MainDrawer.vue';
import MainFooter from 'layouts/_components/MainFooter.vue';
import MainHeader from 'layouts/_components/MainHeader.vue';
import { ref } from 'vue';

const opened = ref<boolean>(true);
</script>
<style lang="scss">
.main-layout .q-page-container {
  background: $grey-1;
}
</style>
