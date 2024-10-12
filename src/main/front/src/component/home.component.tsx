import React, { useEffect } from 'react';
import { JobListComponent } from './job/list/job-list.component';
import './home.component.css';
import { SelectedJob } from './job/details/selected-job.component';
import { useJobStore } from '../shared/store/jobs.store';
import { useItemStore } from '../shared/store/item-store.store';

export const Home: React.FC = () => {
    const jobStore = useJobStore();
    const itemStore = useItemStore();

    useEffect(() => {
        itemStore.fetchInventory(false);
        jobStore.fetch(false);
        jobStore.subscribeToUpdates();
    }, []);

    return (
        <div className="home-container">
            <div className="left-pane">
                <JobListComponent/>
            </div>
            <div className="right-pane">
                <SelectedJob />
            </div>
        </div>
    );
};
