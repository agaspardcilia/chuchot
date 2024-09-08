import React, { useState } from 'react';
import { JobReport, JobStatus } from '../../shared/model/job.model';
import './job-details.component.css';
import { Logs } from './logs.component';
import { SpinyThingy } from '../../shared/component/spiny-thingy.component';
import { ItemListComponent } from '../item/item-list.component';

const statusIcon: { [k in JobStatus]: string | React.JSX.Element } = {
    SUCCESS: '✅',
    FAILURE: '❌',
    READY: '🦧',
    IN_PROGRESS: (<SpinyThingy />),
    PENDING: '🕙',
};

interface JobDetailsProps {
    job: JobReport;
    onStart: (id: string) => void;
    onCancel: (id: string) => void;
    onDelete: (id: string) => void;
}

export const JobDetails: React.FC<JobDetailsProps> = ({ job, onStart, onDelete, onCancel }) => {
    const [opened, setOpened] = useState<boolean>(false);
    const { id, status, itemName, name } = job;
    const isInProgress = ['PENDING', 'IN_PROGRESS'].includes(status);
    const startedOnce = status !== 'READY';

    return (
        <div className="job-container">
            <div className="job-header">
                <span className="expend pointer" onClick={() => setOpened(e => !e)}>[{opened ? '-' : '+'}]</span>
                {' '}
                <span title={id} onClick={() => setOpened(!opened)}>
                    <span title={status}>{statusIcon[status]}</span> - {name} ({itemName})
                </span>
                <span className={`details`}>
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
                <div className={`details ${opened ? '' : 'hidden'}`}>
                    {/*{startedOnce && <Logs id={id}/>}*/}
                    {startedOnce && <ItemListComponent jobId={id}/>}

                </div>
            </div>

        </div>
    );
};

