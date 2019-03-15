import React from 'react';
import { Table } from 'semantic-ui-react';
import PropTypes from 'prop-types';
import CheckboxToggle from './CheckboxToggle';

const ToggleRow = ({ toggleId, toggleValue }) => {
    return (
        <Table.Row className="toggle-row">
            <Table.Cell className="toggle-cell id-cell">{toggleId}</Table.Cell>
            <Table.Cell className="toggle-cell val-cell">
                <CheckboxToggle toggleId={toggleId} toggleValue={toggleValue} />
            </Table.Cell>
        </Table.Row>
    )
};

export default ToggleRow;

ToggleRow.propTypes = {
    toggleId: PropTypes.string,
    toggleValue: PropTypes.bool
};

ToggleRow.defaultProps = {
    toggleUpdate: {}
};