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
    async onTxDone(){
      console.log("txdone")
      if (autoFetch.value) {
        await fetch();
      }
    },
    async onTxStart(){
      console.log("txstart")
      if (autoFetch.value) {
        await fetch();
      }
    },
    async onRxDone(){
      console.log("rxdone")
      if (autoFetch.value) {
        await fetch();
      }
    },
    async onRxStart(){
      console.log("rxstart")
      if (autoFetch.value) {
        await fetch();
      }
    },
  });

  return { options, ports, nodes, autoFetch, fetch };
});
