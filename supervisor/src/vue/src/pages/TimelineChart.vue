<template>
  <div id="chart">
    <apexchart type="rangeBar" height="350" :options="chartOptions" :series="series"></apexchart>
  </div>
</template>
<script setup lang="ts">
import { computed, shallowRef } from 'vue';

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
]);

const series = computed(() => [
  {
    data: seriesData.value,
  },
]);

const chartOptions = computed(() => ({
  chart: {
    height: 350,
    type: 'rangeBar',
  },
  plotOptions: {
    bar: {
      horizontal: true,
    },
  },
  xaxis: {
    type: 'datetime',
  },
}));
</script>
