import { defineStore } from 'pinia';
import { onBeforeMount, ref } from 'vue';
import { onLoRaEvent } from 'src/composables/onLoRaEvent';
import { PortModel, PortService } from 'src/api/PortService';
import { NodeModel, NodeOptions, NodeService } from 'src/api/NodeService';
import { LoRaEvent } from 'stores/websocket';

export const usePlatformStore = defineStore('platform', () => {
  const ports = ref<PortModel[]>([]);
  const nodes = ref<NodeModel[]>([]);
  const events = ref<Record<string, LoRaEvent[]>>({});
  const options = ref<NodeOptions>();
  const autoFetch = ref<boolean>(true);

  async function fetch() {
    if (options.value === undefined) await fetchOptions();

    [ports.value, nodes.value] = await Promise.all([
      PortService.getPorts(),
      NodeService.getNodes(),
    ]);

    await Promise.all(
      nodes.value.map(async (n) => (events.value[n.id] = await NodeService.getEvents(n.id))),
    );
  }

  async function fetchOptions() {
    options.value = await NodeService.getOptions();
  }

  onBeforeMount(async () => {
    await fetchOptions();
    await fetch();
  });

  onLoRaEvent({
    async onWebsocketConnect() {
      if (autoFetch.value) {
        await fetchOptions();
        await fetch();
      }
    },
    async onEvent(evt) {
      if (!evt.name.startsWith('SERIAL') && autoFetch.value) {
        await fetch();
      }
    },
  });

  return { options, ports, nodes, events, autoFetch, fetch };
});
