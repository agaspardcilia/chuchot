import React, { useEffect } from 'react';
import { useJobStore } from '../../../shared/store/jobs.store';

interface LogsProps {
    id: string;
}

export const Logs: React.FC<LogsProps> = ({ id }) => {
    const { fetchLogs, getLogs } = useJobStore();

    useEffect(() => {
        fetchLogs(id);
        const interval = setInterval(() => {
            fetchLogs(id);
        }, 2000);
        return () => {
            clearInterval(interval);
        };
    }, []);

    const logs = getLogs(id);

    return (
        <div>
            Logs:
            <textarea cols={30} rows={10}>{logs.logs.join('\n')}</textarea>
            Errors:
            <textarea cols={30} rows={10}>{logs.errorLogs.join('\n')}</textarea>
        </div>

    );
};
