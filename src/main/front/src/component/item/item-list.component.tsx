import React, { useEffect } from 'react';
import { useItemStore } from '../../shared/store/item-store.store';
import { Item } from '../../shared/model/item.model';
import { ItemComponent } from './item.component';
import { ItemUploadComponent } from './item-upload.component';

interface ItemListProps {
    jobId?: string;
    showPreview?: boolean;
    playable?: boolean;
    showUpload?: boolean;
}

export const ItemListComponent: React.FC<ItemListProps> = ({ jobId, playable, showPreview, showUpload }) => {
    const itemStore = useItemStore();

    useEffect(() => {
        if (jobId) {
            itemStore.fetchJobInventory(jobId);
        } else {
            itemStore.fetchInventory();
        }
    }, []);

    const getItems = (): Item[] => {
        if (jobId) {
            return itemStore.jobItems[jobId];
        } else {
            return itemStore.items;
        }
    }

    return (
        <div>
            {showUpload ? (
                <div>
                    <ItemUploadComponent />
                </div>
            ) : undefined}
            <div>
                {itemStore.itemsLoading
                    ? 'Loading'
                    : (getItems() || [])
                        .map(i => <ItemComponent item={i} key={i.name} playable={playable} showPreview={showPreview}/>)
                }
            </div>
        </div>
    );
};
