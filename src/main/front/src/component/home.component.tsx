import React from 'react';
import { JobListComponent } from './job/list/job-list.component';
import './home.component.css';
import { SelectedJob } from './job/details/selected-job.component';

export const Home: React.FC = () => {
    return (
        <div className="home-container">
            <div className="left-pane">
                <JobListComponent/>
            </div>
            <div className="right-pane">
                <SelectedJob />
            </div>
        </div>
    );
};
