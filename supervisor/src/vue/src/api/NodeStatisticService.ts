import { api } from 'boot/axios';
import { AxiosResponse } from 'axios';

export interface NodeMessage {
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
  receiverId: number;
  date: Date;
}

export const NodeStatisticService = {
  async getMessages(id: string): Promise<NodeMessage[]> {
    const response: AxiosResponse<NodeMessage[]> = await api.get(`/nodes/${id}/messages`);
    return response.data.map((row) => ({
      ...row,
      startDate: new Date(row.startDate),
      endDate: row.endDate ? new Date(row.endDate) : null,
      receptions: row.receptions.map((r) => ({
        ...r,
        date: new Date(r.date),
      })),
    }));
  },
};
