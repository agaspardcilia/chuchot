import React, { useEffect } from 'react';
import { UpdateJobComponent } from './update-job.component';
import { JobDescription } from '../../shared/model/job.model';
import { useJobStore } from '../../shared/store/jobs.store';
import { tartine } from '../../shared/util/tartine';
import { JobDetails } from './job-details.component';
import { useItemStore } from '../../shared/store/item-store.store';

export const JobComponent: React.FC = () => {
    const jobStore = useJobStore();
    const itemStore = useItemStore();

    useEffect(() => {
        const updateStores = (silent: boolean): void => {
            itemStore.fetchInventory(silent);
            jobStore.fetch(silent);
        }
        updateStores(false);
        // TODO: not a fan of the interval, should find an alternative to busy pulling.
        const interval = setInterval(() => {
            updateStores(true);
        }, 2000);
        return () => {
            clearInterval(interval);
        };
    }, []);

    const onJobSave = (description: JobDescription, id?: string) => {
        jobStore.update(description, id)
            .then(result => {
                tartine.success(`Job created with success for ${result?.itemName}`);
                jobStore.fetch();
            })
            .catch(e => {
                tartine.error('Failed to create job');
                jobStore.fetch();
            });
    };

    const onJobDelete = (id: string): void => {
        jobStore.delete(id)
            .then(result => {
                tartine.success(`Job deleted`);
                jobStore.fetch();
            })
            .catch(e => {
                tartine.error('Failed to delete job');
                jobStore.fetch();
            });
    };

    const onJobStart = (id: string): void => {
        jobStore.start(id)
            .then(result => {
                tartine.success(`Job started`);
                jobStore.fetch();
            })
            .catch(e => {
                tartine.error('Failed to start job');
                jobStore.fetch();
            });
    };

    const onJobCancel = (id: string): void => {
        console.log('cancel requested', id);
    };

    return (
        <div>
            {itemStore.itemsLoading || jobStore.loading
                ? <h2>Loading, please wait...</h2>
                : <div>
                    <h2>Jobs</h2>
                    {jobStore.jobs
                        .map(j => (
                            <JobDetails
                                key={j.id}
                                job={j}
                                onCancel={onJobCancel}
                                onDelete={onJobDelete}
                                onStart={onJobStart}
                            />
                        ))
                    }
                    <hr />
                    <h3>Create job</h3>
                    <UpdateJobComponent onSave={onJobSave} items={itemStore.items}/>
                </div>
            }
        </div>
    );
};
