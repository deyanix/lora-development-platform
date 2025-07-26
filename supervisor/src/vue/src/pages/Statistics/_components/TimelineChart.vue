<template>
  <div id="chart">
    <apexchart
      ref="chart"
      type="rangeBar"
      height="350"
      :options="defaultChartOptions"
      :series="defaultChartData"
    />
  </div>
</template>
<script setup lang="ts">
import { onMounted, onUnmounted, ref, shallowRef, useTemplateRef, watch } from 'vue';
import { usePlatformStore } from 'stores/platform';
import { NodeStatisticService } from 'src/api/NodeStatisticService';
import { VueApexChartsComponent } from 'vue3-apexcharts';
import { NodeUtilities } from 'src/utilities/NodeUtilities';

const defaultChartData = [{ name: 'Wiadomości', data: [] }];

const platform = usePlatformStore();

const chart = useTemplateRef<VueApexChartsComponent>('chart');
const interval = ref<ReturnType<typeof setInterval>>();
const currentTimeInterval = ref<ReturnType<typeof setInterval>>();

type SeriesDataItem = { x: string; y?: number[]; eventId?: number; rangeName?: string };

const seriesData = shallowRef<SeriesDataItem[]>([]);

async function refreshData() {
  const data: SeriesDataItem[] = [];

  for (const node of platform.nodes) {
    const id = NodeUtilities.formatId(node.id);
    const messages = await NodeStatisticService.getMessagesBySenderId(node.id);
    for (const message of messages) {
      if (!message.endDate) continue;

      const existing = seriesData.value.find((item) => item.eventId === message.eventId);
      if (!existing) {
        data.push({
          x: id,
          y: [message.startDate.getTime(), message.endDate.getTime()],
          eventId: message.eventId,
        });
      }
    }

    if (messages.length === 0) {
      data.push({ x: id });
    }
  }

  console.log(data.map((i) => i.rangeName));

  seriesData.value = [...seriesData.value, ...data];
  await chart.value?.appendData([{ data }]);
}

function getChartOptions() {
  const visibleWindowSize = 60 * 1000;
  const now = new Date().getTime();

  const dynamicMaxTime = now + 1000;
  const dynamicMinTime = dynamicMaxTime - visibleWindowSize;
  return {
    chart: {
      height: 400,
      type: 'rangeBar',
      animations: {
        enabled: true,
        easing: 'linear',
        speed: 500,
        animateGradually: {
          enabled: true,
          delay: 150,
        },
        dynamicAnimation: {
          enabled: true,
          speed: 500,
        },
      },
      toolbar: {
        show: false,
      },
      zoom: {
        enabled: false,
        allowMouseWheelZoom: false,
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
}

async function refreshOptions() {
  await chart.value?.updateOptions(getChartOptions());
}

const defaultChartOptions = getChartOptions();

watch(
  () => platform.nodes,
  () => refreshData(),
  { immediate: true },
);

onMounted(() => {
  interval.value = setInterval(async () => {
    await refreshData();
  }, 900);

  currentTimeInterval.value = setInterval(async () => {
    await refreshOptions();
  }, 100);
});

onUnmounted(() => {
  if (interval.value) {
    clearInterval(interval.value);
  }

  if (currentTimeInterval.value) {
    clearInterval(currentTimeInterval.value);
  }
});

// const series = [
//   {
//     data: [],
//   },
// ];
//
// const chartOptions = computed(() => );
</script>
