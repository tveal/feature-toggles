import React from 'react';
import PropTypes from 'prop-types';

const ToggleRow = ({ toggleId, toggleValue }) => {
    return (
        <div>
            toggleId: {toggleId};
            value: {"" + toggleValue}
        </div>
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