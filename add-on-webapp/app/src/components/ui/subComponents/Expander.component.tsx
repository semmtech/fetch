import { useDispatch } from 'reactn';
import React from 'react';
import styled from 'styled-components';
import Icon from '@material-ui/core/Icon';

import { IRowProps } from '../../../types';
import constants from '../../../constants';

// This is the component of the expander
export default ({
  isExpanded,
  origin,
  level
}: {
  isExpanded: boolean;
  origin: IRowProps;
  level: number;
}) => {
  const fetchChildren = useDispatch('fetchChildren'); // calling reducer fetchChildren
  const name = origin.uri.replace(/.*\//, '');

  return (
    <ExpanderContainer
      onClick={() => {
        if (origin.children.length === 0) {
          fetchChildren(true, origin, level);
        }
      }}
      data-testid={`Expand_${name}`}
      id={constants.iconExpandWrapper}
    >
      {isExpanded ? (
        <Icon
          id={constants.iconExpand}
          data-testid={`Down_${name}`}
          style={{ fontSize: 20 }}
        >
          expand_more
        </Icon>
      ) : (
        <Icon
          id={constants.iconExpand}
          data-testid={`Right_${name}`}
          style={{ fontSize: 20 }}
        >
          chevron_right
        </Icon>
      )}
    </ExpanderContainer>
  );
};

const ExpanderContainer = styled.div`
  cursor: pointer;
  display: flex;
  flex-direction: row;
  padding: 1px;
  margin: auto;
`;
