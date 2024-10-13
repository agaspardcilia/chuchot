import React from 'react';
import { Item } from '../../shared/model/item.model';

interface VideoPreviewProps {
    sourceItem: Item;
    subtitles?: {language: string, item: Item};
}

export const VideoPreview: React.FC<VideoPreviewProps> = ({sourceItem, subtitles}) => {
    const { downloadLink } = sourceItem;
    return (
        <div>
            <video controls crossOrigin="anonymous">
                <source src={downloadLink}/>
                {subtitles && (
                    <track default src={subtitles.item.downloadLink} kind="subtitles" srcLang={subtitles.language} label="Transcript" />
                )}
            </video>
        </div>
    );
};
