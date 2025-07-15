import { defineStore } from 'pinia';
import { onBeforeUnmount, onMounted, ref, watch } from 'vue';
import { Client } from '@stomp/stompjs';
import SockJS from 'sockjs-client';
import { api } from 'boot/axios';

export enum LoRaEventType {
  PORT_CONNECT = 'PORT_CONNECT',
  PORT_DISCONNECT = 'PORT_DISCONNECT',
  PORT_RECOGNIZED = 'PORT_RECOGNIZED',
  SERIAL_TX = 'SERIAL_TX',
  SERIAL_RX = 'SERIAL_RX',
  RX_START = 'RX_START',
  RX_DONE = 'RX_DONE',
  RX_TIMEOUT = 'RX_TIMEOUT',
  RX_ERROR = 'RX_ERROR',
  TX_START = 'TX_START',
  TX_DONE = 'TX_DONE',
  TX_TIMEOUT = 'TX_TIMEOUT',
}

export interface LoRaEvent {
  id: number;
  date: Date;
  portName: string;
  name: string;
}

export interface LoRaNodeEvent extends LoRaEvent {
  nodeId: string;
}

export interface LoRaDataEvent extends LoRaNodeEvent {
  data: string;
}

export interface LoRaPortReceivedMessage extends LoRaDataEvent {
  rssi: number;
  snr: number;
}

export type LoRaPortListenerCallbackContextless = () => void;
export type LoRaPortListenerCallback<Event> = (evt: Event) => void;

export interface LoRaPortListener {
  onWebsocketConnect?: LoRaPortListenerCallbackContextless;
  onWebsocketDisconnect?: LoRaPortListenerCallbackContextless;
  onEvent?: LoRaPortListenerCallback<LoRaEvent>;
  onConnect?: LoRaPortListenerCallback<LoRaEvent>;
  onDisconnect?: LoRaPortListenerCallback<LoRaEvent>;
  onSerialTx?: LoRaPortListenerCallback<LoRaDataEvent>;
  onSerialRx?: LoRaPortListenerCallback<LoRaDataEvent>;
  onTxDone?: LoRaPortListenerCallback<LoRaEvent>;
  onTxTimeout?: LoRaPortListenerCallback<LoRaEvent>;
  onRxDone?: LoRaPortListenerCallback<LoRaPortReceivedMessage>;
  onRxTimeout?: LoRaPortListenerCallback<LoRaEvent>;
  onRxError?: LoRaPortListenerCallback<LoRaEvent>;
  onRxStart?: LoRaPortListenerCallback<LoRaEvent>;
  onTxStart?: LoRaPortListenerCallback<LoRaDataEvent>;
}

export const useWebsocketStore = defineStore('websocket', () => {
  const isConnected = ref<boolean>(false);
  const stompClient = ref<Client | null>(null);
  const listeners = ref<LoRaPortListener[]>([]);
  const websocketUrl = api.getUri({ url: '/ws' });

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

      client.subscribe('/topic/event', (message) => {
        const evt = JSON.parse(message.body);
        evt.date = new Date(evt.date);
        listeners.value.map((l) => l.onEvent?.(evt));

        switch (evt.name) {
          case LoRaEventType.PORT_CONNECT:
          case LoRaEventType.PORT_RECOGNIZED:
            listeners.value.map((l) => l.onConnect?.(evt));
            break;
          case LoRaEventType.PORT_DISCONNECT:
            listeners.value.map((l) => l.onDisconnect?.(evt));
            break;
          case LoRaEventType.SERIAL_TX:
            listeners.value.map((l) => l.onSerialTx?.(evt));
            break;
          case LoRaEventType.SERIAL_RX:
            listeners.value.map((l) => l.onSerialRx?.(evt));
            break;
          case LoRaEventType.RX_START:
            listeners.value.map((l) => l.onRxStart?.(evt));
            break;
          case LoRaEventType.RX_DONE:
            listeners.value.map((l) => l.onRxDone?.(evt));
            break;
          case LoRaEventType.RX_TIMEOUT:
            listeners.value.map((l) => l.onRxTimeout?.(evt));
            break;
          case LoRaEventType.RX_ERROR:
            listeners.value.map((l) => l.onRxError?.(evt));
            break;
          case LoRaEventType.TX_START:
            listeners.value.map((l) => l.onTxStart?.(evt));
            break;
          case LoRaEventType.TX_DONE:
            listeners.value.map((l) => l.onTxDone?.(evt));
            break;
          case LoRaEventType.TX_TIMEOUT:
            listeners.value.map((l) => l.onTxTimeout?.(evt));
            break;
        }
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

  console.log('reload');
  onMounted(() => {
    console.log('mount');
    initWebSocket();
  });

  onBeforeUnmount(async () => {
    console.log('unmount');
    if (stompClient.value) {
      await stompClient.value.deactivate();
    }
  });

  watch(isConnected, (val) => {
    if (val) {
      listeners.value.map((l) => l.onWebsocketConnect?.());
    } else {
      listeners.value.map((l) => l.onWebsocketDisconnect?.());
    }
  });

  return { isConnected, stompClient, addListener, removeListener };
});
