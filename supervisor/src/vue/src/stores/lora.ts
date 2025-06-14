import { defineStore } from 'pinia';
import { onBeforeUnmount, onMounted, ref } from 'vue';
import { Client } from '@stomp/stompjs';
import SockJS from 'sockjs-client';

const websocketUrl = 'http://localhost:8080/ws';

export const useWebsocketStore = defineStore('websocket', () => {
  const messages = ref<string[]>([]);
  const isConnected = ref<boolean>(false);
  const stompClient = ref<Client | null>(null);

  const addMessage = (msg: string) => {
    messages.value.push(msg);
  };

  const initWebSocket = () => {
    const client = new Client({
      webSocketFactory: () => new SockJS(websocketUrl),
      reconnectDelay: 5000,
      heartbeatIncoming: 4000,
      heartbeatOutgoing: 4000,
    });

    if (typeof WebSocket === 'function') {
      client.webSocketFactory = undefined;
      client.brokerURL = websocketUrl;
    }

    client.onConnect = () => {
      isConnected.value = true;

      client.subscribe('/topic/serial/rx', (message) => {
        const payload = JSON.parse(message.body);
        const msg = payload.message?.replaceAll('\r', '\u240d').replaceAll('\n', '\u2424');
        addMessage(`${payload.port} < ${msg}`);
      });

      client.subscribe('/topic/serial/tx', (message) => {
        const payload = JSON.parse(message.body);
        const msg = payload.message?.replaceAll('\r', '\u240d').replaceAll('\n', '\u2424');
        addMessage(`${payload.port} > ${msg}`);
      });

      client.subscribe('/topic/lora/tx/done', (message) => {
        const payload = JSON.parse(message.body);
        const msg = payload.message?.replaceAll('\r', '\u240d').replaceAll('\n', '\u2424');
        addMessage(`${payload.port} >> ${msg}`);
      });

      client.subscribe('/topic/lora/tx/done', (message) => {
        const payload = JSON.parse(message.body);
        const msg = payload.message?.replaceAll('\r', '\u240d').replaceAll('\n', '\u2424');
        addMessage(`${payload.port} << ${msg}`);
      });
    };

    client.onStompError = (frame) => {
      console.error('STOMP error', frame);
      isConnected.value = false;
    };

    client.onWebSocketClose = () => {
      isConnected.value = false;
    };

    client.activate();
    stompClient.value = client;
  };

  onMounted(() => {
    initWebSocket();
  });

  onBeforeUnmount(async () => {
    if (stompClient.value) {
      await stompClient.value.deactivate();
    }
  });

  return { isConnected, stompClient, messages };
});
