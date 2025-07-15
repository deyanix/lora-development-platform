import { api } from 'boot/axios';
import { LoRaEvent } from 'stores/websocket';

export interface NodeModel {
  id: string;
  portName: string;
  radioConfiguration: NodeRadioConfiguration;
  configuration: NodeConfiguration;
  flashing: boolean;
  connected: boolean;
}

export interface NodeConfiguration {
  mode: string;
  auto: string;
  initialBackoffMax: number;
  interval: number;
  ackRequired: boolean;
  ackLifetime: number;
  backoffIncrease: boolean;
}

export interface NodeRadioConfiguration {
  frequency: number;
  power: number;
  bandwidth: string;
  codingRate: string;
  spreadingFactor: number;
  payloadLength: number;
  preambleLength: number;
  enableCrc: boolean;
  iqInverted: boolean;
  txTimeout: number;
  rxSymbolTimeout: number;
}

export type NodeOption<T> = { value: T; name: string };
export type NodeOptionRange = { min: number; max: number };

export interface NodeOptions {
  frequency: NodeOptionRange;
  power: NodeOptionRange;
  preambleLength: NodeOptionRange;
  payloadLength: NodeOptionRange;
  txTimeout: NodeOptionRange;
  rxSymbolTimeout: NodeOptionRange;
  spreadingFactor: NodeOption<number>[];
  bandwidth: NodeOption<string>[];
  codingRate: NodeOption<string>[];
  mode: string[];
  auto: string[];
}

export const NodeService = {
  async getOptions(): Promise<NodeOptions> {
    const response = await api.get('/nodes/any/options');
    return response.data;
  },
  async getNodes(): Promise<NodeModel[]> {
    const response = await api.get('/nodes');
    return response.data;
  },
  async getEvents(id: string): Promise<LoRaEvent[]> {
    const response = await api.get(`/nodes/${id}/events`);
    return response.data;
  },
  async transmit(id: string, text: string): Promise<void> {
    await api.post(`/nodes/${id}/transmit`, text, { headers: { 'Content-Type': 'text/plain' } });
  },
  async updateFlashing(id: string, flashing: boolean): Promise<void> {
    await api.post(`/nodes/${id}/flashing`, { flashing });
  },
  async updateConfiguration(id: string, configuration: Partial<NodeConfiguration>) {
    await api.patch(`/nodes/${id}/configuration`, configuration);
  },
  async updateRadioConfiguration(id: string, configuration: Partial<NodeRadioConfiguration>) {
    await api.patch(`/nodes/${id}/radio`, configuration);
  },
  async resetAuto(id: string) {
    await api.post(`/nodes/${id}/reset-auto`);
  },
  async getTimeOnAir(id: string, length: number): Promise<number> {
    const response = await api.get(`/nodes/${id}/time-on-air`, { params: { length } });
    return response.data;
  },
};
