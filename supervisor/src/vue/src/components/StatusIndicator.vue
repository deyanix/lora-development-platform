<template>
  <div class="status-indicator" :class="{ 'status-indicator--active': status }"></div>
</template>
<script setup lang="ts">
defineProps<{ status?: boolean }>();
</script>
<style lang="scss">
.status-indicator {
  width: 12px;
  height: 12px;
  border-radius: 50%;
  background-color: $grey-8;

  &--active {
    background-color: $positive;
  }
}
</style>
