import { create } from 'zustand';
import { JobDescription, JobLogs, JobReport } from '../model/job.model';
import { jobApi } from '../api/jobs.api';
import { tartine } from '../util/tartine';

interface JobStore {
    jobs: JobReport[];
    logs: {[k in string]: JobLogs};
    loading: boolean;
    fetch: (silent?: boolean) => Promise<void>;
    update: (description: JobDescription, id?: string) => Promise<JobReport | null>;
    start: (id: string) => Promise<void>;
    delete: (id: string) => Promise<void>;
    fetchLogs: (id: string) => Promise<void>;
    getLogs: (id: string) => JobLogs;
    selectedJob: JobReport | null;
    setSelectedJob: (id: string) => void;
    subscribeToUpdates: () => void;
}

export const useJobStore = create<JobStore>(
    (set, get) => ({
        jobs: [],
        logs: {},
        loading: false,
        fetch: async (silent: boolean = false) => {
            !silent && set({ loading: true });
            const response = await jobApi.getAll();
            if (response.status === 'success') {
                set({ jobs: response.result, loading: false });
            } else {
                throw new Error('Failed to fetch jobs');
            }
        },
        update: async (description: JobDescription, id?: string) => {
            const response = await jobApi.update(description, id);
            if (response.status === 'success') {
                return response.result;
            } else {
                throw new Error('Failed to update job');
            }
        },
        start: async (id: string) => {
            const response = await jobApi.start(id);
            if (response.status === 'success') {
                return response.result;
            } else {
                throw new Error('Failed to start job');
            }
        },
        delete: async (id: string) => {
            const response = await jobApi.delete(id);
            if (response.status === 'success') {
                return response.result;
            } else {
                throw new Error('Failed to delete job');
            }
        },
        fetchLogs: async (id: string) => {
            const logs = get().logs;
            const log = logs[id];
            // Get the last line number or 0.
            const fromLine = log ? Math.max(log.logs.length - 1, 0) : 0;
            const response = await jobApi.getLogs(id, fromLine);
            if (response.status === 'success') {
                set({logs : {...logs, [id]: response.result}})
            } else {
                throw new Error('Failed to fetch logs');
            }
        },
        getLogs: (id: string): JobLogs => {
            const logs = get().logs[id];
            if (logs) {
                return logs;
            } else {
                return { logs: [], errorLogs: []};
            }
        },

        selectedJob: null,
        setSelectedJob: (id: string) => {
            const job = get().jobs
                .find((j) => j.id === id);
            if (!job) {
                tartine.error(`No job to be found for id '${id}'`);
                set({ selectedJob: null });
            }

            set({ selectedJob: job});
        },
        subscribeToUpdates: () => {
            jobApi.subscribeToUpdates((event) => {
                get().fetch(true);

            })
        }
    })
);
