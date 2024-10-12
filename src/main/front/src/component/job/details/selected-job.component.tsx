import React, { useEffect } from 'react';
import { useJobStore } from '../../../shared/store/jobs.store';
import { JobDetails } from './job-details.component';
import { useItemStore } from '../../../shared/store/item-store.store';
import { SpinyThingy } from '../../../shared/component/spiny-thingy.component';

export const SelectedJob: React.FC = () => {
    const jobStore = useJobStore();
    const itemStore = useItemStore();
    const selectedJob = jobStore.selectedJob;

    useEffect(() => {
        if (selectedJob) {
            itemStore.fetchJobInventory(selectedJob.id);
        }
    }, [selectedJob]);

    if (!selectedJob) {
        return <p>Select a job!</p>;
    }
    if (jobStore.loading) {
        return <p><SpinyThingy /> Loading job...</p>
    }

    const sourceItem = itemStore.items.find(i => i.name === selectedJob.description.sourceItemName);
    const jobOutput = itemStore.jobItems[selectedJob.id];

    return <JobDetails job={selectedJob} sourceItem={sourceItem} jobOutput={jobOutput} />;
}
