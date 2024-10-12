import React, { useEffect, useState } from 'react';
import { JobReport } from '../../../shared/model/job.model';
import './job-details.component.css';
import { ItemListComponent } from '../../item/item-list.component';
import { JobLine } from '../job-line.component';
import { tartine } from '../../../shared/util/tartine';
import { useJobStore } from '../../../shared/store/jobs.store';
import { Collapsable } from '../../../layout/collapsable.component';
import { JobParameters } from './element/job-parameters.component';
import { JobPreview } from './element/job-preview.component';
import { Item } from '../../../shared/model/item.model';

interface JobDetailsProps {
    job: JobReport;
    sourceItem?: Item;
    jobOutput: Item[];
}

export const JobDetails: React.FC<JobDetailsProps> = ({ job, sourceItem, jobOutput }) => {
    const jobStore = useJobStore();
    const [opened, setOpened] = useState<boolean>(false);
    const { id, status, description } = job;
    const isInProgress = ['PENDING', 'IN_PROGRESS'].includes(status);
    const startedOnce = status !== 'READY';

    const onDelete = (id: string): void => {
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

    const onStart = (id: string): void => {
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

    const onCancel = (id: string): void => {
        console.log('cancel requested', id);
    };

    const renderPreview = () => {
        if (!sourceItem) {
            return <p>No source file to be found.</p>
        }

        const subItem = (jobOutput || []).find(i => i.name.endsWith('.srt'));
        return (
            <JobPreview job={job.description} sourceItem={sourceItem} subtitles={subItem} />
        );
    };

    return (
        <div className="job-container">
            <div className="job-header">
                <span className="expend pointer" onClick={() => setOpened(e => !e)}>[{opened ? '-' : '+'}]</span>
                {' '}
                <JobLine job={job} />
                <span className="details">
                    {isInProgress
                        ? (<button onClick={() => onCancel(id)}>Cancel</button>)
                        : (
                            <>
                                <button onClick={() => onStart(id)}>Start</button>
                                <button onClick={() => onDelete(id)}>Delete</button>
                            </>
                        )
                    }
                </span>
            </div>
            <div className="job-body">
                <Collapsable title="Parameters">
                    <JobParameters parameters={description.parameters} />
                </Collapsable>
                <Collapsable title="Preview">
                    {renderPreview()}
                </Collapsable>
                <Collapsable title="Result">
                    TODO
                </Collapsable>
                <div className={`details ${opened ? '' : 'hidden'}`}>
                    {/*{startedOnce && <Logs id={id}/>}*/}
                    {startedOnce && <ItemListComponent jobId={id} />}
                </div>
            </div>
        </div>
    );
};

