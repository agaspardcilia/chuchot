import React, { useState } from 'react';
import { Item } from '../../shared/model/item.model';
import './item.css';

interface ItemProps {
    item: Item;
    showPreview?: boolean;
    playable?: boolean;
}

export const ItemComponent: React.FC<ItemProps> = ({ item, showPreview, playable }: ItemProps) => {
    const [playerOpened, setPlayerOpen] = useState<boolean>(false);

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

    const getHeaderContent = () => {
        const { metaData } = item;
      switch (metaData.type) {
          case "VIDEO":
              return (<>
                  <img className="item-thumbnail" src={metaData.thumbnailLink} alt={`${item.name} Thumbnail`}/>
                  {' '}
                  <span className="item-name">{item.name}</span> ({getItemSize()})
              </>);
          case "OTHER":
          case "AUDIO":
          case "Text":
              return (<>📄 <span className="item-name">{item.name}</span> ({getItemSize()})</>);
      }
    };

    return (
        <div className="item-container">
            <div className="item-header" title={`Created on: ${item.creation}\nLast updated on: ${item.lastUpdated}`}>
                {getHeaderContent()}
                <br />
                <a target="_blank" href={item.downloadLink}>📥</a>
                {playable
                    ? <>
                    {' '}
                        <span className="pointer" onClick={() => setPlayerOpen((prev) => !prev)}>{playerOpened ? '[retract]' : '▶️'}</span>
                    </>
                    : undefined
                }
            </div>
            <div className="item-body">
                {playerOpened
                    ? (<video controls >
                        <source src={item.downloadLink} />
                    </video>)
                    : undefined}
            </div>
        </div>
    );
};
