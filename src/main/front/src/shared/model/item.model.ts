export interface Item {
    name: string;
    downloadLink: string;
    size: number;
    creation: string;
    lastUpdated: string;
    lastAccessed: string;
    metaData: MetaData
}

export type MetaData = VideoMetaData | AudioMetaData | TextMetaData | OtherMetaData;

interface VideoMetaData {
    type: 'VIDEO';
    name: string;
    duration: number;
    formattedDuration: string;
    thumbnailLink: string;
}

interface AudioMetaData {
    type: 'AUDIO';
    name: string;
    duration: number;
    formattedDuration: string;
}

interface TextMetaData {
    type: 'TEXT';
    name: string;
}

interface OtherMetaData {
    type: 'OTHER';
}
