import { defineStore } from 'pinia';
import { onBeforeMount, ref } from 'vue';
import { onLoRaEvent } from 'src/composables/onLoRaEvent';
import { PortModel, PortService } from 'src/api/PortService';
import { NodeModel, NodeOptions, NodeService } from 'src/api/NodeService';
import { LoRaEvent, LoRaEventType } from 'stores/websocket';

const PlatformFetchEvents: string[] = [
  LoRaEventType.PORT_RECOGNIZED,
  LoRaEventType.PORT_CONNECT,
  LoRaEventType.PORT_DISCONNECT,
];

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
      const nodeId = Object.entries(evt)
        .find(([k]) => k === 'nodeId')
        ?.at(1);

      if (nodeId) {
        const arr = events.value[nodeId];

        if (!arr) {
          events.value[nodeId] = [evt];
        } else {
          arr.unshift(evt);

          if (arr.length > 100) {
            arr.splice(100, arr.length - 100);
          }
        }

        if (autoFetch.value && PlatformFetchEvents.includes(evt.name)) {
          await fetch();
        }
      }
    },
  });

  return { options, ports, nodes, events, autoFetch, fetch };
});
