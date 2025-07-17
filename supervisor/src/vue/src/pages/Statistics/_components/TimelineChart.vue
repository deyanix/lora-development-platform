<template>
  <div id="chart">
    <apexchart type="rangeBar" height="350" :options="chartOptions" :series="series"></apexchart>
  </div>
</template>
<script setup lang="ts">
import { computed, onMounted, onUnmounted, ref, shallowRef, watch } from 'vue';
import { usePlatformStore } from 'stores/platform';
import { NodeStatisticService } from 'src/api/NodeStatisticService';

const platform = usePlatformStore();

const interval = ref<ReturnType<typeof setInterval>>();
const currentTimeInterval = ref<ReturnType<typeof setInterval>>();

const currentTime = shallowRef(new Date().getTime());
const seriesData = shallowRef<{ x: string; y: number[] }[]>([]);

async function generateNewData() {
  const data = [];

  for (const node of platform.nodes) {
    const messages = await NodeStatisticService.getMessages(node.id);
    for (const message of messages) {
      if (!message.endDate) continue;

      data.push({ x: node.id, y: [message.startDate.getTime(), message.endDate.getTime()] });
    }

    if (messages.length === 0) {
      data.push({ x: node.id, y: [] });
    }
  }

  seriesData.value = data;
}

watch(
  () => platform.nodes,
  () => generateNewData(),
  { immediate: true },
);

onMounted(() => {
  interval.value = setInterval(async () => {
    await generateNewData();
  }, 900);

  currentTimeInterval.value = setInterval(() => {
    currentTime.value = new Date().getTime();
  }, 250);
});

onUnmounted(() => {
  if (interval.value) {
    clearInterval(interval.value);
  }
  if (currentTimeInterval.value) {
    clearInterval(currentTimeInterval.value);
  }
});

const series = computed(() => [
  {
    data: seriesData.value,
  },
]);

const chartOptions = computed(() => {
  const visibleWindowSize = 60 * 1000;
  const now = currentTime.value;

  const dynamicMaxTime = now + 10 * 1000;
  const dynamicMinTime = dynamicMaxTime - visibleWindowSize;

  return {
    chart: {
      height: 400,
      type: 'rangeBar',
      animations: {
        enabled: true,
        easing: 'linear',
        speed: 800,
        animateGradually: {
          enabled: true,
          delay: 150,
        },
        dynamicAnimation: {
          enabled: true,
          speed: 350,
        },
      },
    },
    plotOptions: {
      bar: {
        horizontal: true,
      },
    },
    xaxis: {
      type: 'datetime',
      datatimeUTC: true,
      labels: {
        formatter: function (val: number) {
          return new Date(val).toLocaleTimeString(undefined, {
            hour: '2-digit',
            minute: '2-digit',
            second: '2-digit',
            hour12: false,
          });
        },
      },
      min: dynamicMinTime,
      max: dynamicMaxTime,
    },
  };
});
</script>
