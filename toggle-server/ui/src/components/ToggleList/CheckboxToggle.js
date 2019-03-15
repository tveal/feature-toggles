import React from 'react'
import { Checkbox } from 'semantic-ui-react'
import './checkbox.css';

const toggleSwitchUrl = "http://localhost:8090/toggle-server/switch?toggleId=";

const CheckboxToggle = ({ toggleId, toggleValue }) => <Checkbox
    className={toggleValue ? "enabled" : "disabled"}
    toggle checked={toggleValue}
    onClick={() => {
        fetch(toggleSwitchUrl + toggleId).then(resp => console.log(resp));
    }}
/>

export default CheckboxToggle
