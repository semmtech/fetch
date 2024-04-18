import { testConfig } from '../../../utils';
import AddTestData from '../../../utils/bootstrap/AddTestData';
import DeleteTestData from '../../../utils/bootstrap/DeleteTestData';
import { autoCompleteFields } from '../../../utils';

const config = {
  firstId: '159753',
  secondId: '01230'
};

describe('Admin UI Update a query and check if the columns of the configurations have changed', () => {
  const rootsQueryId = 'hallo5';
  const rootsQuery =
    'SELECT * WHERE {\n ?label ?uri ?hasChildren .\n }\n LIMIT 10';
  const visibleColumns = ['isImported', 'label', 'uri', 'uuid', 'hasChildren'];
  const updatedVisibleColumns = ['label', 'uri', 'hasChildren'];

  before(() => {
    cy.visit(testConfig.adminURL);
    cy.login(testConfig.userName, testConfig.password);

    DeleteTestData();
    AddTestData();
  });

  after(() => {
    cy.logout();
    cy.login(testConfig.userName, testConfig.password);

    DeleteTestData();
    cy.logout();
  });

  it('Check if the sessionId exists', () => {
    cy.getCookie(testConfig.sessionId).should('exist');
    cy.reload(true);
  });

  it('Check if the Relatics configs are there', () => {
    cy.checkExistence(`Card_configurations_${config.firstId}`, true);
    cy.checkExistence(`Card_configurations_${config.secondId}`, true);
  });

  // For config 1
  it('Click on the action button and pick edit', () => {
    cy.clickItem(`CardThreeDots_${config.firstId}`);
    cy.clickItem('MenuItemEdit');
  });

  it('Check if the columns are visible', () => {
    visibleColumns.map((columnName, index) => {
      cy.checkVisibility(`Column_${columnName}_${index}`, true);
    });

    cy.checkColumnItems({
      columnsLength: 5,
      visible: 2,
      notVisible: 3
    });

    cy.clickItem('CancelButton');
  });

  // For config 2
  it('Click on the action button and pick edit', () => {
    cy.clickItem(`CardThreeDots_${config.secondId}`);
    cy.clickItem('MenuItemEdit');
  });

  it('Check if the columns are visible', () => {
    visibleColumns.map((columnName, index) => {
      cy.checkVisibility(`Column_${columnName}_${index}`, true);
    });

    cy.checkColumnItems({
      columnsLength: 5,
      visible: 3,
      notVisible: 2
    });

    cy.clickItem('CancelButton');
  });

  // Updating the roots query
  it('Click on menu list queries', () => {
    cy.clickItem(testConfig.menuItems.queries);
  });

  it('Check if the query is there', () => {
    cy.checkExistence(`Card_sparqlqueries_${rootsQueryId}`, true);
  });

  it('Click on the action button and pick edit', () => {
    cy.clickItem(`CardThreeDots_${rootsQueryId}`);
    cy.clickItem('MenuItemEdit');
  });

  it('Edit the query', () => {
    cy.typeYasguiEditor('textarea', 2, '{control}a{del}');
    cy.typeYasguiEditor('textarea', 2, rootsQuery);

    cy.clickItem('FinishAddButton');
  });

  it('Choose Yes', () => {
    cy.clickItem('AlertYesButton');
    cy.wait(2000);
  });

  it('Click on menu list configurations', () => {
    cy.clickItem(testConfig.menuItems.configurations);
  });

  // For config 1
  it('Click on the action button and pick edit', () => {
    cy.clickItem(`CardThreeDots_${config.firstId}`);
    cy.clickItem('MenuItemEdit');
  });

  it('Check if the columns are updated', () => {
    updatedVisibleColumns.map((columnName, index) => {
      cy.checkVisibility(`Column_${columnName}_${index}`, true);
    });

    cy.checkColumnItems({
      columnsLength: 3,
      visible: 1,
      notVisible: 2
    });

    cy.clickItem('CancelButton');
  });

  // For config 2
  it('Click on the action button and pick edit', () => {
    cy.clickItem(`CardThreeDots_${config.secondId}`);
    cy.clickItem('MenuItemEdit');
  });

  it('Check if the columns are updated', () => {
    updatedVisibleColumns.map((columnName, index) => {
      cy.checkVisibility(`Column_${columnName}_${index}`, true);
    });

    cy.checkColumnItems({
      columnsLength: 3,
      visible: 1,
      notVisible: 2
    });

    cy.clickItem('CancelButton');
  });
});

