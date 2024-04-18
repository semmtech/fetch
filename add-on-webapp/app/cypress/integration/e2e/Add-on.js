import { config, data, options } from '../../utils';

const collapse = 'collapse';
const isImported = 'isImported';

const rootRow = config.tableRow + data.root.uri;
const headerCollapse = config.tableHeader + collapse;
const headerImported = config.tableHeader + isImported;
const foldHeaderCollapse = config.foldColumn + collapse;
const foldHeaderImported = config.foldColumn + isImported;

describe('Add-on UI happy user flow', () => {
  beforeEach(() => {
    cy.server();
    cy.setRoutes({ configId: data.configurationId });
  });

  it('Visit the add-on', () => {
    cy.visit(`/add-on/index.html?configurationId=${data.configurationId}`);
    cy.wait('@getRoots');
  });

  it('Check if table header buttons and the display name are there', () => {
    cy.checkVisibility({ testId: config.importButton, visible: true });
    cy.checkDisabled({ testId: config.importButton, disabled: true });

    cy.checkVisibility({ testId: config.filterButton, visible: true });
    cy.checkDisabled({ testId: config.filterButton, disabled: true });

    cy.checkVisibility({
      testId: config.clearActiveFiltersButton,
      visible: true
    });
    cy.checkDisabled({
      testId: config.clearActiveFiltersButton,
      disabled: true
    });

    cy.checkValueText({
      testId: 'title',
      value: 'Physical objects'
    });
    cy.checkValueText({
      testId: 'subtitle',
      value: 'This is a subtitle'
    });
  });

  it('Check if the table is visible', () => {
    cy.checkVisibility({ testId: config.tableBody, visible: true });
    cy.checkExisting({ testId: `${headerCollapse}`, exist: true });
    cy.checkExisting({ testId: `${headerImported}`, exist: true });

    cy.checkTableCells({ testId: config.tableCell, visible: true, length: 20 });
  });

  it('Check if the fold icons are there', () => {
    cy.checkExisting({
      testId: foldHeaderCollapse,
      exist: true
    });
    cy.checkExisting({
      testId: foldHeaderImported,
      exist: true
    });
  });

  it('Check if the fold functionality works for both columns', () => {
    cy.clickItemForce({ testId: foldHeaderCollapse });
    cy.clickItemForce({ testId: foldHeaderImported });

    cy.CheckFirstCell({ testId: config.tableCell, equals: true });
    cy.CheckLastCell({ testId: config.tableCell, equals: true });

    cy.clickItemForce({ testId: foldHeaderCollapse });
    cy.clickItemForce({ testId: foldHeaderImported });

    cy.CheckFirstCell({ testId: config.tableCell, equals: false });
    cy.CheckLastCell({ testId: config.tableCell, equals: false });
  });

  it('Select the root, expand the same row and check if color is gone after deselect (bug edge case)', () => {
    cy.clickItem({ testId: rootRow });
    cy.checkSelectedColor({ testId: rootRow, check: true });
    cy.clickItem({ testId: config.right + data.root.uri });
    cy.wait('@getChildren');
    cy.clickItem({ testId: rootRow });
    cy.checkSelectedColor({ testId: rootRow, check: false });
  });

  it('Select the root and import it (fails)', () => {
    cy.route(
      options({
        url: `/api/import/?configurationId=${data.configurationId}`,
        response: {}
      })
    ).as('failedImport'); // needs to overwrite these route with the new response or else it is gonna take the old one

    cy.clickItem({ testId: rootRow });
    cy.clickItem({ testId: config.importButton });
    cy.wait('@failedImport');

    cy.checkIncludes({
      testId: config.toastError,
      selectors: 'The import could not be started, please try again.'
    });

    cy.clickItem({ testId: 'CloseToast' });
  });

  it('Select the root and import it', () => {
    cy.checkDisabled({ testId: config.importButton, disabled: false });
    cy.clickItem({ testId: config.importButton });
    cy.wait('@imports');

    cy.checkItemsLength({ testId: config.toast, length: 2 });

    cy.checkIncludes({
      testId: config.toastSuccess,
      selectors: ['Import definition', 'Total rows imported: 1']
    });
    cy.checkIncludes({
      testId: config.toastWarning,
      selectors: ['Import hierarchy', 'Warnings', 'Total rows imported: 1']
    });

    cy.wait(6000);

    cy.clickItem({ testId: 'CloseToast' });

    cy.checkVisibility({ testId: '"Import_Item 2"', visible: true });
  });

  it('Deselect the root', () => {
    cy.checkSelectedColor({ testId: rootRow, check: false });
    cy.checkDisabled({ testId: config.importButton, disabled: true });
  });

  it('Expand a root and check if its children are there', () => {
    cy.checkVisibility({ testId: config.down + data.root.uri, visible: true });

    data.children.map(({ uri, children }) => {
      cy.checkVisibility({ testId: config.tableRow + uri, visible: true });

      if (children)
        cy.checkVisibility({ testId: config.right + uri, visible: true });
      else cy.checkExisting({ testId: config.right + uri, exist: false });
    });
  });

  it('Select the root and 1 child and import them', () => {
    cy.route(
      options({
        url: `/api/import/?configurationId=${data.configurationId}`,
        response: 'fixture:importsLevel'
      })
    ).as('importsLevel');

    cy.clickItem({ testId: rootRow });
    data.children.map(({ uri }, index) => {
      if (index !== 1) cy.clickItem({ testId: config.tableRow + uri });
    });
    cy.clickItem({ testId: config.importButton });
    cy.wait('@importsLevel');

    cy.checkIncludes({
      testId: config.toastSuccess,
      selectors: ['Import definition', 'Total rows imported: 2']
    });
    cy.checkIncludes({
      testId: config.toastWarning,
      selectors: ['Import hierarchy', 'Warnings', 'Total rows imported: 2']
    });

    cy.wait(6000);

    cy.clickItem({ testId: 'CloseToast' });
  });

  it('Select the root and import it (failed -> error messages)', () => {
    cy.route(
      options({
        url: `/api/import/?configurationId=${data.configurationId}`,
        response: 'fixture:importsError'
      })
    ).as('importsError');

    cy.clickItem({ testId: rootRow });
    cy.clickItem({ testId: config.importButton });
    cy.wait('@importsError');

    cy.checkIncludes({
      testId: config.toastError,
      selectors: ['Import hierarchy', '']
    });
    cy.checkIncludes({
      testId: config.toastSuccess,
      selectors: ['Import definition']
    });

    cy.wait(5000);
  });

  it('Copy the error message', () => {
    cy.clickItem({
      testId: 'CopyToClipBoard'
    });

    cy.checkItemsLength({ testId: config.toast, length: 2 });

    cy.wait(6000);

    cy.clickItem({
      testId: 'CloseToast'
    });
  });

  it('Expand 1 level', () => {
    cy.route(
      options({
        url: `/api/visualization/children?configurationId=${data.configurationId}`,
        response: 'fixture:childrenLevel'
      })
    ).as('getChildrenLevel');

    cy.clickItem({ testId: config.right + data.children[0].uri });
    cy.wait('@getChildrenLevel');
  });

  it('Handle the select of data', () => {
    cy.clickItem({ testId: config.tableRow + data.lastChild.uri });
    cy.clickItem({ testId: config.down + data.children[0].uri });

    cy.checkValueText({
      testId: '"AmountChildren_Child 1"',
      value: '  (+1 children)'
    });

    cy.clickItem({ testId: config.tableRow + data.children[0].uri });
    cy.clickItem({ testId: config.tableRow + data.children[1].uri });
    cy.clickItem({ testId: config.down + data.root.uri });
    cy.clickItem({ testId: rootRow });

    cy.checkValueText({
      testId: '"AmountChildren_Item 2"',
      value: '  (+3 children)'
    });

    cy.clickItem({ testId: config.right + data.root.uri });
    cy.clickItem({ testId: config.right + data.children[0].uri });
    cy.clickItem({ testId: config.tableRow + data.lastChild.uri });

    cy.clickItem({ testId: config.down + data.root.uri });
    cy.checkValueText({
      testId: '"AmountChildren_Item 2"',
      value: '  (+2 children)'
    });
  });

  it('Handle pagination', () => {
    cy.checkExisting({ testId: 'PaginationFooter', exist: true });
    cy.checkIncludes({ testId: config.pageSizeSelect, selectors: 10 });

    cy.clickItem({ testId: config.pageSizeSelect, isDataTestIdPresent: false });

    data.pageSizeOptions.map(pageSize => {
      cy.checkVisibility({
        testId: `[data-value="${pageSize}"]`,
        visible: true,
        isDataTestIdPresent: false
      });
    });

    cy.clickItem({
      testId: data.pageSizeTestIds.fifty,
      isDataTestIdPresent: false
    });

    cy.checkTableCells({ testId: config.tableCell, visible: true, length: 30 });

    cy.clickItem({ testId: config.pageSizeSelect, isDataTestIdPresent: false });
    cy.clickItem({
      testId: data.pageSizeTestIds.five,
      isDataTestIdPresent: false
    });

    cy.checkTableCells({ testId: config.tableCell, visible: true, length: 10 });

    cy.checkPagination({ previous: true, next: false, content: '1-5 of 15' });
    cy.clickItem({ testId: config.lastPage });

    cy.checkPagination({ previous: false, next: true, content: '11-15 of 15' });
    cy.clickItem({ testId: config.previousPage });

    cy.checkPagination({ previous: false, next: false, content: '6-10 of 15' });
    cy.clickItem({ testId: config.firstPage });

    cy.checkPagination({ previous: true, next: false, content: '1-5 of 15' });
  });

  it('Handle sorting of data part 1', () => {
    cy.clickItem({ testId: config.pageSizeSelect, isDataTestIdPresent: false });
    cy.clickItem({
      testId: data.pageSizeTestIds.fifty,
      isDataTestIdPresent: false
    });

    data.orderedData.map((item, index) => {
      cy.checkMultipleValues({ index, value: item });
    });
    cy.clickItemForce({ testId: headerCollapse });
    cy.clickItemForce({ testId: headerCollapse });

    data.orderedData.reverse().map((item, index) => {
      cy.checkMultipleValues({ index, value: item });
    });
    cy.clickItemForce({ testId: headerCollapse });
  });

  it('Handle sorting of data part 2', () => {
    cy.route(
      options({
        url: `/api/visualization/roots?configurationId=${data.configurationId}`,
        response: 'fixture:rootsColumnsisImported'
      })
    ).as('getRootsImport');
    cy.reload(true);
    cy.wait('@getRootsImport');

    cy.clickItem({ testId: config.pageSizeSelect, isDataTestIdPresent: false });
    cy.clickItem({
      testId: data.pageSizeTestIds.fifty,
      isDataTestIdPresent: false
    });

    data.orderedDataByImport.map((item, index) => {
      cy.checkMultipleValues({ index, value: item });
    });
    cy.clickItemForce({ testId: headerImported });
    cy.clickItemForce({ testId: headerImported });

    data.orderedDataByImport.reverse().map((item, index) => {
      cy.checkMultipleValues({ index, value: item });
    });

    cy.clickItemForce({ testId: headerCollapse });

    data.orderedDataByImport.sort().map((item, index) => {
      cy.checkMultipleValues({ index, value: item });
    });
  });

  it('Check if pagination is not there', () => {
    cy.route(
      options({
        url: `/api/visualization/roots?configurationId=${data.configurationId}`,
        response: 'fixture:rootsColumnsNoPagination'
      })
    ).as('getRoots');

    cy.visit(`/add-on/index.html?configurationId=${data.configurationId}`);
    cy.wait('@getRoots');
    cy.checkExisting({ testId: 'PaginationFooter', exist: false });
    cy.checkVisibility({ testId: config.tableBody, visible: true });
    cy.checkExisting({ testId: `${headerCollapse}`, exist: true });
    cy.checkExisting({ testId: `${headerImported}`, exist: true });

    cy.checkTableCells({ testId: config.tableCell, visible: true, length: 30 });
  });

  it('Check if we got an error message instead of the table if we pass in a wrong url', () => {
    cy.visit(`${config.addonURL}?configurationId=12345`);
    cy.checkExisting({ testId: config.tableError, exist: true });
  });

  it('Check if we get an empty table with the message: No results found', () => {
    cy.route(
      options({
        url: `/api/visualization/roots?configurationId=${data.configurationId}`,
        response: 'fixture:rootsColumnsNoValues'
      })
    ).as('getRootsNoValues');

    cy.visit(`/add-on/index.html?configurationId=${data.configurationId}`);
    cy.wait('@getRootsNoValues');
    cy.checkExisting({ testId: 'PaginationFooter', exist: false });
    cy.checkVisibility({ testId: config.tableBody, visible: true });
    cy.checkExisting({ testId: `${headerCollapse}`, exist: true });
    cy.checkExisting({ testId: `${headerImported}`, exist: true });

    cy.checkTableCells({ testId: config.tableCell, visible: true, length: 2 });
    // 2 -> Because there are two columns, you need to take the sum of the cells of both columns in account

    cy.checkValueText({
      testId: 'title',
      value: 'Empty values'
    });
    cy.checkValueText({
      testId: 'subtitle',
      value: 'This is the subtitle'
    });
  });
});
