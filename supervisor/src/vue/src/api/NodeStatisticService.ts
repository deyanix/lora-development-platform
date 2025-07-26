import { api } from 'boot/axios';
import { AxiosResponse } from 'axios';

export interface NodeMessage {
  eventId: number;
  senderId: string;
  startDate: Date;
  endDate: Date | null;
  data: string;
  successful: boolean;
  duration: number;
  finished: boolean;
  receptions: NodeMessageReception[];
}

export interface NodeMessageReception {
  eventId: number;
  receiverId: number;
  date: Date;
  successful: boolean;
  snr: number | null;
  rssi: number | null;
}

export const NodeStatisticService = {
  deserializeMessage(row: NodeMessage): NodeMessage {
    return {
      ...row,
      startDate: new Date(row.startDate),
      endDate: row.endDate ? new Date(row.endDate) : null,
      receptions: row.receptions.map((r) => ({
        ...r,
        date: new Date(r.date),
      })),
    };
  },
  async getMessages(): Promise<NodeMessage[]> {
    const response: AxiosResponse<NodeMessage[]> = await api.get(`/nodes/any/messages`);
    return response.data.map((row) => this.deserializeMessage(row));
  },
  async getMessagesBySenderId(id: string): Promise<NodeMessage[]> {
    const response: AxiosResponse<NodeMessage[]> = await api.get(`/nodes/${id}/messages`);
    return response.data.map((row) => this.deserializeMessage(row));
  },
};
