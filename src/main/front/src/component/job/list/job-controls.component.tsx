import React from 'react';

interface JobControlsProps {
    onFilterChange: (field: string, direction: 'asc' | 'desc') => void;
    selectedFilter?: string;
    onAdd: () => void;
}

export const JobControls: React.FC<JobControlsProps> = ({ onAdd }) => {
    return (
        <>
            <button onClick={onAdd}>➕ Add</button>
            <select>
                <option>Something</option>
                <option>Something else</option>
            </select>
        </>
    );
}
