import { api } from 'boot/axios';

export interface PortModel {
  port: string;
  nodeId: string | null;
}

export const PortService = {
  async getPorts(): Promise<PortModel[]> {
    const response = await api.get('/ports');
    return response.data;
  },
  async connect(port: string): Promise<void> {
    await api.post(`/ports/${port}/connect`);
  },
  async disconnect(port: string): Promise<void> {
    await api.post(`/ports/${port}/disconnect`);
  },
};
