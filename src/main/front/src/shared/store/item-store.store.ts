import { create } from 'zustand';
import { Item } from '../model/item.model';
import { itemStoreStoreApi } from '../api/item-store.store.api';

interface ItemStore {
    items: Item[];
    itemsLoading: boolean;
    fetchInventory: (silent?: boolean) => Promise<void>;

    jobItems: {[k in string]: Item[]};
    jobItemLoading: string[];
    fetchJobInventory: (jobId: string, silent?: boolean) => Promise<void>;
}

export const useItemStore = create<ItemStore>(
    (set, get) => ({
        items: [],
        itemsLoading: false,
        fetchInventory: async (silent: boolean = false) => {
            !silent && set({ itemsLoading: true });
            const response = await itemStoreStoreApi.inventory();
            if (response.status === 'success') {
                set({ items: response.result, itemsLoading: false });
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
        }
    })
);
