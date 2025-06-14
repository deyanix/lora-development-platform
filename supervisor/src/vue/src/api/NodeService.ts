import { api } from 'boot/axios';

export const NodeService = {
  async toggleFlashing(id: string): Promise<void> {
    await api.post(`/nodes/${id}/flashing`);
  },
  async transmit(id: string, text: string): Promise<void> {
    await api.post(`/nodes/${id}/transmit`, text);
  },
};
