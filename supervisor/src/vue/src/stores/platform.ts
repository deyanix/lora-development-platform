import { defineStore } from 'pinia';
import { onBeforeMount, ref } from 'vue';
import { onLoRaEvent } from 'src/composables/onLoRaEvent';
import { PortModel, PortService } from 'src/api/PortService';
import { NodeModel, NodeOptions, NodeService } from 'src/api/NodeService';

export const usePlatformStore = defineStore('platform', () => {
  const ports = ref<PortModel[]>([]);
  const nodes = ref<NodeModel[]>([]);
  const options = ref<NodeOptions>({} as NodeOptions);
  const autoFetch = ref<boolean>(true);

  async function fetch() {
    [ports.value, nodes.value] = await Promise.all([
      PortService.getPorts(),
      NodeService.getNodes(),
    ]);
  }

  async function fetchOptions() {
    options.value = await NodeService.getOptions();
  }

  onBeforeMount(async () => {
    await fetchOptions();
    await fetch();
  });

  onLoRaEvent({
    async onConnect() {
      if (autoFetch.value) {
        await fetch();
      }
    },
    async onDisconnect() {
      if (autoFetch.value) {
        await fetch();
      }
    },
    onTxDone(evt) {
      console.log('TX DONE', evt.portName);
    },
    onTxStart(evt) {
      console.log('TX START', evt.portName, evt.message);
    },
    onRxDone(evt) {
      console.log('RX DONE', evt.portName, evt.message);
    },
    onRxStart(evt) {
      console.log('RX START', evt.portName);
    },
  });

  return { options, ports, nodes, autoFetch, fetch };
});
