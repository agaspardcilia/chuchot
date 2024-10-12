import { Result } from '../model/result.model';
import { Http } from '../util/http.util';
import { JobDescription, JobLogs, JobReport } from '../model/job.model';

const basePath = '/job';

export const jobApi = {
    update: (description: JobDescription, id?: string): Promise<Result<JobReport>> =>
        Http.post<JobReport>(id ? `${basePath}/${id}` : basePath, description),
    getAll: (): Promise<Result<JobReport[]>> => Http.get<JobReport[]>(`${basePath}/all`),
    start: (id: string): Promise<Result<void>> => Http.post<void>(`${basePath}/start/${id}`),
    delete: (id: string): Promise<Result<void>> => Http.delete<void>(`${basePath}/${id}`),
    getLogs: (id: string, fromLine: number): Promise<Result<JobLogs>> => Http.get<JobLogs>(`${basePath}/${id}/logs-from/${fromLine}`),
    subscribeToUpdates: (onMessage: (ev: MessageEvent) => any): EventSource => {
        const eventSource = Http.sse(`${basePath}/sse/job-update`);
        eventSource.onmessage = onMessage;
        return eventSource;
    },
};
