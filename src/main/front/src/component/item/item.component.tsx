import React, { useState } from 'react';
import { Item } from '../../shared/model/item.model';
import './item.css';

interface ItemProps {
    item: Item;
    showPreview?: boolean;
    playable?: boolean;
}

export const ItemComponent: React.FC<ItemProps> = ({ item, showPreview, playable }: ItemProps) => {
    const [playerOpened, setPlayerOpened] = useState<boolean>(false);

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

    const getIcon = () => {
        const { metaData } = item;
        switch (metaData.type) {
            case 'VIDEO':
                if (playerOpened) {
                    return undefined;
                }
                return (
                    <button onClick={() => setPlayerOpened(true)}>
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

    const getItemTitle = () => {
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

    const getControls = () => {
        return (
            <>
                <a target="_blank" href={item.downloadLink}>📥</a>
                {playable
                    ? <>
                        {' '}
                        <a className="pointer"
                           onClick={() => setPlayerOpened((prev) => !prev)}>
                            {playerOpened ? '[retract]' : '▶️'}
                        </a>
                    </>
                    : undefined
                }
            </>
        );
    }

    return (
        <div className="item-container">
            <div className="item-icon">{getIcon()}</div>
            <div className="item-body">
                <div className="item-title">
                    {getItemTitle()}
                    {' '}
                    {getControls()}
                </div>
                {playerOpened ? (
                    <div className="item-video">
                        <video controls>
                            <source src={item.downloadLink}/>
                        </video>
                    </div>
                ) : undefined}
            </div>
        </div>
    );
};
