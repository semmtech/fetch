export const config = {
  importButton: 'ImportButton',
  filterButton: 'FilterButton',
  clearActiveFiltersButton: 'ClearActiveFiltersButton',
  addFilterButton: 'AddFilterButton',
  tableError: 'TableError',
  tableBody: 'TableBody',
  tableCell: 'TableCell',
  tableRow: 'TableRow_',
  tableHeader: 'TableHeader_',
  toast: '.Toastify__toast',
  toastWarning: '.Toastify__toast--warning',
  toastSuccess: '.Toastify__toast--success',
  toastError: '.Toastify__toast--error',
  down: 'Down_',
  right: 'Right_',
  addonURL: '/add-on/index.html',
  totalText: 'TotalRoots',
  firstPage: 'GoFirstPage',
  previousPage: 'GoPreviousPage',
  lastPage: 'GoLastPage',
  nextPage: 'GoNextPage',
  expanderText: '#ExpandCell > .sc-htoDjs > #ExpanderText',
  pageSizeSelect: '.MuiSelect-root',
  foldColumn: 'Fold_'
};

export const data = {
  configurationId: 'abc',
  lastChild: {
    uri: 'Child10',
    children: true
  },
  root: {
    uri: 'Item2'
  },
  children: [
    {
      uri: 'Child1',
      children: true
    },
    {
      uri: 'Child2',
      children: false
    }
  ],
  orderedData: [
    'Bunker',
    'DienstGebouw',
    'Garage',
    'Gym',
    'Item 1',
    'Item 2',
    'Item 3',
    'Item 4',
    'Item 5',
    'Kantoor',
    'MeetingRoom',
    'Pool',
    'Toilet',
    'bathroom',
    'kelder'
  ],
  orderedDataByImport: [
    'Bathroom',
    'Bunker',
    'Gym',
    'Item 2',
    'Item 4',
    'Item 5',
    'Kantoor',
    'Kelder',
    'MeetingRoom',
    'Toilet',
    'DienstGebouw',
    'Garage',
    'Item 1',
    'Item 3',
    'Pool'
  ],
  pageSizeOptions: [5, 10, 20, 50, 100, 1000],
  pageSizeTestIds: {
    five: '[data-value="5"]',
    fifty: '[data-value="50"]'
  }
};

export const headers = {
  'Content-Type': 'application/json',
  Accept: 'application/json',
  Authorization: 'Basic YWRtaW46d2VsY29tZTE='
};

export const options = ({ url, response, status }) => ({
  method: 'POST',
  headers,
  url,
  response,
  status
});
