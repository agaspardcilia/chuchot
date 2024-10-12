import React from 'react';
import { JobReport, JobStatus } from '../../shared/model/job.model';
import { SpinyThingy } from '../../shared/component/spiny-thingy.component';

interface JobLineProps {
    job: JobReport;
}

const statusIcon: { [k in JobStatus]: string | React.JSX.Element } = {
    SUCCESS: '✅',
    FAILURE: '❌',
    READY: '🦧',
    IN_PROGRESS: (<SpinyThingy/>),
    PENDING: '🕙'
};

export const JobLine: React.FC<JobLineProps> = ({ job }) => {
    const { id, status, description } = job;
    const { name, sourceItemName } = description;
    return (
        <span title={id}>
            <span title={status}>{statusIcon[status]}</span> :: {name} ({sourceItemName})
        </span>
    );
};
