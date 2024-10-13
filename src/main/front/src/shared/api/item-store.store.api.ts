import { Result } from '../model/result.model';
import { Item } from '../model/item.model';
import { Http } from '../util/http.util';

const basePath = '/store';

export const itemStoreStoreApi = {
    fetchAccept: (): Promise<Result<{accept: string}>> => Http.get(`${basePath}/accept`),
    inventory: (): Promise<Result<Item[]>> => Http.get<Item[]>(`${basePath}/inventory`),
    jobInventory: (jobId: string): Promise<Result<Item[]>> => Http.get<Item[]>(`${basePath}/inventory/${jobId}`),
    uploadItem: (file: File): Promise<Result<Item>> => {
        const formData = new FormData();
        formData.append('file', file);
        return Http.post(`${basePath}/upload`, formData, 'form-data');
    },
    downloadItem: (item: Item) => Http.get<string>(item.downloadLink, 'raw-text', false),
    subscribeToUpdates: (onMessage: (ev: MessageEvent) => any): EventSource => {
        const eventSource = Http.sse(`${basePath}/sse/item-update`);
        eventSource.onmessage = onMessage;
        return eventSource;
    },
};
