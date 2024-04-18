import React from 'reactn';
import styled from 'styled-components';
import { Menu as MMenu, MenuProps as MMenuProps } from '@material-ui/core';
import MenuItem from '@material-ui/core/MenuItem';

interface CustomProps {
  menuItems: { title: string; action(): void }[];
  closeMenu(): void;
}
type MenuProps = Pick<MMenuProps, 'anchorEl' | 'id'> & CustomProps;

export default ({ id, anchorEl, closeMenu, menuItems }: MenuProps) => (
  <MMenu
    id={id}
    anchorEl={anchorEl}
    disableAutoFocusItem={true}
    open={Boolean(anchorEl)}
    onClose={() => {
      closeMenu();
    }}
  >
    {menuItems.map(({ title, action }, index) => (
      <MenuItem
        key={title}
        id={`MenuItem_${title}`}
        onClick={e => {
          e.stopPropagation();
          closeMenu();
          action();
        }}
        {...(index !== menuItems.length - 1 && {
          style: {
            borderBottom: '1px solid rgb(0, 0, 0, 0.10)'
          }
        })}
      >
        <MenuItemText data-testid={title}>{title}</MenuItemText>
      </MenuItem>
    ))}
  </MMenu>
);

const MenuItemText = styled.span`
  margin-left: 10px;
`;
