import { LoRaPortListener, useWebsocketStore } from 'stores/websocket';
import { onBeforeUnmount } from 'vue';

export function onLoRaEvent(listener: LoRaPortListener): void {
  const websocket = useWebsocketStore();

  websocket.addListener(listener);

  onBeforeUnmount(() => {
    websocket.removeListener(listener);
  });
}
