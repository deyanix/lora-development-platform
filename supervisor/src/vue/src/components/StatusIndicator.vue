<template>
  <span
    class="status-indicator"
    :class="{ 'status-indicator--active': status, 'status-indicator--blink': blink }"
  />
</template>
<script setup lang="ts">
defineProps<{ status?: boolean; blink?: boolean }>();
</script>
<style lang="scss">
.status-indicator {
  width: 12px;
  height: 12px;
  display: inline-block;
  border-radius: 50%;
  background-color: $grey-8;

  &--active {
    background-color: $positive;
  }

  &--active#{&}--blink {
    animation: status-indicator-blinker 1s ease-in-out infinite;
  }
}

@keyframes status-indicator-blinker {
  30% {
    opacity: 0;
  }
  50% {
    opacity: 1;
  }
  90% {
    opacity: 1;
  }
}
</style>
