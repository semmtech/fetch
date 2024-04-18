import React from 'reactn';
import Button from '@material-ui/core/Button';
import Dialog from '@material-ui/core/Dialog';
import DialogActions from '@material-ui/core/DialogActions';
import DialogContent from '@material-ui/core/DialogContent';
import DialogContentText from '@material-ui/core/DialogContentText';
import styled from 'styled-components';

import { AlertDialog } from '../../../types';
import { iconColor } from '../../../utils/styles';

export default ({
  description,
  item,
  open,
  confirmationAction,
  dialogOnClose,
  cancelAction
}: AlertDialog) => (
  <Dialog open={open} data-testid="AlertDialog" onClose={() => dialogOnClose()}>
    <Title data-testid="AlertTitle">Are you sure?</Title>
    <DialogContent>
      <DialogContentText
        id="alert dialog description"
        data-testid="AlertDescription"
      >
        {description}
      </DialogContentText>
      {item && (
        <DialogContentText data-testid="AlertItem" id="alert dialog item">
          {item}
        </DialogContentText>
      )}
    </DialogContent>
    <DialogActions>
      <Button
        onClick={() => cancelAction()}
        style={iconColor}
        data-testid="AlertNoButton"
      >
        No
      </Button>
      <Button
        onClick={() => confirmationAction()}
        style={iconColor}
        data-testid="AlertYesButton"
      >
        Yes
      </Button>
    </DialogActions>
  </Dialog>
);

const Title = styled.span`
  flex: 0 0 auto;
  margin: 0;
  padding: 24px 24px 20px;
  color: ${p => p.theme.colors.black};
  font-weight: 600;
  font-size: 20px;
  font-family: Roboto, Helvetica, Arial, sans-serif;
`;
