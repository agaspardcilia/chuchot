import React, { useState } from 'react';
import { Item } from '../../shared/model/item.model';
import './item.css';
import { VideoPreview } from './video-preview.component';
import { useItemStore } from '../../shared/store/item-store.store';
import { tartine } from '../../shared/util/tartine';
import { PreviewState, TextPreview } from './text-preview.component';

interface ItemProps {
    item: Item;
}

export const ItemComponent: React.FC<ItemProps> = ({ item }: ItemProps) => {
    const { downloadItem } = useItemStore();
    const [previewOpened, setPreviewOpened] = useState<boolean>(false);
    const [previewState, setPreviewState] = useState<'none' | 'loading' | 'failed' | 'success'>('none');
    const [previewContent, setPreviewContent] = useState<string>();

    const onPreviewToggle = () => {
        if (previewState === 'none') {
            setPreviewState('loading');
            downloadItem(item).then(res => {
                setPreviewContent(res);
                setPreviewState('success');
            }).catch(err => {
                tartine.error(`Failed to load preview for ${item.name}`);
                setPreviewState('failed');
                console.error(err);
            });
        }
        setPreviewOpened(pred => !pred);
    };

    const getItemSize = (): string => {
        const { size } = item;
        if (size > 1000000000) {
            return `${Math.trunc(item.size / 1000000000)}Gb`;
        } else if (size > 100000) {
            return `${Math.trunc(item.size / 1000000)}Mb`;
        } else {
            return `${Math.trunc(item.size / 1000)}Kb`;
        }
    };

    const renderIcon = () => {
        const { metaData } = item;
        switch (metaData.type) {
            case 'VIDEO':
                if (previewOpened) {
                    return undefined;
                }
                return (
                    <button onClick={() => setPreviewOpened(true)}>
                        <img className="item-thumbnail pointer"
                             src={metaData.thumbnailLink}
                             alt={`${item.name} Thumbnail`}/>
                    </button>
                );
            case 'OTHER':
            case 'AUDIO':
            case 'TEXT':
                return '📄';
        }
    };

    const renderItemTitle = () => {
        const { metaData } = item;
        switch (metaData.type) {
            case 'AUDIO':
            case 'VIDEO':
                return (
                    <>
                        <span className="item-name">{item.name}</span>
                        {' - '}
                        <span title="hh:mm:ss">{metaData.formattedDuration}</span> ({getItemSize()})
                    </>
                );
            case 'OTHER':
            case 'TEXT':
                return (<><span className="item-name">{item.name}</span> ({getItemSize()})</>);
        }
    };

    const renderPreview = () => {
        if (!previewOpened) {
            return undefined;
        }

        const { metaData } = item;
        switch (metaData.type) {
            case 'AUDIO':
            case 'VIDEO':
                return (
                    <div className="item-video">
                        <VideoPreview sourceItem={item}/>
                    </div>
                );
            case 'OTHER':
            case 'TEXT':
                const getLocalState = (): PreviewState => {
                    switch (previewState) {
                        case 'failed':
                            return 'failed';
                        case 'loading':
                            return 'loading';
                        default:
                            return 'none';
                    }
                };

                return (
                    <>
                        <TextPreview content={previewContent} state={getLocalState()} />
                    </>
                );
        }
    };

    const renderControls = () => {
        return (
            <>
                <a target="_blank" href={item.downloadLink}>📥</a>
                {' '}
                <a className="pointer"
                   onClick={() => onPreviewToggle()}>
                    {previewOpened ? '[retract]' : '[preview]'}
                </a>

            </>
        );
    }

    return (
        <div className="item-container">
            <div className="item-icon">{renderIcon()}</div>
            <div className="item-body">
                <div className="item-title">
                    {renderItemTitle()}
                    {' '}
                    {renderControls()}
                </div>
                {renderPreview()}
            </div>
        </div>
    );
};
