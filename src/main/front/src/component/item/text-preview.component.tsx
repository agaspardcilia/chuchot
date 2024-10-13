import React from 'react';
import { SpinyThingy } from '../../shared/component/spiny-thingy.component';

export type PreviewState = 'none' | 'loading' | 'failed';

interface TextPreviewProps {
    content: string | undefined;
    state: PreviewState;
}

export const TextPreview: React.FC<TextPreviewProps> = ({ content, state }) => {
    if (state === 'loading') {
        return (
            <div><SpinyThingy /> Loading preview...</div>
        );
    }

    if (state === 'failed') {
        return (
            <div>Failed to retrieve preview. 😥</div>
        );
    }

    if (!content) {
        return (
            <div>No content to display.</div>
        );
    }

    return (
        <div>
            <textarea disabled value={content} />
        </div>
    );
};
