<template>
  <div ref="chartRef" style="width: 100%; height: 500px" />
</template>

<script setup lang="ts">
import { onBeforeMount, onMounted, onUnmounted, ref, shallowRef, watch } from 'vue';
import * as echarts from 'echarts';
import { usePlatformStore } from 'stores/platform';
import { NodeUtilities } from 'src/utilities/NodeUtilities';
import { NodeMessage, NodeStatisticService } from 'src/api/NodeStatisticService';
import { onLoRaEvent } from 'src/composables/onLoRaEvent';

interface ChartItem {
  id: number;
  name: string;
  value: [string, number, number | undefined, NodeMessage];
}

const chartRef = ref<HTMLDivElement | null>(null);
const chartInstance = shallowRef<echarts.ECharts | null>(null);
const platform = usePlatformStore();
const seriesData = shallowRef<ChartItem[]>([]);
const updateTimer = ref<ReturnType<typeof setInterval>>();

function renderItem(
  params: echarts.CustomSeriesRenderItemParams,
  api: echarts.CustomSeriesRenderItemAPI,
): echarts.CustomSeriesRenderItemReturn {
  const categoryIndex = api.value(0);
  const startVal = api.value(1);
  const endVal = api.value(2);

  const startCoord = api.coord([startVal, categoryIndex]);
  const endCoord = api.coord([endVal, categoryIndex]);
  const coordSys = params.coordSys as echarts.CustomSeriesRenderItemParams['coordSys'] & {
    x: number;
    y: number;
    width: number;
    height: number;
  };

  if (!startCoord[0] || !endCoord[0] || !startCoord[1] || !coordSys) {
    return;
  }

  const size = api.size?.([0, 1]) ?? [];
  if (typeof size === 'number' || !size[1]) {
    return;
  }

  let x = startCoord[0];
  let width = endCoord[0] - x;

  if (x < coordSys.x) {
    width -= coordSys.x - x;
    x = coordSys.x;
  }
  if (x + width > coordSys.x + coordSys.width) {
    width = coordSys.x + coordSys.width - x;
  }

  if (width <= 0) {
    return;
  }

  const height = size[1] * 0.6;

  return {
    type: 'rect',
    transition: ['shape'],
    shape: {
      x: x,
      y: startCoord[1] - height / 2,
      width: width,
      height: height,
    },
    style: api.style(),
  };
}

/**
 * Funkcja uruchamiana w stałym interwale.
 * Odpowiada za przesuwanie osi czasu i usuwanie starych danych z pamięci.
 */
function updateTimeAxisAndCleanUp() {
  const now = Date.now();
  const timeWindow = 30 * 1000;
  const minTime = now - timeWindow;
  const maxTime = now + 1000;
  const limitTime = minTime - 5000;

  // OPTYMALIZACJA: Usuń elementy, które całkowicie zniknęły za lewą krawędzią
  const currentData = seriesData.value.filter(
    (item) => item.value[2] && item.value[2] >= limitTime,
  );
  if (currentData.length !== seriesData.value.length) {
    seriesData.value = currentData;
  }

  if (chartInstance.value) {
    chartInstance.value.setOption({
      xAxis: {
        min: minTime,
        max: maxTime,
      },
      series: [
        {
          data: seriesData.value,
        },
      ],
    });
  }
}

/**
 * Inicjalizuje wykres ECharts z początkową konfiguracją.
 */
function initChart() {
  if (chartRef.value) {
    chartInstance.value = echarts.init(chartRef.value);

    const option: echarts.EChartsOption = {
      tooltip: {
        trigger: 'item',
        formatter: (params) => {
          if (Array.isArray(params)) return '';

          const itemData = params.data as ChartItem;
          const msg = itemData.value[3];
          const duration = msg.duration.toFixed(0);

          return (
            `${msg.data}<br/>` +
            `<b>Czas trwania:</b> ${duration} ms<br/>` +
            `<b>Liczba odbiorów:</b> ${msg.receptions.length}`
          );
        },
      },
      grid: {
        containLabel: true,
        left: '20',
        right: '20',
        top: '60',
        bottom: '40',
      },
      xAxis: {
        type: 'time',
        axisLabel: {
          hideOverlap: true,
          formatter: (value: number) => echarts.time.format(value, '{HH}:{mm}:{ss}', false),
        },
      },
      yAxis: {
        type: 'category',
        data: platform.nodes.map((n) => NodeUtilities.formatId(n.id)),
        inverse: true,
        axisLabel: {
          interval: 0,
        },
      },
      series: {
        type: 'custom',
        renderItem,
        itemStyle: {
          opacity: 0.9,
          color: '#4CAF50',
        },
        encode: {
          x: [1, 2],
          y: 0,
        },
        data: seriesData.value,
      },
      animation: true,
      animationDuration: 500,
      animationDurationUpdate: 500,
      animationEasing: 'linear',
      animationEasingUpdate: 'linear',
    };

    chartInstance.value.setOption(option);
  }
}

// Obserwuj zmiany na liście węzłów, aby zaktualizować oś Y
watch(
  () => platform.nodes,
  (newNodes) => {
    if (chartInstance.value) {
      chartInstance.value.setOption({
        yAxis: {
          data: newNodes.map((n) => NodeUtilities.formatId(n.id)),
        },
      });
    }
  },
  { deep: true },
);

onBeforeMount(async () => {
  const messages = await NodeStatisticService.getMessages();
  seriesData.value = messages.map((msg) => createItem(msg)).filter((msg) => !!msg);
});

onMounted(() => {
  initChart();

  // Uruchomienie pętli aktualizującej oś czasu i czyszczącej dane
  updateTimer.value = setInterval(updateTimeAxisAndCleanUp, 500);

  const resizeObserver = new ResizeObserver(() => {
    chartInstance.value?.resize();
  });
  if (chartRef.value) {
    resizeObserver.observe(chartRef.value);
  }

  onUnmounted(() => {
    resizeObserver.disconnect();
    if (updateTimer.value) clearInterval(updateTimer.value);
    chartInstance.value?.dispose();
    chartInstance.value = null;
  });
});

function createItem(msg: NodeMessage): ChartItem | undefined {
  if (!msg.endDate || !msg.successful) return;

  const nodeName = NodeUtilities.formatId(msg.senderId);
  return {
    id: msg.eventId,
    name: nodeName,
    value: [nodeName, msg.startDate.getTime(), msg.endDate?.getTime(), msg],
  };
}

onLoRaEvent({
  onMessage(msg: NodeMessage) {
    const itemIndex = seriesData.value.findIndex((item) => item.id === msg.eventId);
    const oldItem: ChartItem | undefined = seriesData.value[itemIndex];
    const item = createItem(msg);
    if (!item) return;

    if (oldItem) {
      const updatedItem: ChartItem = {
        ...oldItem,
        value: item.value,
      };

      const newData = [...seriesData.value];
      newData[itemIndex] = updatedItem;
      seriesData.value = newData;
    } else {
      seriesData.value = [...seriesData.value, item];
    }
  },
  onClear() {
    seriesData.value = [];
  },
});
</script>
