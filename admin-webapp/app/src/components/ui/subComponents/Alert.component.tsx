import React from 'reactn';
import styled from 'styled-components';

// This is the component of the error alert message
export default ({
  title,
  feedback,
  errMessage,
  type
}: {
  title: string;
  feedback: string;
  errMessage?: string;
  type?: 'error' | 'warning';
}) => (
  <Wrapper type={type}>
    <Header>{title}</Header>
    <MessagesWrapper>
      <span>{feedback}</span>
      <span>{errMessage}</span>
    </MessagesWrapper>
  </Wrapper>
);

const Header = styled.span`
  font-size: medium;
`;

const MessagesWrapper = styled.div`
  padding: 5px;
  user-select: all;
`;

const Wrapper = styled.div<{ type?: 'error' | 'warning' }>`
  font-size: small;
  display: flex;
  flex-direction: column;
  color: ${p =>
    p.type === 'warning' ? p.theme.colors.blackOpacity : p.theme.colors.white};
`;
