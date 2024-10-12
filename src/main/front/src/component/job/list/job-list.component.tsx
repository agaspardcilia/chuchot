import React, { useState } from 'react';
import { UpdateJobComponent } from './update-job.component';
import { JobDescription } from '../../../shared/model/job.model';
import { useJobStore } from '../../../shared/store/jobs.store';
import { tartine } from '../../../shared/util/tartine';
import { useItemStore } from '../../../shared/store/item-store.store';
import { JobControls } from './job-controls.component';
import { SpinyThingy } from '../../../shared/component/spiny-thingy.component';
import { JobLine } from '../job-line.component';

type Mode = 'list' | 'add';

export const JobListComponent: React.FC = () => {
    const jobStore = useJobStore();
    const itemStore = useItemStore();
    const [mode, setMode] = useState<Mode>('list');

    const onSave = (description: JobDescription, id?: string) => {
        jobStore.update(description, id)
            .then(result => {
                tartine.success(`Job created with success for ${result?.description.sourceItemName}`);
            })
            .catch(e => {
                tartine.error('Failed to create job');
            });
    };

    const renderList = () => {
        const {loading, jobs} = jobStore;

        if (itemStore.itemsLoading || loading) {
            return <h2> <SpinyThingy/> Loading, please wait...</h2>;
        }

        if (!jobs.length) {
            return (
                <div>
                    No job found, add one!
                </div>
            )
        }

        return (
            <div>
                {jobs.map(j => (
                    <div key={j.id}>
                        <a className="pointer" onClick={() => jobStore.setSelectedJob(j.id)}>
                            <JobLine job={j}/>
                        </a>
                    </div>
                ))}
            </div>
        );
    };

    const renderJobUpdate = () => {
        return (
            <>
                <h3>Create job</h3>
                <UpdateJobComponent onSave={onSave}
                                    items={itemStore.items}
                                    onCancel={() => setMode('list')}/>
            </>
        );
    };

    const renderContent = () => {
        switch (mode) {
            case 'list':
                return renderList();
            case 'add':
                return renderJobUpdate();
        }
    };

    return (
        <div>
            <h2>Jobs</h2>
            <JobControls onFilterChange={(f, d) => console.log('Filter change', f, d)}
                         selectedFilter={undefined}
                         onAdd={() => setMode('add')}/>
            <hr/>
            { renderContent() }
            <hr/>


        </div>
    );
};
