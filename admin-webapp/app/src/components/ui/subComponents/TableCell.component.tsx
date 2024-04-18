import React from 'reactn';
import styled from 'styled-components';

import { useHover } from '../../../hooks';
import { CustomTooltip } from '../../../utils';

export default ({
  value
}: {
  value: string | boolean | number | undefined | null;
}) => {
  const [hoverRef, isHovered] = useHover();
  const checkWidth =
    hoverRef.current &&
    hoverRef.current.offsetWidth < hoverRef.current.scrollWidth;

  return (
    <CustomTooltip
      title={<TooltipContent>{value}</TooltipContent>}
      disableFocusListener={true}
      disableTouchListener={true}
      disableHoverListener={!checkWidth && isHovered}
      leaveTouchDelay={0}
      PopperProps={{
        disablePortal: true
      }}
      enterDelay={100}
    >
      <Wrapper ref={hoverRef}>
        <span>{value}</span>
      </Wrapper>
    </CustomTooltip>
  );
};

const Wrapper = styled.div`
  overflow: hidden;
  text-overflow: ellipsis;
`;

const TooltipContent = styled.span`
  white-space: pre-wrap;
  display: block;
`;
