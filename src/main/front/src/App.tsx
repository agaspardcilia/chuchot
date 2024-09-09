import React, { ReactElement } from 'react';
import './App.css';
import { Header } from './layout/header.component';
import { Home } from './component/home.component';
import 'react-toastify/dist/ReactToastify.css';
import { ToastContainer } from 'react-toastify';
import { Startup } from './startup.component';

const App = (): ReactElement => {
    return (
        <>
            <Startup />
            <Header />
            <Home />
            <ToastContainer />
        </>
    );
};

export default App;
