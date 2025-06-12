<template>
  <q-page padding>
    <div>
      Status połączenia:
      <b :style="`color: ${isConnected ? 'green' : 'red'}`">
        {{ isConnected ? 'Połączono' : 'Rozłączono' }}
      </b>
    </div>

    <div class="console q-mt-md">
      <q-scroll-area style="height: 200px; max-width: 100%">
        <div v-for="(msg, index) in messages" :key="index">
          {{ msg }}
        </div>
      </q-scroll-area>
    </div>
  </q-page>
</template>

<script setup lang="ts">
import { ref, onMounted, onBeforeUnmount } from 'vue';
import { Client } from '@stomp/stompjs';
import SockJS from 'sockjs-client';

const messages = ref<string[]>([]);
const isConnected = ref<boolean>(false);
const stompClient = ref<Client | null>(null);

const addMessage = (msg: string) => {
  messages.value.push(msg);
};

const initWebSocket = () => {
  const brokerUrl = 'http://localhost:8080/ws';

  const client = new Client({
    webSocketFactory: () => new SockJS(brokerUrl),
    debug: (str: string) => {
      console.log(new Date(), str);
    },
    reconnectDelay: 5000,
    heartbeatIncoming: 4000,
    heartbeatOutgoing: 4000,
  });

  if (typeof WebSocket === 'function') {
    client.webSocketFactory = undefined;
    client.brokerURL = brokerUrl;
  }

  client.onConnect = (frame) => {
    console.log('Połączono z WebSocket!', frame);
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
  };

  client.onStompError = (frame) => {
    console.error('Błąd STOMP:', frame);
    isConnected.value = false;
  };

  client.onWebSocketClose = () => {
    console.log('Połączenie WebSocket zamknięte.');
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
    console.log('Klient STOMP zdezaktywowany.');
  }
});
</script>
<style lang="scss">
.console {
  background: black;
  color: white;
  font-family: Consolas, serif;
  padding: 12px;
  border-radius: 4px;
}
</style>
