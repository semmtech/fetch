import { data, config, options } from '../../utils';

const menuItems = [
  '"Expand all"',
  '"Select all"',
  '"Unselect all"',
  '"Import all"'
];
const menuButton = '2_MenuButton';

describe('Add-on UI Expanding all children', () => {
  beforeEach(() => {
    cy.server();
    cy.setRoutes({ configId: data.configurationId });
  });

  it('Visit the add-on', () => {
    cy.VisitAddon({ configId: data.configurationId });
  });

  it('Open cell menu', () => {
    cy.clickItem({ testId: menuButton });

    menuItems.forEach(item => {
      cy.checkExisting({
        testId: item,
        exist: true
      });
    });
  });

  it('Catch bug', () => {
    // Bug: When you click anywhere beside the menu it expand and then you see the loading icon
    cy.get('body').click('center');

    cy.checkVisibility({
      testId: 'LoaderIcon',
      visible: false
    });
    cy.checkExisting({
      testId: menuItems[0],
      exist: false
    });
  });

  it('Expand all children', () => {
    cy.MockNestedChildren({
      configId: data.configurationId,
      menuItem: menuItems[0],
      menuButton
    });

    cy.CheckAllChildrenExist({
      row: '2',
      amount: 11
    });
  });
});

describe('Add-on UI Selecting all children', () => {
  beforeEach(() => {
    cy.server();
    cy.setRoutes({
      configId: data.configurationId
    });
  });

  it('Visit the add-on', () => {
    cy.VisitAddon({
      configId: data.configurationId
    });
  });

  it('Expand all children', () => {
    cy.MockNestedChildren({
      configId: data.configurationId,
      menuItem: menuItems[0],
      menuButton
    });

    cy.CheckAllChildrenExist({
      row: '2',
      amount: 11
    });
  });

  it('Select all children', () => {
    // Bug: Selecting all children was deleting the previous selected rows
    cy.clickItem({
      testId: config.tableRow + data.root.uri
    });
    cy.checkSelectedColor({
      testId: config.tableRow + data.root.uri,
      check: true
    });

    cy.MockNestedChildren({
      configId: data.configurationId,
      menuItem: menuItems[1],
      menuButton
    });

    cy.clickItem({
      testId: config.down + 'DienstGebouw'
    });

    cy.GetSelectedAmountOfChildren({
      testId: 'AmountChildren_DienstGebouw',
      selecting: true
    });

    cy.checkSelectedColor({
      testId: config.tableRow + data.root.uri,
      check: true
    });
  });

  it('Unselect all children', () => {
    cy.clickItem({
      testId: menuButton
    });
    cy.clickItem({
      testId: menuItems[2]
    });

    cy.GetSelectedAmountOfChildren({
      testId: 'AmountChildren_DienstGebouw',
      selecting: false
    });

    // Make sure this row is still selected after unselecting all of a different row
    cy.checkSelectedColor({
      testId: config.tableRow + data.root.uri,
      check: true
    });
  });

  it('Catch bug', () => {
    // Bug: amount of children selected was not visible when you select everything without expanding first
    cy.reload(true);

    cy.MockNestedChildren({
      configId: data.configurationId,
      menuItem: menuItems[1],
      menuButton
    });

    cy.checkSelectedColor({ testId: 'TableRow_DienstGebouw', check: true });

    cy.GetSelectedAmountOfChildren({
      testId: 'AmountChildren_DienstGebouw',
      selecting: true
    });
  });

  it('Catch failed fetch call -> Show alert', () => {
    cy.reload(true);

    cy.route(
      options({
        url: `/api/visualization/children?configurationId=${data.configurationId}`,
        response: '',
        status: 500
      })
    ).as('getChildren');

    cy.clickItem({ testId: menuButton });
    cy.clickItem({ testId: menuItems[1] });

    cy.wait('@getChildren');

    cy.checkIncludes({
      testId: config.toastError,
      selectors: ['try again', 'children']
    });
  });
});

describe('Add-on UI Importing all children', () => {
  beforeEach(() => {
    cy.server();
  });

  it('Visit the add-on', () => {
    cy.VisitAddon({ configId: data.configurationId });
  });

  it('Import all children', () => {
    cy.route(
      options({
        url: `/api/visualization/roots?configurationId=${data.configurationId}`,
        response: 'fixture:rootsColumnsFilters'
      })
    ).as('getRoots');

    cy.route(
      options({
        url: `/api/import/?configurationId=${data.configurationId}`,
        response: 'fixture:importsAllDescendants'
      })
    ).as('imports');

    cy.MockNestedChildren({
      configId: data.configurationId,
      menuItem: menuItems[3],
      menuButton
    });

    cy.checkSelectedColor({
      testId: config.tableRow + 'DienstGebouw',
      check: true
    });

    cy.GetSelectedAmountOfChildren({
      testId: 'AmountChildren_DienstGebouw',
      selecting: true
    });

    cy.checkIncludes({
      testId: config.toastSuccess,
      selectors: ['Import definition', 'Total rows imported: more than 10']
    });
    cy.checkIncludes({
      testId: config.toastSuccess,
      selectors: ['Import hierarchy', 'Total rows imported: more than 10']
    });
  });
});
