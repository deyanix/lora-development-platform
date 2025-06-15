import { defineStore } from 'pinia';
import { onBeforeUnmount, onMounted, ref } from 'vue';
import { Client } from '@stomp/stompjs';
import SockJS from 'sockjs-client';

const websocketUrl = 'http://localhost:8080/ws';

export interface LoRaPortEvent {
  portName: string;
}

export interface LoRaPortMessage extends LoRaPortEvent {
  message: string;
}

export interface LoRaPortReceivedMessage extends LoRaPortMessage {
  rssi: number;
  snr: number;
}

export type LoRaPortListenerCallback<Event> = (evt: Event) => void;

export interface LoRaPortListener {
  onConnect?: LoRaPortListenerCallback<LoRaPortEvent>;
  onDisconnect?: LoRaPortListenerCallback<LoRaPortEvent>;
  onSerialTx?: LoRaPortListenerCallback<LoRaPortMessage>;
  onSerialRx?: LoRaPortListenerCallback<LoRaPortMessage>;
  onTxDone?: LoRaPortListenerCallback<LoRaPortMessage>;
  onTxTimeout?: LoRaPortListenerCallback<LoRaPortEvent>;
  onRxDone?: LoRaPortListenerCallback<LoRaPortReceivedMessage>;
  onRxTimeout?: LoRaPortListenerCallback<LoRaPortEvent>;
  onRxError?: LoRaPortListenerCallback<LoRaPortEvent>;
}

export const useWebsocketStore = defineStore('websocket', () => {
  const isConnected = ref<boolean>(false);
  const stompClient = ref<Client | null>(null);
  const listeners = ref<LoRaPortListener[]>([]);

  function addListener(listener: LoRaPortListener): void {
    listeners.value.push(listener);
  }

  function removeListener(listener: LoRaPortListener): void {
    const index = listeners.value.indexOf(listener);
    if (index >= 0) {
      listeners.value.splice(index, 1);
    }
  }

  function initWebSocket() {
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

      client.subscribe('/topic/serial/tx', (message) => {
        const evt = JSON.parse(message.body);
        listeners.value.map((l) => l.onSerialTx?.(evt));
      });

      client.subscribe('/topic/serial/rx', (message) => {
        const evt = JSON.parse(message.body);
        listeners.value.map((l) => l.onSerialRx?.(evt));
      });

      client.subscribe('/topic/port/connect', (message) => {
        const evt = JSON.parse(message.body);
        listeners.value.map((l) => l.onConnect?.(evt));
      });

      client.subscribe('/topic/port/disconnect', (message) => {
        const evt = JSON.parse(message.body);
        listeners.value.map((l) => l.onDisconnect?.(evt));
      });

      client.subscribe('/topic/port/tx/done', (message) => {
        const evt = JSON.parse(message.body);
        listeners.value.map((l) => l.onTxDone?.(evt));
      });

      client.subscribe('/topic/port/tx/timeout', (message) => {
        const evt = JSON.parse(message.body);
        listeners.value.map((l) => l.onTxTimeout?.(evt));
      });

      client.subscribe('/topic/port/rx/done', (message) => {
        const evt = JSON.parse(message.body);
        listeners.value.map((l) => l.onRxDone?.(evt));
      });

      client.subscribe('/topic/port/rx/timeout', (message) => {
        const evt = JSON.parse(message.body);
        listeners.value.map((l) => l.onRxTimeout?.(evt));
      });

      client.subscribe('/topic/port/rx/error', (message) => {
        const evt = JSON.parse(message.body);
        listeners.value.map((l) => l.onRxError?.(evt));
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
  }

  onMounted(() => {
    initWebSocket();
  });

  onBeforeUnmount(async () => {
    if (stompClient.value) {
      await stompClient.value.deactivate();
    }
  });

  return { isConnected, stompClient, addListener, removeListener };
});
