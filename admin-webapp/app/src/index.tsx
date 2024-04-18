import React, { setGlobal } from 'reactn';
import ReactDOM from 'react-dom';
import { HashRouter } from 'react-router-dom';
import { ThemeProvider, createGlobalStyle } from 'styled-components';
import 'yasgui-yasqe/dist/yasqe.min.css';

import { colors } from './utils/colors';
import './reducers';
import App from './components/App.component';
import {
  Configuration,
  Filter,
  Query,
  Endpoint,
  WorkspaceOrJsonApi,
  TargetSystemOrEndpoint,
  Environment
} from './types';

const configurations: Configuration[] = [];
const sparqlqueries: Query[] = [];
const sparqlendpoints: Endpoint[] = [];
const workspaces: WorkspaceOrJsonApi[] = [];
const jsonapis: WorkspaceOrJsonApi[] = [];
const targetsystems: TargetSystemOrEndpoint[] = [];
const environments: Environment[] = [];
const globalFilters: {
  configurations: Filter[];
  sparqlendpoints: Filter[];
  sparqlqueries: Filter[];
  workspaces: Filter[];
  jsonapis: Filter[];
  targetsystems: Filter[];
  environments: Filter[];
} = {
  configurations: [],
  sparqlendpoints: [],
  sparqlqueries: [],
  workspaces: [],
  targetsystems: [],
  environments: [],
  jsonapis: []
};

// global initial state
export const initialState = {
  currentQueryEditor: '',
  showMenu: false,
  openFilterDialog: false,
  configurations,
  globalFilters,
  errorDelete: { name: '', message: '' },
  errorClone: { name: '', message: '' },
  sparqlendpoints,
  sparqlqueries,
  workspaces,
  targetsystems,
  environments,
  jsonapis,
  editItem: { isEditing: false, item: {}, type: '' },
  cloneItem: { isCloning: false, item: {}, type: '' },
  removeItem: { isDeleting: false, item: {}, type: '' },
  createItem: {
    isCreating: false,
    item: {},
    type: '',
    configType: undefined
  }
};

const theme = {
  colors
};
type ThemeInterface = typeof theme;

const GlobalStyle = createGlobalStyle`
.yasqe .CodeMirror-fullscreen {
  top: 47px;
  left: 50px;
}
`;

// tslint:disable:interface-name
declare module 'styled-components' {
  interface DefaultTheme extends ThemeInterface {}
}

// set the global default state of the application
setGlobal(initialState);

ReactDOM.render(
  <HashRouter>
    <ThemeProvider theme={theme}>
      <>
        <GlobalStyle />
        <App />
      </>
    </ThemeProvider>
  </HashRouter>,
  document.getElementById('root')
);
