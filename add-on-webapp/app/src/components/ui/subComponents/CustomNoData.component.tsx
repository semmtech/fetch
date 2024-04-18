import React from 'react';
import styled from 'styled-components';

const CustomNoDataComponent = (props: { isLoading: boolean }) => {
  if (props.isLoading) {
    return null; // The property of React table requires a react element or null
  }
  return <Wrapper data-testid="NothingFound">No results found</Wrapper>;
};

export default CustomNoDataComponent;

const Wrapper = styled.div`
  position: absolute;
  color: ${p => p.theme.colors.blackOpacity};
  pointer-events: none;
  top: 45%;
  left: 15px;
  font-weight: bold;
  font-size: medium;
`;
