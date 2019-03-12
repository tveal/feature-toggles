import React from 'react';
import ReactDOM from 'react-dom';
import './index.css';
import ToggleList from './components/ToggleList';
import registerServiceWorker from './registerServiceWorker';

ReactDOM.render(<ToggleList />, document.getElementById('root'));
registerServiceWorker();
