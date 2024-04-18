import React, { useGlobal } from 'reactn';
import { Route, Redirect, Switch } from 'react-router-dom';
import styled from 'styled-components';
import { MuiPickersUtilsProvider } from '@material-ui/pickers';
import DateFnsUtils from '@date-io/date-fns';
import { ToastContainer, toast } from 'react-toastify';
import 'react-toastify/dist/ReactToastify.css';

import Menu from '../components/ui/Menu.component';
import ConfigurationsTab from './ui/menuComponents/ConfigurationsTab.component';
import EndpointsTab from './ui/menuComponents/EndpointsTab.component';
import QueriesTab from './ui/menuComponents/QueriesTab.component';
import RelaticsTab from './ui/menuComponents/RelaticsTab.component';
import NeanexTab from './ui/menuComponents/NeanexTab.component';

export default () => {
  const [showMenu] = useGlobal('showMenu');

  const NoMatchPage = () => <Title>Not found</Title>;

  return (
    <MuiPickersUtilsProvider utils={DateFnsUtils}>
      <Wrapper>
        <ToastContainer
          hideProgressBar={true}
          autoClose={15000}
          pauseOnFocusLoss={false}
          position={toast.POSITION.TOP_LEFT}
          closeOnClick={false}
        />
        <Menu />
        <Container showMenu={showMenu}>
          <Switch>
            <Route
              exact={true}
              path="/"
              render={() => <Redirect to="/Configurations" />}
            />
            <Route path="/Configurations" component={ConfigurationsTab} />
            <Route path="/SPARQLendpoints" component={EndpointsTab} />
            <Route path="/SPARQLqueries" component={QueriesTab} />
            <Route path="/Relatics" component={RelaticsTab} />
            <Route path="/Neanex" component={NeanexTab} />
            <Route component={NoMatchPage} />
          </Switch>
        </Container>
      </Wrapper>
    </MuiPickersUtilsProvider>
  );
};

const Wrapper = styled.div`
  width: 100%;
  height: 100%;
  color: ${p => p.theme.colors.black};
`;

const Title = styled.h3`
  text-align: center;
  color: ${p => p.theme.colors.red};
  font-size: x-large;
`;

const Container = styled.div<{ showMenu: boolean }>`
  margin-left: ${p => (p.showMenu ? '250px' : '50px')};
  width: ${p => (p.showMenu ? 'calc(100% - 250px)' : 'calc(100% - 50px)')};
  padding-top: 50px;
  transition-property: width, margin;
  transition-duration: ${p => (p.showMenu ? '195ms, 195ms' : '225ms, 225ms')};
  transition-timing-function: cubic-bezier(0.4, 0, 0.6, 1),
    cubic-bezier(0.4, 0, 0.6, 1);
  transition-delay: 0ms, 0ms;
  height: 100vh;
`;
