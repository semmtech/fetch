import AddTestData from '../../utils/bootstrap/AddTestData';
import DeleteTestData from '../../utils/bootstrap/DeleteTestData';
import { testConfig, autoCompleteFields } from '../../utils';

const fields = {
  configurations: [
    'isActive',
    'workspace',
    'jsonApi',
    'id',
    'name',
    'displayName',
    'description',
    'sparqlEndpoint',
    'startDate',
    'endDate',
    'targetType',
    "sparqlQuery"
  ],
  endpoints: ['id', 'name', 'url'],
  queries: ['id', 'name', 'description', 'query'],
  environments: ['id', 'name', 'serviceUrl', 'namespace', 'environmentId']
};

const subFields = {
  workspace: ['environmentId', 'workspaceId', 'name', 'id'],
  jsonApi: ['name', 'id', 'serviceUrl'],
  sparqlEndpoint: ['id', 'name', 'url'],
  sparqlQuery: ['name', 'description', 'type']
};

describe('Admin UI Filter', () => {
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

  describe('Filter dialog for Configurations data', () => {
    it('Check if the start route is configurations', () => {
      cy.getCookie(testConfig.sessionId).should('exist');
      cy.reload(true);
    });

    it('Check if the configs page is there and the configs', () => {
      cy.checkVisibility('AddFilterButton', true);
      cy.checkExistence('Card_configurations_12345', true);
      cy.checkExistence('Card_configurations_67890', true);
      cy.checkExistence('Card_configurations_159753', true);
    });

    it('Click on filter button', () => {
      cy.clickItem('AddFilterButton');
    });

    it('Check if filter dialog appears', () => {
      cy.checkVisibility('FilterTitle', true);
      cy.checkVisibility(autoCompleteFields.mainFilter, true, true);
      cy.checkVisibility('#value', true, true);
      cy.checkVisibility('FilterCancelButton', true);
      cy.checkVisibility('FilterAddButton', true);
    });

    it('Click on main filter id', () => {
      cy.clickItem(autoCompleteFields.mainFilter, true);
    });

    it('Check the available fields', () => {
      cy.checkSelectFields(fields.configurations);
    });

    it('Add filter for name', () => {
      cy.addFilter('name', 'FilterCancelButton', 'mocked');
    });

    it('Click on filter button', () => {
      cy.clickItem('AddFilterButton');
    });

    it('Check if fields are all clear in the filter dialog', () => {
      cy.checkValueText('#value', '', true);
      cy.checkValueText(autoCompleteFields.mainFilter, '', true);
    });

    it('Click on main filter id', () => {
      cy.clickItem(autoCompleteFields.mainFilter, true);
    });

    it('Add filter for name (with key enter)', () => {
      cy.clickItem('name');
      cy.typeText('#value', 'mocked {enter}');
    });

    it('Delete this filter', () => {
      cy.clickItem('RemoveFilterButton');
    });

    it('Click on filter button', () => {
      cy.clickItem('AddFilterButton');
    });

    it('Click on main filter id', () => {
      cy.clickItem(autoCompleteFields.mainFilter, true);
    });

    it('Add filter for name', () => {
      cy.addFilter('name', 'FilterAddButton', 'mocked');
    });

    it('Check if the remove filters button is there', () => {
      cy.checkExistence('RemoveFilterButton', true);
    });

    it('Click on filter button', () => {
      cy.clickItem('AddFilterButton');
    });

    it('Add filter for isActive', () => {
      cy.clickItem(autoCompleteFields.mainFilter, true);
      cy.clickItem('isActive');
      cy.clickItem(autoCompleteFields.filterValue, true);

      cy.checkSelectFields(['false', 'true']);

      cy.clickItem('true');
      cy.clickItem('FilterAddButton');
    });

    it('Click on filter button', () => {
      cy.clickItem('AddFilterButton');
    });

    it('check existance of fields for startDate', () => {
      cy.clickItem(autoCompleteFields.mainFilter, true);
      cy.clickItem('startDate');

      cy.checkExistence('FilterStart', true);
      cy.checkExistence('FilterEnd', true);
    });

    it('Select cancel in filter dialog', () => {
      cy.clickItem('FilterCancelButton');
    });

    it('Click on filter button', () => {
      cy.clickItem('AddFilterButton');
    });

    it('Check subFilters', () => {
      cy.clickItem(autoCompleteFields.mainFilter, true);
      cy.clickItem('sparqlEndpoint');
      cy.clickItem(autoCompleteFields.subFilter, true);
      cy.checkSelectFields(subFields.sparqlEndpoint);

      cy.clickItem(autoCompleteFields.mainFilter, true);
      cy.clickItem('jsonApi');
      cy.clickItem(autoCompleteFields.subFilter, true);
      cy.checkSelectFields(subFields.jsonApi, true);

      cy.clickItem(autoCompleteFields.mainFilter, true);
      cy.clickItem('workspace');
      cy.clickItem(autoCompleteFields.subFilter, true);
      cy.checkSelectFields(subFields.workspace, true);

      cy.clickItem(autoCompleteFields.mainFilter, true);
      cy.clickItem('sparqlQuery');
      cy.clickItem(autoCompleteFields.subFilter, true);
      cy.checkSelectFields(subFields.sparqlQuery, true);
    });

    it('Add filter with a subfilter (workspace)', () => {
      cy.addFilter('name', 'FilterAddButton', '001');
    });

    it('Check if the filtered configs are there', () => {
      cy.checkExistence('Card_configurations_12345', true);
    });

    it('Check if the overview of the filters is there and only active for configurations', () => {
      cy.checkExistence('name_0', true);
      cy.checkExistence('isActive_1', true);
      cy.checkExistence('workspace_2', true);

      cy.clickItem(testConfig.menuItems.endpoints);

      cy.checkExistence('name_0', false);
      cy.checkExistence('isActive_1', false);
      cy.checkExistence('workspace_2', false);

      cy.clickItem(testConfig.menuItems.configurations);
    });

    it('Remove a filter', () => {
      cy.clickItem('delete_2');
    });

    it('Check if the filtered configs are there', () => {
      cy.checkExistence('workspace_2', false);
      cy.checkExistence('Card_configurations_12345', true);
      cy.checkExistence('Card_configurations_67890', true);
    });

    it('Remove all filters', () => {
      cy.clickItem('RemoveFilterButton');
    });

    it('Check if the overview of the filters is empty', () => {
      cy.checkExistence('name_0', false);
      cy.checkExistence('isActive_1', false);
      cy.checkExistence('workspace_2', false);
    });
  });

  describe('Filter dialog for endpoints data', () => {
    it('Click on menu list endpoints', () => {
      cy.clickItem(testConfig.menuItems.endpoints);
    });

    it('Check if the endpoints page is there', () => {
      cy.checkVisibility('AddFilterButton', true);

      cy.checkExistence('Card_sparqlendpoints_endpoint123', true);
      cy.checkExistence('Card_sparqlendpoints_endpoint456', true);
      cy.checkExistence('Card_sparqlendpoints_endpoint159753', true);
    });

    it('Click on filter button', () => {
      cy.clickItem('AddFilterButton');
    });

    it('Click on main filter id', () => {
      cy.clickItem(autoCompleteFields.mainFilter, true);
    });

    it('Check the available fields', () => {
      cy.checkSelectFields(fields.endpoints);
    });

    it('Add filter for id', () => {
      cy.addFilter('id', 'FilterAddButton', '456');
    });

    it('Check if the filtered endpoints are there', () => {
      cy.checkExistence('Card_sparqlendpoints_endpoint456', true);
    });

    it('Check if the overview of the filters is there', () => {
      cy.checkExistence('id_0', true);
    });

    it('Remove a filter', () => {
      cy.clickItem('delete_0');
    });

    it('Check if the overview of the filters is empty', () => {
      cy.checkExistence('id_0', false);
    });
  });

  describe('Filter dialog for queries data', () => {
    it('Click on menu list queries', () => {
      cy.clickItem(testConfig.menuItems.queries);
    });

    it('Check if the queries page is there', () => {
      cy.checkVisibility('AddFilterButton', true);

      cy.checkExistence('Card_sparqlqueries_abcde', true);
      cy.checkExistence('Card_sparqlqueries_fghij', true);
      cy.checkExistence('Card_sparqlqueries_12345', true);
      cy.checkExistence('Card_sparqlqueries_hallo1', true);
      cy.checkExistence('Card_sparqlqueries_hallo2', true);
      cy.checkExistence('Card_sparqlqueries_67890', true);
      cy.checkExistence('Card_sparqlqueries_123789', true);
      cy.checkExistence('Card_sparqlqueries_hallo5', true);
      cy.checkExistence('Card_sparqlqueries_hallo6', true);
    });

    it('Click on filter button', () => {
      cy.clickItem('AddFilterButton');
    });

    it('Click on main filter id', () => {
      cy.clickItem(autoCompleteFields.mainFilter, true);
    });

    it('Check the available fields', () => {
      cy.checkSelectFields(fields.queries);
    });

    it('Add filter for id', () => {
      cy.addFilter('id', 'FilterAddButton', '123');
    });

    it('Check if the filtered queries are there', () => {
      cy.checkExistence('Card_sparqlqueries_12345', true);
      cy.checkExistence('Card_sparqlqueries_123789', true);
    });

    it('Check if the overview of the filters is there', () => {
      cy.checkExistence('id_0', true);
    });

    it('Remove all filters', () => {
      cy.clickItem('RemoveFilterButton');
    });

    it('Check if the overview of the filters is empty', () => {
      cy.checkExistence('id_0', false);
    });
  });

  describe('Filter dialog for environments data', () => {
    it('Click on menu list Relatics', () => {
      cy.clickItem(testConfig.menuItems.relatics);
    });

    it('Check if the relatics page is there', () => {
      cy.checkExistence('Card_environments_123env', true);
      cy.checkExistence('Card_environments_456env', true);
      cy.checkExistence('Card_environments_789env', true);
    });

    it('Click on filter button', () => {
      cy.clickFirstItem('AddFilterButton');
    });

    it('Click on main filter id', () => {
      cy.clickItem(autoCompleteFields.mainFilter, true);
    });

    it('Check the available fields', () => {
      cy.checkSelectFields(fields.environments);
    });

    it('Add filter for name', () => {
      cy.addFilter('name', 'FilterAddButton', '001');
    });

    it('Check if the filtered environment is there', () => {
      cy.checkExistence('Card_environments_123env', true);
    });

    it('Check if the overview of the filters is there', () => {
      cy.checkExistence('name_0', true);
    });

    it('Remove all filters', () => {
      cy.clickFirstItem('RemoveFilterButton');
    });

    it('Check if the overview of the filters is empty', () => {
      cy.checkExistence('name_0', false);
    });
  });

  describe('Filter dialog for workspaces data', () => {
    it('Check if the relatics page is there', () => {
      cy.checkExistence('Card_workspaces_relatics123', true);
      cy.checkExistence('Card_workspaces_relatics456', true);
    });

    it('Click on filter button', () => {
      cy.clickAnyItem('AddFilterButton', 1);
    });

    it('Click on main filter id', () => {
      cy.clickItem(autoCompleteFields.mainFilter, true);
    });

    it('Check the available fields', () => {
      cy.checkSelectFields(subFields.workspace);
    });

    it('Add filter for name', () => {
      cy.addFilter('name', 'FilterAddButton', '001');
    });

    it('Check if the filtered environment is there', () => {
      cy.checkExistence('Card_workspaces_relatics123', true);
    });

    it('Check if the overview of the filters is there', () => {
      cy.checkExistence('name_0', true);
    });

    it('Remove all filters', () => {
      cy.clickFirstItem('RemoveFilterButton');
    });

    it('Check if the overview of the filters is empty', () => {
      cy.checkExistence('name_0', false);
    });
  });

  describe('Filter dialog for data of the json api"s', () => {
    it('Click on menu list Neanex', () => {
      cy.clickItem(testConfig.menuItems.neanex);
    });

    it('Check if the jsonApi"s are there', () => {
      cy.checkExistence('Card_jsonapis_123jsonapi', true);
      cy.checkExistence('Card_jsonapis_456jsonapi', true);
    });

    it('Click on filter button', () => {
      cy.clickItem('AddFilterButton');
    });

    it('Click on main filter id', () => {
      cy.clickItem(autoCompleteFields.mainFilter, true);
    });

    it('Check the available fields', () => {
      cy.checkSelectFields(subFields.jsonApi, false);
    });

    it('Add filter for name', () => {
      cy.addFilter('name', 'FilterAddButton', '002');
    });

    it('Check if the filtered json api is there', () => {
      cy.checkExistence('Card_jsonapis_456jsonapi', true);
    });

    it('Check if the overview of the filters is there', () => {
      cy.checkExistence('name_0', true);
    });

    it('Remove all filters', () => {
      cy.clickFirstItem('RemoveFilterButton');
    });

    it('Check if the overview of the filters is empty', () => {
      cy.checkExistence('name_0', false);
    });
  });
});
