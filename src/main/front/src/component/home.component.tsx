import React from 'react';
import { JobComponent } from './job/job.component';
import { ItemListComponent } from './item/item-list.component';

export const Home: React.FC = () => {
    return (
        <div>
            <JobComponent />
            <ItemListComponent showPreview={true} playable={true} />
        </div>
    );
};
