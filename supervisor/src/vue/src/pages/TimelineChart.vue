<template>
  <div id="chart">
    <apexchart type="rangeBar" height="350" :options="chartOptions" :series="series"></apexchart>
  </div>
</template>
<script setup lang="ts">
import {computed, onMounted, onUnmounted, shallowRef} from 'vue';

const nodeLabels = ['Node A', 'Node B', 'Node C', 'Node D', 'Node E', 'Node F'];
let intervalId: number | null = null;
let timeUpdateIntervalId: number | null = null;

const currentTime = shallowRef(new Date().getTime());
const seriesData = shallowRef([
  {
    x: 'Code',
    y: [new Date('2019-03-02').getTime(), new Date('2019-03-04').getTime()],
  },
  {
    x: 'Test',
    y: [new Date('2019-03-04').getTime(), new Date('2019-03-08').getTime()],
  },
  {
    x: 'Validation',
    y: [new Date('2019-03-08').getTime(), new Date('2019-03-12').getTime()],
  },
  {
    x: 'Deployment',
    y: [new Date('2019-03-12').getTime(), new Date('2019-03-18').getTime()],
  },
  {
    x: 'Node F',
    y: [new Date('2019-03-12').getTime(), new Date('2019-03-18').getTime()],
  },
]);


const generateNewData = () => {
  const now = new Date().getTime();
  const newEvents: { x: string; y: number[] }[] = [];

  const numberOfNewEvents = Math.floor(Math.random() * 2) + 1;

  for (let i = 0; i < numberOfNewEvents; i++) {
    const randomNodeIndex = Math.floor(Math.random() * nodeLabels.length);
    const selectedLabel = nodeLabels[randomNodeIndex];

    const eventStartTimeOffset = Math.floor(Math.random() * 5 * 1000);
    const startTime = now - eventStartTimeOffset;

    const duration = Math.floor(Math.random() * 4 + 1) * 1000;
    const endTime = startTime + duration;

    newEvents.push({
      x: selectedLabel!,
      y: [startTime, endTime],
    });
  }

  let updatedSeriesData = [...seriesData.value, ...newEvents];

  const historyBuffer = 120 * 1000;

  const minTimeForRetention = now - historyBuffer;

  updatedSeriesData = updatedSeriesData.filter(event => event.y[1]! >= minTimeForRetention);
  updatedSeriesData = updatedSeriesData.sort((a, b) => {
    const indexA = nodeLabels.indexOf(a.x);
    const indexB = nodeLabels.indexOf(b.x);
    return indexA - indexB;
  });

  seriesData.value = updatedSeriesData;
};

generateNewData();

onMounted(() => {
  intervalId = setInterval(generateNewData, 1000) as unknown as number;
  timeUpdateIntervalId = setInterval(() => {
    currentTime.value = new Date().getTime();
  }, 1000) as unknown as number;
});

onUnmounted(() => {
  if (intervalId !== null) {
    clearInterval(intervalId);
  }
  if (timeUpdateIntervalId !== null) {
    clearInterval(timeUpdateIntervalId);
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

  const dynamicMaxTime = now + (10 * 1000);
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
          delay: 150
        },
        dynamicAnimation: {
          enabled: true,
          speed: 350
        }
      }
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
        formatter: function(val: number) {
          return new Date(val).toLocaleTimeString(undefined, { hour: '2-digit', minute: '2-digit', second: '2-digit', hour12: false });
        }
      },
      min: dynamicMinTime,
      max: dynamicMaxTime,
    }
  };
});
</script>
