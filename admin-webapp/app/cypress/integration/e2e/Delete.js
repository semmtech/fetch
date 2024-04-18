import AddTestData from '../../utils/bootstrap/AddTestData';
import DeleteTestData from '../../utils/bootstrap/DeleteTestData';
import { testConfig } from '../../utils';

describe('Admin UI Deleting', () => {
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

  describe('Delete for Configurations', () => {
    it('Check if the start route is configurations', () => {
      cy.getCookie(testConfig.sessionId).should('exist');
      cy.reload(true);
    });

    it('Check if the configs are there', () => {
      cy.checkValueText('PageHeaderTitle', 'Configurations');

      cy.checkExistence('Card_configurations_159753', true);
      cy.checkExistence('Card_configurations_12345', true);
      cy.checkExistence('Card_configurations_67890', true);

      cy.checkVisibility('CardThreeDots_159753', true);
      cy.checkVisibility('CardThreeDots_12345', true);
      cy.checkVisibility('CardThreeDots_67890', true);
    });

    it('Click on the action button for first card and pick delete', () => {
      cy.clickItem('CardThreeDots_12345');

      cy.checkVisibility('MenuItemEdit', true);
      cy.checkVisibility('MenuItemDelete', true);
      cy.checkVisibility('CardDelete', true);
      cy.checkVisibility('CardDeleteText', true);

      cy.clickItem('MenuItemDelete');
    });

    it('Check if the alert config appears and click yes', () => {
      cy.checkVisibility('AlertTitle', true);
      cy.checkVisibility('AlertDescription', true);

      cy.checkVisibility('AlertItem', true);
      cy.checkValueText('AlertItem', 'Cypress first mocked configuration');

      cy.checkVisibility('AlertNoButton', true);
      cy.checkVisibility('AlertYesButton', true);

      cy.clickItem('AlertYesButton');
    });

    it('Check if the card is gone', () => {
      cy.checkExistence('Card_configurations_12345', false);
    });

    it('Click on the action button for second card and pick delete', () => {
      cy.clickItem('CardThreeDots_67890');
      cy.clickItem('MenuItemDelete');
    });

    it('Check if the alert config appears and click no', () => {
      cy.checkVisibility('AlertTitle', true);
      cy.checkVisibility('AlertDescription', true);

      cy.checkVisibility('AlertItem', true);
      cy.checkValueText('AlertItem', 'Cypress second mocked configuration');

      cy.clickItem('AlertNoButton');
    });

    it('Check if the card is still there', () => {
      cy.checkExistence('Card_configurations_67890', true);
    });
  });

  describe('Delete for SPARQL Endpoints', () => {
    it('Go to endpoints', () => {
      cy.clickItem(testConfig.menuItems.endpoints);
    });

    it('Check if the endpoints are there', () => {
      cy.checkValueText('PageHeaderTitle', 'SPARQL Endpoints');

      cy.checkExistence('Card_sparqlendpoints_endpoint123', true);
      cy.checkExistence('Card_sparqlendpoints_endpoint456', true);
      cy.checkExistence('Card_sparqlendpoints_endpoint159753', true);
    });

    it('Click on the action button for first card and pick delete', () => {
      cy.clickItem('CardThreeDots_endpoint123');
      cy.clickItem('MenuItemDelete');
    });

    it('Check if the alert config appears and click yes', () => {
      cy.checkVisibility('AlertItem', true);
      cy.checkValueText('AlertItem', 'Endpoint 001');

      cy.clickItem('AlertYesButton');
    });

    it('Check if the card is gone', () => {
      cy.checkExistence('Card_sparqlendpoints_endpoint123', false);
    });

    it('Click on the action button for second card and pick delete', () => {
      cy.clickItem('CardThreeDots_endpoint456');
      cy.clickItem('MenuItemDelete');
    });

    it('Check if the alert config appears and click no', () => {
      cy.checkVisibility('AlertItem', true);
      cy.checkValueText('AlertItem', 'Endpoint 002');

      cy.clickItem('AlertNoButton');
    });

    it('Check if the card is still there', () => {
      cy.checkExistence('Card_sparqlendpoints_endpoint456', true);
    });
  });

  describe('Delete for SPARQL Queries', () => {
    it('Go to the queries', () => {
      cy.clickItem(testConfig.menuItems.queries);
    });

    it('Check if the queries are there', () => {
      cy.checkValueText('PageHeaderTitle', 'SPARQL Queries');

      cy.checkExistence('Card_sparqlqueries_12345', true);
      cy.checkExistence('Card_sparqlqueries_67890', true);
      cy.checkExistence('Card_sparqlqueries_123789', true);
    });

    it('Click on the action button for first card and pick delete', () => {
      cy.clickItem('CardThreeDots_12345');
      cy.clickItem('MenuItemDelete');
    });

    it('Check if the alert config appears and click yes', () => {
      cy.checkVisibility('AlertItem', true);
      cy.checkValueText('AlertItem', 'CYPRESS_001');

      cy.clickItem('AlertYesButton');
    });

    it('Check if the card is gone', () => {
      cy.checkExistence('Card_sparqlqueries_12345', false);
    });

    it('Click on the action button for second card and pick delete', () => {
      cy.clickItem('CardThreeDots_67890');
      cy.clickItem('MenuItemDelete');
    });

    it('Check if the alert config appears and click no', () => {
      cy.checkVisibility('AlertItem', true);
      cy.checkValueText('AlertItem', 'CYPRESS_002');

      cy.clickItem('AlertNoButton');
    });

    it('Check if the card is still there', () => {
      cy.checkExistence('Card_sparqlqueries_67890', true);
    });
  });

  describe('Delete for Environments and workspaces', () => {
    it('Go to the relatics page', () => {
      cy.clickItem(testConfig.menuItems.relatics);
    });

    it('Check if the environments and workspaces are there', () => {
      cy.checkExistence('Card_environments_123env', true);
      cy.checkExistence('Card_environments_456env', true);
      cy.checkExistence('Card_environments_789env', true);

      cy.checkExistence('Card_workspaces_relatics123', true);
      cy.checkExistence('Card_workspaces_relatics456', true);
    });

    it('Click on the action button for first env and pick delete', () => {
      cy.clickItem('CardThreeDots_123env');
      cy.clickItem('MenuItemDelete');
    });

    it('Check if the alert config appears and click yes', () => {
      cy.checkValueText('AlertItem', 'TEST env 001');
      cy.clickItem('AlertYesButton');
    });

    it('Check if the toast alert appears with the error message that the env cannot be deleted (because there is still a workspace linked)', () => {
      cy.get('.Toastify__toast--error').should(item => {
        expect(
          item
            .text()
            .includes(
              'The environment is still linked to workspaces. Please, delete these workspaces first.'
            )
        ).to.be.true;
      });
    });

    it('Check if the card is still there', () => {
      cy.checkExistence('Card_environments_123env', true);
    });

    it('Click on the action button of the linked workspace and pick delete', () => {
      cy.clickItem('CardThreeDots_relatics123');
      cy.clickItem('MenuItemDelete');
    });

    it('Check if the alert config appears and click yes', () => {
      cy.checkValueText('AlertDescription', 'Delete the selected workspace');
      cy.clickItem('AlertYesButton');
    });

    it('Check if the linked workspace is gone', () => {
      cy.checkExistence('Card_workspaces_relatics123', false);
    });

    it('Click on the action button for first env and pick delete', () => {
      cy.clickItem('CardThreeDots_123env');
      cy.clickItem('MenuItemDelete');
    });

    it('Check if the alert config appears and click yes', () => {
      cy.checkValueText('AlertItem', 'TEST env 001');
      cy.clickItem('AlertYesButton');
    });

    it('Check if the card is gone', () => {
      cy.checkExistence('Card_environments_123env', false);
    });
  });

  describe('Delete for json api"s', () => {
    it('Go to the Neanex tab', () => {
      cy.clickItem(testConfig.menuItems.neanex);
    });

    it('Check if the json api"s are there', () => {
      cy.checkValueText('PageHeaderTitle', "JSON API's");
      cy.checkExistence('Card_jsonapis_123jsonapi', true);
      cy.checkExistence('Card_jsonapis_456jsonapi', true);
    });

    it('Click on the action button for first card and pick delete', () => {
      cy.clickItem('CardThreeDots_123jsonapi');
      cy.clickItem('MenuItemDelete');
    });

    it('Check if the alert config appears and click yes', () => {
      cy.checkVisibility('AlertItem', true);
      cy.checkValueText('AlertItem', 'TEST json api 001');

      cy.clickItem('AlertYesButton');
    });

    it('Check if the card is gone', () => {
      cy.checkExistence('Card_jsonapis_123jsonapi', false);
    });
  });
});
