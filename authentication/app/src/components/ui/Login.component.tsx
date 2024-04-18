import { useGlobal } from 'reactn';
import React, { useState } from 'react';
import styled from 'styled-components';
import ReactTooltip from 'react-tooltip';
import { FaUser, FaKey, FaEye, FaEyeSlash } from 'react-icons/fa';
import Button from '@material-ui/core/Button';
import InputAdornment from '@material-ui/core/InputAdornment';
import TextField from '@material-ui/core/TextField';

import Logo from '../../img/Laces.png';
import { feedbackMessage } from '../../utils';
import { icon } from '../../utils/styles';
import constants from '../../constants';

export default () => {
  const [hidden, setHidden] = useState(true);

  const [password, setPassword] = useState('');
  const [username, setUsername] = useState('');
  const login: any = useGlobal('login');

  const showPassword = () => {
    setHidden(!hidden);
  };

  return (
    <Container>
      <img src={Logo} alt="Laces logo" height="250" width="250" />
      <Title>Laces Fetch</Title>
      <LoginForm>
        <Wrapper>
          <TextField
            id="userName"
            fullWidth={true}
            autoFocus={true}
            value={username}
            onChange={e => setUsername(e.target.value)}
            type="text"
            placeholder={constants.username}
            InputProps={{
              startAdornment: (
                <InputAdornment position="start">
                  <FaUser
                    size={25}
                    style={{
                      color: '#4c4c4c'
                    }}
                  />
                </InputAdornment>
              )
            }}
          />
        </Wrapper>
        <Wrapper>
          <TextField
            id="password"
            fullWidth={true}
            type={hidden ? 'password' : 'text'}
            placeholder={constants.password}
            onChange={e => setPassword(e.target.value)}
            onKeyPress={event => {
              if (event.key === 'Enter') {
                login(username, password);
              }
            }}
            value={password}
            InputProps={{
              endAdornment: (
                <InputAdornment position="end">
                  {hidden ? (
                    <FaEye
                      data-tip={constants.showPassword}
                      onClick={showPassword}
                      size={25}
                      style={icon as React.CSSProperties}
                    />
                  ) : (
                    <FaEyeSlash
                      data-tip={constants.hidePassword}
                      onClick={showPassword}
                      size={25}
                      style={icon as React.CSSProperties}
                    />
                  )}
                </InputAdornment>
              ),
              startAdornment: (
                <InputAdornment position="start">
                  <FaKey
                    size={25}
                    style={{
                      color: '#4c4c4c'
                    }}
                  />
                </InputAdornment>
              )
            }}
          />
          <ReactTooltip place="right" type="dark" effect="float" />
        </Wrapper>
        <Wrapper>
          <Button
            data-testid="LoginButton"
            fullWidth={true}
            style={{
              backgroundColor: '#F6E524',
              color: '#4c4c4c'
            }}
            onClick={(): void => login(username, password)}
          >
            Login
          </Button>
        </Wrapper>
      </LoginForm>
      {feedbackMessage(
        'logout',
        'You have been logged out, please login again if you want to use the application.'
      )}
      {feedbackMessage('error', 'Login was unsuccessfull, please try again.')}
    </Container>
  );
};

const Container = styled.div`
  padding: 20px;
  display: flex;
  flex-direction: column;
  align-items: center;
`;

const LoginForm = styled.form`
  display: flex;
  flex-direction: column;
  align-items: center;
`;

const Wrapper = styled.div`
  width: 100%;
  margin: 10px;
  font-size: 20px;
  display: flex;
`;

const Title = styled.h1`
  color: #4c4c4c;
`;
