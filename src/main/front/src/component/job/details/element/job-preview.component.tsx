import React from 'react';
import { JobDescription } from '../../../../shared/model/job.model';
import { Item } from '../../../../shared/model/item.model';

interface JobPreviewProps {
    job: JobDescription;
    sourceItem: Item;
    subtitles?: Item;
}

export const JobPreview: React.FC<JobPreviewProps> = ({job, sourceItem, subtitles}) => {
    const { downloadLink } = sourceItem;
    console.log('subLink', subtitles?.downloadLink);
    return (
        <div>
            <video controls crossOrigin="anonymous">
                <source src={downloadLink}/>
                {subtitles && (
                    <track default src={subtitles.downloadLink} kind="subtitles" srcLang={job.parameters.language} label="Transcript" />
                )}
            </video>
        </div>
    );
};
