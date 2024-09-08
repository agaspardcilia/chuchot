export type JobStatus = 'READY' | 'PENDING' | 'IN_PROGRESS' | 'SUCCESS' | 'FAILURE';

export interface JobReport {
     id: string;
     name: string;
     itemName: string;
     creation: string;
     lastUpdate: string;
     status: JobStatus;
}

export interface JobDescription {
    name: string;
    sourceItemName: string;
    parameters: JobDescriptionParameter;
}

// TODO: this parameters could be typed better.
export interface JobDescriptionParameter {
    language: string;
    model: string;
    task: string;
}

export interface JobLogs {
    logs: string[];
    errorLogs: string[];
}
