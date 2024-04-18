import React, { setGlobal } from 'reactn';
import ReactDOM from 'react-dom';
import { ThemeProvider } from 'styled-components';
import CssBaseline from '@material-ui/core/CssBaseline';
import './reducers';

import App from './components/App.component';
import { colors } from './utils/colors';
import { State } from 'reactn/default';

// global initial state
export const defaultInitialState: State = {
  columns: [],
  error: { name: '', message: '' },
  selectedIds: [],
  loadingRoots: true,
  configurationId: '',
  data: {},
  search: '',
  importSteps: [],
  loading: false,
  newSorted: [],
  parameters: [],
  enablePagination: true,
  title: '',
  subtitle:'',
  foldedColumn: { id: undefined, folded: false },
  expandedRows: {},
  defaultColumnWidths: [],
  filters: {
    areFiltersActive: false,
    rootFilters: {},
    loadingPicklist: false,
    picklist: [],
    isFilterDialogOpen: false,
    hasEmptyRootsFilters: false
  }
};

const theme = {
  colors
};

type ThemeInterface = typeof theme;

// tslint:disable:interface-name
declare module 'styled-components' {
  interface DefaultTheme extends ThemeInterface {}
}

// set the global default state of the application
setGlobal(defaultInitialState);

ReactDOM.render(
  <ThemeProvider theme={theme}>
    <>
      <CssBaseline />
      <App />
    </>
  </ThemeProvider>,
  document.getElementById('root')
);
