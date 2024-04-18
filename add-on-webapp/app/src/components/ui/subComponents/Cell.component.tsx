import React from 'reactn';
import styled from 'styled-components';
import { CellInfo } from 'react-table';

import { useHover } from '../../../hooks';

export default ({ value }: { value: CellInfo['value'] }) => {
  const [hoverRef, isHovered] = useHover();
  const checkWidth =
    hoverRef.current &&
    hoverRef.current.offsetWidth < hoverRef.current.scrollWidth;

  return (
    <Wrapper
      id="CellTextWrapper"
      data-tip={checkWidth && isHovered ? value : ''}
      ref={hoverRef}
    >
      <span id="CellText">{value}</span>
    </Wrapper>
  );
};

const Wrapper = styled.div`
  overflow: hidden;
  text-overflow: ellipsis;
  margin-top: 4px;
`;