describe('Admin UI Change roots query and see the columns updating with the right values', () => {
  const rootsQueryId = '12345';
  const visibleColumns = ['isImported', 'label', 'uri', 'uuid', 'hasChildren'];
  const updatedVisibleColumns = ['sub', 'pred', 'obj'];
  const updatedOrderVisibleColumns = ['sub', 'obj', 'pred'];

  before(() => {
    cy.visit(testConfig.adminURL);
    cy.login(testConfig.userName, testConfig.password);

    DeleteTestData();
    AddTestData();
  });

  after(() => {
    cy.logout();
    cy.login(testConfig.userName, testConfig.password);

    DeleteTestData();
    cy.logout();
  });

  it('Check if the sessionId exists', () => {
    cy.getCookie(testConfig.sessionId).should('exist');
    cy.reload(true);
  });

  it('Click on the action button and pick edit', () => {
    cy.clickItem(`CardThreeDots_${config.firstId}`);

    cy.clickItem('MenuItemEdit');
  });

  it('Check roots query id and columns', () => {
    cy.checkItemValue(
      autoCompleteFields.sparqlRootsQuery,
      'value',
      'TEST query'
    );

    visibleColumns.map((columnName, index) => {
      cy.checkVisibility(`Column_${columnName}_${index}`, true);
    });
  });

  it('Edit the configuration', () => {
    cy.clickItem(autoCompleteFields.sparqlRootsQuery, true);
    cy.clickItem(rootsQueryId);

    updatedVisibleColumns.map((columnName, index) => {
      cy.checkVisibility(`Column_${columnName}_${index}`, true);
    });

    cy.dragAndDrop({
      selector: 'Column_pred_1',
      current: {
        x: 527,
        y: 822
      },
      destination: {
        x: 1027,
        y: 822
      }
    });
  });

  it('Check the updated columns', () => {
    updatedOrderVisibleColumns.map((columnName, index) => {
      cy.checkVisibility(`Column_${columnName}_${index}`, true);
    });
  });

  it('Remove the roots query', () => {
    cy.clickItem(autoCompleteFields.sparqlRootsQuery, true);
    cy.get(autoCompleteFields.sparqlRootsQuery).clear();
  });

  it('Check the columns are gone', () => {
    updatedOrderVisibleColumns.map((columnName, index) => {
      cy.checkVisibility(`Column_${columnName}_${index}`, false);
    });
  });
});

describe('Admin UI Create Configuration and select a roots query -> see the columns updating', () => {
  const visibleColumns = ['sub', 'pred', 'obj'];

  before(() => {
    cy.visit(testConfig.adminURL);
    cy.login(testConfig.userName, testConfig.password);

    DeleteTestData();
    AddTestData();
  });

  after(() => {
    cy.logout();
    cy.login(testConfig.userName, testConfig.password);

    DeleteTestData();
    cy.logout();
  });

  it('Check if the sessionId exists', () => {
    cy.getCookie(testConfig.sessionId).should('exist');
  });

  it('Click on the create button', () => {
    cy.clickItem('PlusButton');
    cy.clickItem('MenuItemJsonAPI');
  });

  it('Check roots query id and columns', () => {
    cy.checkItemValue(autoCompleteFields.sparqlRootsQuery, 'value', '');

    visibleColumns.map((columnName, index) => {
      cy.checkVisibility(`Column_${columnName}_${index}`, false);
    });
  });

  it('Edit the configuration', () => {
    cy.clickItem(autoCompleteFields.sparqlRootsQuery, true);
    cy.clickItem('12345');
  });

  it('Check the updated columns', () => {
    visibleColumns.map((columnName, index) => {
      cy.checkVisibility(`Column_${columnName}_${index}`, true);
    });

    cy.checkColumnItems({
      columnsLength: 3,
      visible: 3,
      notVisible: 0
    });

    cy.clickItem('CancelButton');
  });
});
