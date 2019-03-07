import React, { Component } from 'react';
import logo from './logo.svg';
import './App.css';

class App extends Component {
  constructor(props) {
    super(props);
    this.state = { msg: "Default message" };
    this.eventSource = new EventSource('http://localhost:8090/toggle-server/sse');
  }

  componentDidMount() {
    this.eventSource.onmessage = e => this.updateMsg(e);
  }

  updateMsg(msg) {
    this.setState(Object.assign({}, { "msg": msg.data }));
  }

  render() {
    return (
      <div className="App">
        <header className="App-header">
          <img src={logo} className="App-logo" alt="logo" />
          <h1 className="App-title">Welcome to React</h1>
        </header>
        <p className="App-intro">
          To get started, edit <code>src/App.js</code> and save to reload.
        </p>
        <p>Message from sse: {this.state.msg}</p>
      </div>
    );
  }
}

export default App;
