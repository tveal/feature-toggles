import React, { Component } from 'react';
import base64 from 'base-64';
import ToggleRow from './ToggleRow'
import './styles.css';

class ToggleList extends Component {
  constructor(props) {
    super(props);
    this.state = { msg: {} };
    this.eventSource = new EventSource('http://localhost:8090/toggle-server/sse');
  }

  componentDidMount() {
    this.eventSource.onmessage = e => this.updateMsg(e);
  }

  updateMsg(updateMsg) {
    const toggleUpdate = JSON.parse(base64.decode(updateMsg.data));
    let newState = this.state.msg;
    Object.keys(toggleUpdate).map((key, i) => {
      newState[key] = toggleUpdate[key];
    });
    this.setState(Object.assign({}, { "msg": newState }));
  }

  render() {
    return (
      <div className="toggle-list">
        <header className="list-header">
          <h1 className="list-title">Welcome to React</h1>
        </header>
        <p className="list-intro">
          To get started, edit <code>src/components/ToggleList/index.js</code> and save to reload.
        </p>
        {Object.keys(this.state.msg).map((key, i) => (
          <ul key={i}>
            <ToggleRow toggleId={key} toggleValue={this.state.msg[key]} />
          </ul>
        ))}
      </div>
    );
  }
}

export default ToggleList;
