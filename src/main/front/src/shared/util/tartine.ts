import { toast } from 'react-toastify';

export const tartine = {
    success: (content: string) => toast(content, { type: 'success'}),
    error: (content: string) => toast(content, { type: 'error'}),
    info: (content: string) => toast(content, { type: 'info'}),
};
