import { JobDescriptionParameter } from '../../../../shared/model/job.model';
import React from 'react';

interface JobParametersProps {
    parameters: JobDescriptionParameter;
}

export const JobParameters: React.FC<JobParametersProps> = ({ parameters }) => {
    const { task, model, language } = parameters;
    return (
        <ul>
            <li>Tak {task}</li>
            <li>Model {model}</li>
            <li>Language {language}</li>
        </ul>
    );
};
