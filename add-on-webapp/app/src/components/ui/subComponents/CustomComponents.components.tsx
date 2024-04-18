import React, { useState } from 'react';
import {
  TextField as MTextField,
  TextFieldProps as MTextFieldProps,
  Button as MButton,
  ButtonProps as MButtonProps,
  IconButton as MIconButton,
  IconButtonProps as MIconButtonProps,
  CircularProgress
} from '@material-ui/core';
import _noop from 'lodash/noop';
import {
  Autocomplete as MAutocomplete,
  AutocompleteProps as MAutocompleteProps,
  UseAutocompleteSingleProps
} from '@material-ui/lab';
import { makeStyles } from '@material-ui/core/styles';
import { buttonStyle } from '../../../utils/styles';

export const classes = makeStyles(theme => ({
  backdrop: {
    zIndex: theme.zIndex.drawer + 1
  },
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
    }
  },
  listbox: {
    padding: 10
  },
  paper: {
    padding: 10,
    border: '1px solid rgb(76, 76, 76, 0.5)'
  }
}));

type TextFieldProps = Pick<
  MTextFieldProps,
  | 'label'
  | 'id'
  | 'required'
  | 'disabled'
  | 'InputProps'
  | 'value'
  | 'defaultValue'
  | 'autoFocus'
  | 'onChange'
  | 'onBlur'
  | 'error'
  | 'inputRef'
  | 'multiline'
  | 'rows'
  | 'rowsMax'
  | 'size'
  | 'name'
  | 'onKeyPress'
>;

export const TextField = ({
  required = false,
  disabled = false,
  InputProps = {},
  ...props
}: TextFieldProps) => (
  <MTextField
    {...props}
    required={required}
    disabled={disabled}
    label={props.label}
    value={props.value}
    defaultValue={props.defaultValue}
    fullWidth={true}
    autoFocus={false}
    InputProps={InputProps}
    onBlur={props.onBlur}
    variant="outlined"
    margin="dense"
    onChange={props.onChange}
    onKeyPress={props.onKeyPress}
  />
);

type Props<T> = {
  label?: string;
  required?: boolean;
} & Omit<MAutocompleteProps<T>, 'renderInput'> &
  UseAutocompleteSingleProps<T>;

type AutocompleteProps = Pick<
  Props<{ value: string; display: string }>,
  | 'onChange'
  | 'options'
  | 'loading'
  | 'disabled'
  | 'onOpen'
  | 'value'
  | 'defaultValue'
  | 'id'
  | 'label'
  | 'required'
  | 'style'
  | 'noOptionsText'
>;

export const Autocomplete = ({
  loading = false,
  disabled = false,
  onOpen = _noop,
  noOptionsText = 'Nothing found',
  style = { width: '100%' },
  ...props
}: AutocompleteProps) => {
  const [open, setOpen] = useState(false);

  return (
    <MAutocomplete
      {...props}
      classes={classes()}
      disabled={disabled}
      id={props.id}
      loading={loading}
      open={open}
      value={props.value}
      onOpen={e => {
        setOpen(true);
        onOpen(e);
      }}
      onClose={() => {
        setOpen(false);
      }}
      style={style}
      noOptionsText={noOptionsText}
      getOptionLabel={option => option.display}
      renderOption={option => option.display}
      renderInput={params => (
        <TextField
          {...params}
          label={props.label}
          InputProps={{
            ...params.InputProps,
            endAdornment: (
              <React.Fragment>
                {loading ? (
                  <CircularProgress color="inherit" size={20} />
                ) : null}
                {params.InputProps.endAdornment}
              </React.Fragment>
            )
          }}
        />
      )}
      options={props.options}
      onChange={props.onChange}
    />
  );
};

type ButtonProps = Pick<
  MButtonProps,
  'children' | 'onClick' | 'style' | 'disabled' | 'id' | 'type' | 'form'
>;

export const Button = ({ onClick = _noop, ...props }: ButtonProps) => (
  <MButton
    {...props}
    variant="contained"
    size="medium"
    type={props.type}
    form={props.form}
    onClick={event => {
      onClick(event);
    }}
    style={props.style}
    disabled={props.disabled}
  >
    {props.children}
  </MButton>
);

type IconButtonProps = Pick<
  MIconButtonProps,
  'children' | 'onClick' | 'disabled'
>;

export const IconButton = ({
  onClick = _noop,
  disabled = false,
  ...props
}: IconButtonProps) => (
  <MIconButton
    {...props}
    onClick={event => {
      onClick(event);
    }}
    style={buttonStyle(disabled)}
    disabled={disabled}
  >
    {props.children}
  </MIconButton>
);
