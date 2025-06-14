import { defineStore } from 'pinia';
import { onBeforeMount, ref } from 'vue';
import { onLoRaEvent } from 'src/composables/onLoRaEvent';
import { PortModel, PortService } from 'src/api/PortService';

export const usePlatformStore = defineStore('platform', () => {
  const ports = ref<PortModel[]>([]);

  async function fetch() {
    ports.value = await PortService.getPorts();
  }

  onBeforeMount(async () => {
    await fetch();
  });

  onLoRaEvent({
    async onConnect() {
      await fetch();
    },
    async onDisconnect() {
      await fetch();
    },
  });

  return { ports, fetch };
});
