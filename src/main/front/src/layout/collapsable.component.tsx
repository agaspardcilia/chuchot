import React, { ReactNode, useState } from 'react';
import './collapsable.component.css';

interface CollapsableProps {
    title: string;
    children: ReactNode;
    containerClassName?: string;
}

export const Collapsable: React.FC<CollapsableProps> = ({ title, children, containerClassName }) => {
    const [collapsed, setCollapsed] = useState<boolean>(false);

    return (
        <div className={containerClassName || ''}>
            <div className="collapsable-header pointer" onClick={() => setCollapsed(e => !e)}>
                <span className="expend pointer">[{collapsed ? '+' : '-'}]</span>
                <p>{title}</p>
            </div>
            <div className={collapsed ? 'hidden' : 'visible'}>
                {children}
            </div>
        </div>
    );
};
