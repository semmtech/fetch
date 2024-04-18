import React from 'reactn';
import TextField from '@material-ui/core/TextField';
import Visibility from '@material-ui/icons/Visibility';
import VisibilityOff from '@material-ui/icons/VisibilityOff';
import InputAdornment from '@material-ui/core/InputAdornment';

import constants from '../../../constants';
import { globalTextFieldProps, checkError } from '../../../utils';
import { Row, inputIcon, FormWrapper } from '../../../utils/styles';
import { AuthenticationFields } from '../../../types';

export default ({
  authenticationType,
  values,
  onChange,
  onShow,
  show
}: AuthenticationFields) => {
  switch (authenticationType) {
    case constants.menuItems.basic:
      return (
        <FormWrapper autoComplete="none">
          <TextField
            error={checkError(values.userName)}
            label="User name"
            id="userName"
            value={values.userName || ''}
            onChange={e => onChange(e, 'userName')}
            style={{ margin: '10px' }}
            autoComplete="new-password"
            {...globalTextFieldProps(true, true)}
          />
          <TextField
            error={checkError(values.password)}
            label="Password"
            id="password"
            style={{ margin: '10px' }}
            value={values.password || ''}
            onChange={e => onChange(e, 'password')}
            type={show ? 'password' : 'text'}
            autoComplete="new-password"
            InputProps={{
              endAdornment: (
                <InputAdornment position="end">
                  {show ? (
                    <Visibility
                      onClick={() => onShow()}
                      style={inputIcon}
                      data-testid="showPassword"
                    />
                  ) : (
                    <VisibilityOff
                      onClick={() => onShow()}
                      style={inputIcon}
                      data-testid="hidePassword"
                    />
                  )}
                </InputAdornment>
              )
            }}
            {...globalTextFieldProps(true, true)}
          />
        </FormWrapper>
      );
    case constants.menuItems.tokenUnderscore:
      return (
        <FormWrapper>
          <TextField
            error={checkError(values.applicationId)}
            label="Application id"
            id="applicationId"
            value={values.applicationId || ''}
            onChange={e => onChange(e, 'applicationId')}
            style={{ margin: '10px' }}
            {...globalTextFieldProps(true, true)}
          />
          <TextField
            error={checkError(values.privateKey)}
            label="Private key"
            id="privateKey"
            style={{ margin: '10px' }}
            value={values.privateKey || ''}
            onChange={e => onChange(e, 'privateKey')}
            type={show ? 'password' : 'text'}
            InputProps={{
              endAdornment: (
                <InputAdornment position="end">
                  {show ? (
                    <Visibility
                      onClick={() => onShow()}
                      style={inputIcon}
                      data-testid="showPrivateKey"
                    />
                  ) : (
                    <VisibilityOff
                      onClick={() => onShow()}
                      style={inputIcon}
                      data-testid="hidePrivateKey"
                    />
                  )}
                </InputAdornment>
              )
            }}
            {...globalTextFieldProps(true, true)}
          />
        </FormWrapper>
      );
    default:
      return <Row />;
  }
};
