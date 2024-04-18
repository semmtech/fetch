import React, { useGlobal, useDispatch } from 'reactn';
import styled from 'styled-components';
import { FaAlignJustify, FaPowerOff } from 'react-icons/fa';
import AppBar from '@material-ui/core/AppBar';
import Toolbar from '@material-ui/core/Toolbar';
import Typography from '@material-ui/core/Typography';
import IconButton from '@material-ui/core/IconButton';
import Drawer from '@material-ui/core/Drawer';
import CssBaseline from '@material-ui/core/CssBaseline';
import HelpOutline from '@material-ui/icons/HelpOutline';

import SideList from './SideList.component';
import { appBarStyle } from '../../utils/styles';

const drawerWidth = '250px';

export default () => {
  const [showMenu, setShowMenu] = useGlobal('showMenu');
  const logout = useDispatch('logout');

  return (
    <Container>
      <Wrapper>
        <CssBaseline />
        <AppBar position="fixed" style={appBarStyle(showMenu)}>
          <Toolbar variant="dense">
            <IconButton
              color="inherit"
              aria-label="Menu"
              onClick={() => setShowMenu(!showMenu)}
              style={{ marginLeft: '-21px' }}
            >
              <FaAlignJustify data-testid="menuButton" />
            </IconButton>
            <Typography
              variant="h6"
              color="inherit"
              noWrap={true}
              style={{ flexGrow: 1000, marginLeft: '10px' }}
              data-testid="applicationTitle"
            >
              Laces Fetch
            </Typography>
            <IconButton
              color="inherit"
              aria-label="Help"
              onClick={() => {
                window.open(
                  'http://docs.laces.tech/fetch/index.html',
                  '_blank'
                );
              }}
              style={{ flexGrow: 1, marginRight: '10px' }}
            >
              <HelpOutline style={{ fontSize: '1.75rem' }} />
            </IconButton>
            <IconButton
              color="inherit"
              aria-label="Menu"
              onClick={() => logout('', '')}
              style={{ flexGrow: 1, marginRight: '-10px' }}
            >
              <FaPowerOff data-testid="logoutButton" />
            </IconButton>
          </Toolbar>
        </AppBar>
        <Drawer variant="permanent" open={showMenu}>
          <DrawerAnimation
            role="button"
            showMenu={showMenu}
            data-testid="menuList"
          >
            <SideList />
          </DrawerAnimation>
        </Drawer>
      </Wrapper>
    </Container>
  );
};

const Container = styled.div`
  width: 100%;
  font-size: 20px;
  display: flex;
`;

const Wrapper = styled.div`
  width: 100%;
`;

const DrawerAnimation = styled.div<{ showMenu: boolean }>`
  flex-shrink: 0;
  white-space: nowrap;
  width: ${p => (p.showMenu ? drawerWidth : '50px')};
  padding-top: 48px;
  overflow-x: hidden;
  transition-property: width;
  transition-duration: ${p => (p.showMenu ? '195ms, 195ms' : '225ms, 225ms')};
  transition-timing-function: cubic-bezier(0.4, 0, 0.6, 1),
    cubic-bezier(0.4, 0, 0.6, 1);
  transition-delay: 0ms, 0ms;
`;
