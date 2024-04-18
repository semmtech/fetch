import React from 'reactn';
import styled from 'styled-components';

const messagesView = ({
  messages,
  subTitle
}: {
  messages?: string[];
  subTitle: string;
}) =>
  messages &&
  messages.length > 0 && (
    <MessagesWrapper>
      <span>{subTitle}</span>
      <List>
        {messages.map(warning => (
          <Text key={warning}>{warning}</Text>
        ))}
      </List>
    </MessagesWrapper>
  );

// This is the component of the alert message
export default ({
  title,
  warnings,
  errors,
  importStep
}: {
  title: string;
  warnings?: string[];
  errors?: string[];
  importStep?: string;
}) => (
  <Wrapper data-testid="Import">
    <Header>
      <span>{importStep}</span>
      <span>{title}</span>
    </Header>

    {messagesView({ messages: warnings, subTitle: 'Warnings: ' })}
    {messagesView({ messages: errors, subTitle: 'Errors: ' })}
  </Wrapper>
);

const Text = styled.li`
  display: list-item;
  padding-bottom: 5px;
`;

const Header = styled.div`
  font-size: medium;
  display: flex;
  flex-direction: column;
`;

const Wrapper = styled.div`
  font-size: small;
`;

const MessagesWrapper = styled.div`
  padding: 5px;
  user-select: all;
`;

const List = styled.ul`
  margin: 0;
  padding-inline-start: 15px;
`;
