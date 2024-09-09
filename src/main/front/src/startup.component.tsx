import { tartine } from './shared/util/tartine';
import { useItemStore } from './shared/store/item-store.store';
import { ReactElement, useEffect } from 'react';

export const Startup = (): ReactElement => {
    const { fetchUploadAccept } = useItemStore();
    useEffect(() => {
        fetchUploadAccept()
            .catch(() => {
                tartine.error("Failed to fetch accepted file list, upload will be unavailable")
            });
    }, []);
    return (<></>);
};
