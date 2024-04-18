import React, { useGlobal } from 'reactn';
import styled from 'styled-components';
import Menu from '@material-ui/core/Menu';
import MenuItem from '@material-ui/core/MenuItem';
import DeleteIcon from '@material-ui/icons/Delete';
import EditIcon from '@material-ui/icons/Edit';
import FileCopyIcon from '@material-ui/icons/FileCopy';
import Relatics from '../../../img/Relatics.png';
import Neanex from '../../../img/logoNeanex.png';

import { iconColor } from '../../../utils/styles';
import { Menu as MenuTypes } from '../../../types';
import { configTypes } from '../../../utils/configTypes';

export default ({
  id,
  anchor,
  closeMenu,
  item,
  type,
  editItem,
  cloneItem,
  deleteItem,
  relaticsItem,
  jsonAPIItem
}: MenuTypes) => {
  const [, setEditItem] = useGlobal('editItem');
  const [, setCloneItem] = useGlobal('cloneItem');
  const [, setDeleteItem] = useGlobal('removeItem');
  const [, setCreateItem] = useGlobal('createItem');

  return (
    <Menu
      id={id}
      anchorEl={anchor}
      disableAutoFocusItem={true}
      open={Boolean(anchor)}
      onClose={() => closeMenu()}
    >
      {editItem && (
        <MenuItem
          onClick={() => {
            setEditItem({
              isEditing: true,
              item,
              type
            });
            closeMenu();
          }}
          style={iconColor}
          data-testid="MenuItemEdit"
        >
          <EditIcon data-testid="CardEdit" />
          <MenuItemText data-testid="CardEditText">Edit</MenuItemText>
        </MenuItem>
      )}
      {cloneItem && (
        <MenuItem
          onClick={() => {
            setCloneItem({
              isCloning: true,
              item,
              type
            });
            closeMenu();
          }}
          style={iconColor}
          data-testid="MenuItemEdit"
        >
          <FileCopyIcon data-testid="CardClone" />
          <MenuItemText data-testid="CardCloneText">Clone</MenuItemText>
        </MenuItem>
      )}
      {deleteItem && (
        <MenuItem
          onClick={() => {
            setDeleteItem({
              isDeleting: true,
              item,
              type
            });
            closeMenu();
          }}
          style={iconColor}
          data-testid="MenuItemDelete"
        >
          <DeleteIcon data-testid="CardDelete" />
          <MenuItemText data-testid="CardDeleteText">Delete</MenuItemText>
        </MenuItem>
      )}
      {relaticsItem && (
        <MenuItem
          onClick={() => {
            setCreateItem({
              isCreating: true,
              item,
              type,
              configType: configTypes.relatics
            });
            closeMenu();
          }}
          style={iconColor}
          data-testid="MenuItemRelatics"
        >
          <img alt="Relatics logo" src={Relatics} height="20" width="20" />
          <MenuItemText data-testid="TypeRelaticsText">Relatics</MenuItemText>
        </MenuItem>
      )}
      {jsonAPIItem && (
        <MenuItem
          onClick={() => {
            setCreateItem({
              isCreating: true,
              item,
              type,
              configType: configTypes.jsonAPI
            });
            closeMenu();
          }}
          style={iconColor}
          data-testid="MenuItemJsonAPI"
        >
          <img alt="Neanex logo" src={Neanex} height="20" width="20" />
          <MenuItemText data-testid="TypeJSONAPIText">JSON API</MenuItemText>
        </MenuItem>
      )}
    </Menu>
  );
};

const MenuItemText = styled.span`
  margin-left: 10px;
`;
