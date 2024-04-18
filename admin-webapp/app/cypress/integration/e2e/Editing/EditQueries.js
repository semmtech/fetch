import { autoCompleteFields, testConfig } from '../../../utils';
import AddTestData from '../../../utils/bootstrap/AddTestData';
import DeleteTestData from '../../../utils/bootstrap/DeleteTestData';
import _sample from 'lodash/sample';

describe('Admin UI Editing Queries', () => {
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

  describe('Edit for Queries (filterFields too)', () => {
    const queryTypes = ['children', 'filter', 'import'];
    const query = {
      id: '12345',
      chosenEndpoint: 'endpoint456',
      name: 'CYPRESS_001',
      updatedName: 'CYPRESS_updated_001',
      type: 'roots',
      description: 'Hallo this is cypress testing',
      query:
        'PREFIX rdf: <http://www.w3.org/1999/02/22-rdf-syntax-ns#> \n PREFIX rdfs: <http://www.w3.org/2000/01/rdf-schema#> \n \nSELECT * WHERE {\n ?sub ?pred ?obj .\n} \n LIMIT 3 \n'
    };

    it('Check if cookie exists -> to make sure you are logged in', () => {
      cy.getCookie(testConfig.sessionId).should('exist');
    });

    it('Click on menu list queries', () => {
      cy.clickItem(testConfig.menuItems.queries);
    });

    it('Check if the query is there', () => {
      cy.checkExistence(`Card_sparqlqueries_${query.id}`, true);
    });

    it('Click on the action button and pick edit', () => {
      cy.clickItem(`CardThreeDots_${query.id}`);
      cy.clickItem('MenuItemEdit');
    });

    it('Edit the query', () => {
      cy.checkValueText('PageHeaderTitle', 'Details Query');
      cy.typeText('#queryName', query.updatedName);
      cy.typeText('#queryDescription', query.description);

      cy.clickItem(autoCompleteFields.endpoint, true);
      cy.clickItem(query.chosenEndpoint);

      cy.typeYasguiEditor('textarea', 2, '{control}a{del}').type(query.query);
      cy.typeYasguiEditor('textarea', 2, '{control}{enter}');

      for (var i = 1; i < 4; i++) {
        cy.get(`.rt-tbody > :nth-child(${i}) > .rt-tr`).should('exist');
      }

      cy.clickItem(autoCompleteFields.type, true);
      cy.clickItem(_sample(queryTypes));

      cy.checkExistence('FilterFieldsHeader', false);
      cy.checkExistence('Add-NewFilter', false);

      cy.clickItem('#queryName', true);
      cy.clickItem(autoCompleteFields.type, true);
      cy.clickItem(query.type);

      cy.clickItem('Add-NewFilter');
      cy.clickItem('Add-NewFilter');
      cy.checkVisibility('DeleteFilter-0', true);
      cy.checkVisibility('DeleteFilter-1', true);

      cy.typeText('[data-testid=DND-QueryFilter0] #variable', 'sub');
      cy.typeText('[data-testid=DND-QueryFilter0] #label', 'Name');
      cy.get(autoCompleteFields.query).should('be.disabled');

      cy.clickItem(
        `[data-testid=DND-QueryFilter1] ${autoCompleteFields.type}`,
        true
      );
      cy.clickItem('#Autocomplete_Type-option-1', true);
      cy.typeText('[data-testid=DND-QueryFilter1] #variable', 'obj');
      cy.typeText('[data-testid=DND-QueryFilter1] #label', 'Object');
      cy.clickItem(
        `[data-testid=DND-QueryFilter1] ${autoCompleteFields.query}`,
        true
      );
      cy.clickItem('001');

      cy.clickItem('FinishAddButton');
    });

    it('Check if edit dialog appears', () => {
      cy.checkValueText(
        'AlertDescription',
        `Update the following query: ${query.name} -> ${query.updatedName}`
      );
    });

    it('Choose Yes', () => {
      cy.clickItem('AlertYesButton');
      cy.wait(2000);
      cy.checkExistence(`Card_sparqlqueries_${query.id}`, true);
      cy.checkValueText(`CardTitle_${query.id}`, query.updatedName);
    });

    it('Click on the action button and pick edit again', () => {
      cy.clickItem(`CardThreeDots_${query.id}`);
      cy.clickItem('MenuItemEdit');
    });

    it('Check the updated values in the edit page', () => {
      cy.checkItemValue('#queryName', 'value', query.updatedName);
      cy.checkItemValue('#queryDescription', 'value', query.description);
      cy.checkExistence('TestingZone', true);
      cy.checkItemValue(autoCompleteFields.type, 'value', 'roots');
      cy.checkItemValue(
        '[data-testid=DND-QueryFilter0] #variable',
        'value',
        'sub'
      );
      cy.checkItemValue(
        '[data-testid=DND-QueryFilter0] #label',
        'value',
        'Name'
      );
      cy.checkItemValue(
        `[data-testid=DND-QueryFilter0] ${autoCompleteFields.type}`,
        'value',
        'Text'
      );

      cy.checkItemValue(
        '[data-testid=DND-QueryFilter1] #variable',
        'value',
        'obj'
      );
      cy.checkItemValue(
        '[data-testid=DND-QueryFilter1] #label',
        'value',
        'Object'
      );
      cy.checkItemValue(
        `[data-testid=DND-QueryFilter1] ${autoCompleteFields.type}`,
        'value',
        'SPARQL Picklist'
      );
      cy.checkItemValue(
        `[data-testid=DND-QueryFilter1] ${autoCompleteFields.query}`,
        'value',
        'CYPRESS_004'
      );
      cy.clickItem('CancelButton');
    });
  });
});
