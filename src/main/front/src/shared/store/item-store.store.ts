import { create } from 'zustand';
import { Item } from '../model/item.model';
import { itemStoreStoreApi } from '../api/item-store.store.api';
import { jobApi } from '../api/jobs.api';

interface ItemStore {
    uploadAccept?: string;
    fetchUploadAccept: () => Promise<void>;
    uploadItem: (item: File) => Promise<Item>;

    items: Item[];
    itemsLoading: boolean;
    fetchInventory: (silent?: boolean) => Promise<void>;

    jobItems: {[k in string]: Item[]};
    jobItemLoading: string[];
    fetchJobInventory: (jobId: string, silent?: boolean) => Promise<void>;
    downloadItem: (item: Item) => Promise<string>;

    subscribeToUpdates: () => void;
}

export const useItemStore = create<ItemStore>(
    (set, get) => ({
        uploadAccept: undefined,
        fetchUploadAccept: async () => {
            const response = await itemStoreStoreApi.fetchAccept();
            if (response.status === 'success') {
                set({ uploadAccept: response.result.accept });
            } else {
                throw new Error('Failed to fetch inventory');
            }
        },
        uploadItem: async (item: File) => {
            const response = await itemStoreStoreApi.uploadItem(item);
            if (response.status === 'success') {
                return response.result;
            } else {
                throw new Error('Failed to fetch inventory');
            }
        },

        items: [],
        itemsLoading: false,
        fetchInventory: async (silent: boolean = false) => {
            !silent && set({ itemsLoading: true });
            const response = await itemStoreStoreApi.inventory();
            if (response.status === 'success') {
                set({ items: response.result, itemsLoading: false });
            } else {
                throw new Error('Failed to fetch inventory');
            }
        },

        jobItems: {},
        jobItemLoading: [],
        fetchJobInventory: async (jobId: string) => {
            const response = await itemStoreStoreApi.jobInventory(jobId);
            set({ jobItemLoading: [...get().jobItemLoading, jobId] });

            if (response.status === 'success') {
                const originalJobs = get().jobItems;
                const updatedLoading = get().jobItemLoading.filter(e => e !== jobId);

                set({ jobItems: { ...originalJobs, [jobId]: response.result }, jobItemLoading: updatedLoading });
            }
        },
        downloadItem: async (item: Item) => {
            const result = await itemStoreStoreApi.downloadItem(item);
            if (result.status === 'success') {
                return result.result;
            } else {
                throw new Error(`Failed to retrieve ${item.name}`);
            }
        },

        subscribeToUpdates: () => {
            jobApi.subscribeToUpdates((event: MessageEvent<string>) => {
                get().fetchJobInventory(event.data);
            })
        }
    })
);
