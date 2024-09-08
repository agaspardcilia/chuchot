import React, { useEffect, useState } from 'react';

const ticks: string[] = ['\\', '|', '/', '-'];

export const SpinyThingy: React.FC = ({}) => {
    const [tick, setTick] = useState<number>(0);

    useEffect(() => {
        const interval = setInterval(() => {
            setTick((prevState) => (prevState + 1) % ticks.length);
        }, 150);
        return () => {
            clearInterval(interval);
        }
    }, []);

    return (<span>{ticks[tick]}</span>);
};
