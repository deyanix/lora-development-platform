<template>
  <q-list separator class="full-height">
    <q-expansion-item group="node-panel" label="Konfiguracje radiowe" default-opened>
      <q-item>
        <q-item-section>
          <q-item-label caption> Częstotliwość </q-item-label>
          <q-item-label> {{ frequency }} </q-item-label>
        </q-item-section>
      </q-item>
      <q-item>
        <q-item-section>
          <q-item-label caption> Pasmo </q-item-label>
          <q-item-label> {{ bandwidth?.name }} </q-item-label>
        </q-item-section>
      </q-item>
      <q-item>
        <q-item-section>
          <q-item-label caption> Moc </q-item-label>
          <q-item-label> {{ node.radioConfiguration.power }} dBm </q-item-label>
        </q-item-section>
      </q-item>
      <q-item>
        <q-item-section>
          <q-item-label caption> Korekcja błędów </q-item-label>
          <q-item-label> {{ codingRate?.name }} </q-item-label>
        </q-item-section>
      </q-item>
      <q-item>
        <q-item-section>
          <q-item-label caption> CRC </q-item-label>
          <q-item-label>
            {{ node.radioConfiguration.enableCrc ? 'Włączone' : 'Wyłączone' }}
          </q-item-label>
        </q-item-section>
      </q-item>
      <q-item>
        <q-item-section>
          <q-item-label caption> Odwrócone IQ </q-item-label>
          <q-item-label>
            {{ node.radioConfiguration.iqInverted ? 'Włączone' : 'Wyłączone' }}
          </q-item-label>
        </q-item-section>
      </q-item>
      <q-item>
        <q-item-section>
          <q-item-label caption> Współczynnik rozpraszania </q-item-label>
          <q-item-label> {{ spreadingFactor?.name }} </q-item-label>
        </q-item-section>
      </q-item>
      <q-item>
        <q-item-section>
          <q-item-label caption> Rozmiar ładunku </q-item-label>
          <q-item-label> {{ node.radioConfiguration.payloadLength }} bit </q-item-label>
        </q-item-section>
      </q-item>
      <q-item>
        <q-item-section>
          <q-item-label caption> Rozmiar preambuły </q-item-label>
          <q-item-label> {{ node.radioConfiguration.preambleLength }} symb. </q-item-label>
        </q-item-section>
      </q-item>
      <q-item>
        <q-item-section>
          <q-item-label caption> Limit czasu RX </q-item-label>
          <q-item-label> {{ node.radioConfiguration.rxSymbolTimeout }} symb. </q-item-label>
        </q-item-section>
      </q-item>
      <q-item>
        <q-item-section>
          <q-item-label caption> Limit czasu TX </q-item-label>
          <q-item-label> {{ node.radioConfiguration.txTimeout }} ms </q-item-label>
        </q-item-section>
      </q-item>
    </q-expansion-item>
    <q-expansion-item group="node-panel" label="Konfiguracje węzła">
      <q-item>
        <q-item-section>
          <q-item-label caption> Tryb </q-item-label>
          <q-item-label> {{ node.configuration.mode }} </q-item-label>
        </q-item-section>
      </q-item>
      <q-item>
        <q-item-section>
          <q-item-label caption> Algorytm </q-item-label>
          <q-item-label> {{ node.configuration.auto }} </q-item-label>
        </q-item-section>
      </q-item>
      <q-item>
        <q-item-section>
          <q-item-label caption> Wymagane ACK </q-item-label>
          <q-item-label>
            {{ node.configuration.ackRequired ? 'Włączone' : 'Wyłączone' }}
          </q-item-label>
        </q-item-section>
      </q-item>
      <q-item>
        <q-item-section>
          <q-item-label caption> Długość życia ACK </q-item-label>
          <q-item-label> {{ node.configuration.ackLifetime }} </q-item-label>
        </q-item-section>
      </q-item>
      <q-item>
        <q-item-section>
          <q-item-label caption> Interwał </q-item-label>
          <q-item-label> {{ node.configuration.interval }} ms </q-item-label>
        </q-item-section>
      </q-item>
      <q-item>
        <q-item-section>
          <q-item-label caption> Maksymalne początkowe opóźnienie ponowienia </q-item-label>
          <q-item-label> {{ node.configuration.initialBackoffMax }} ms </q-item-label>
        </q-item-section>
      </q-item>
      <q-item>
        <q-item-section>
          <q-item-label caption> Wydłużanie opóźnienia ponowienia </q-item-label>
          <q-item-label>
            {{ node.configuration.backoffIncrease ? 'Włączone' : 'Wyłączone' }}
          </q-item-label>
        </q-item-section>
      </q-item>
    </q-expansion-item>
  </q-list>
</template>
<script setup lang="ts">
import { NodeModel } from 'src/api/NodeService';
import { computed } from 'vue';
import { usePlatformStore } from 'stores/platform';

const platform = usePlatformStore();
const props = defineProps<{ node: NodeModel }>();

const frequency = computed(
  () => (props.node.radioConfiguration.frequency / 1_000_000).toFixed(2) + ' MHz',
);

const spreadingFactor = computed(() =>
  platform.options?.spreadingFactor.find(
    (opt) => opt.value === props.node.radioConfiguration.spreadingFactor,
  ),
);
const bandwidth = computed(() =>
  platform.options?.bandwidth.find((opt) => opt.value === props.node.radioConfiguration.bandwidth),
);
const codingRate = computed(() =>
  platform.options?.codingRate.find(
    (opt) => opt.value === props.node.radioConfiguration.codingRate,
  ),
);
</script>
