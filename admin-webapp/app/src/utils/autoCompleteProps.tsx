import React from 'react';
import { makeStyles } from '@material-ui/core/styles';
import TextField from '@material-ui/core/TextField';
import { RenderInputParams } from '@material-ui/lab/Autocomplete';
import styled from 'styled-components';
import { InputAdornment } from '@material-ui/core';

import Relatics from '../img/Relatics.png';
import logoNeanex from '../img/logoNeanex.png';
import { colors } from '../utils/colors';
import { globalTextFieldProps } from '.';
import { AutoProps, AutoCompleteOption } from '../types';
import constants from '../constants';

const imgProps = {
  height: '20',
  width: '20',
  style: { margin: 'auto 25px' }
};

const relaticsImg = (
  <img
    alt="Relatics logo"
    data-testid="RelaticsImgSearch"
    src={Relatics}
    {...imgProps}
  />
);
const neanexImg = (
  <img
    alt="Neanex logo"
    data-testid="NeanexImgSearch"
    src={logoNeanex}
    {...imgProps}
  />
);

export const useStyles = makeStyles({
  option: {
    fontSize: 15,
    border: '1px solid rgba(27,31,35,.15)',
    boxShadow: '0 3px 12px rgba(27,31,35,.15)',
    borderRadius: 5,
    minHeight: 'auto',
    alignItems: 'flex-start',
    padding: 8,
    '& > span': {
      marginRight: 10,
      fontSize: 18
    },
    '&[aria-selected="true"]': {
      backgroundColor: colors.grey
    }
  },
  listbox: {
    padding: 10
  },
  paper: {
    padding: 10,
    border: '1px solid rgb(76, 76, 76, 0.5)',
    backgroundColor: colors.lightGrey
  }
});

export const autoProps = ({
  label,
  required,
  error,
  classes,
  style,
  disableDelete,
  subItem,
  typeSubItem,
  configType
}: AutoProps) => ({
  ...(disableDelete && {
    disableClearable: true
  }),
  id: `Autocomplete_${label}`,
  classes: {
    option: classes.option,
    paper: classes.paper,
    listbox: classes.listbox
  },
  autoHighlight: true,
  style: { width: '100%', ...style },
  noOptionsText: 'Nothing found',
  getOptionLabel: (option: AutoCompleteOption) => option.display,
  renderOption: (option: AutoCompleteOption) => (
    <React.Fragment>
      {subItem && option.envType === constants.workspace && relaticsImg}
      {subItem && option.envType === constants.jsonapi && neanexImg}
      <Wrapper data-testid={option.value}>{option.display}</Wrapper>
    </React.Fragment>
  ),
  renderInput: (params: RenderInputParams) => (
    <TextField
      {...params}
      {...globalTextFieldProps(true, required)}
      label={label}
      error={error}
      autoComplete="no"
      inputProps={{
        ...params.inputProps,
        autoComplete: 'off',
        ...(!disableDelete && {
          style: {
            flexGrow: 'unset',
            width: '70%'
          }
        })
      }}
      InputProps={{
        ...params.InputProps,
        style: {
          paddingRight: '10px',
          paddingLeft: '10px'
        },
        ...(subItem &&
          Boolean(typeSubItem) && {
            startAdornment: configType ? (
              <InputAdornment
                style={{ height: '100%', paddingTop: '5px' }}
                position="start"
              >
                {relaticsImg}
              </InputAdornment>
            ) : (
              <InputAdornment
                style={{ height: '100%', paddingTop: '3px' }}
                position="start"
              >
                {neanexImg}
              </InputAdornment>
            )
          })
      }}
    />
  )
});

const Wrapper = styled.div`
  width: 100%;
  display: flex;
  flex-direction: row;
  align-items: center;
`;
