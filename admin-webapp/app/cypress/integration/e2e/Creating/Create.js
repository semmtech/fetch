import { testConfig, autoCompleteFields } from '../../../utils';
import deleteCreatedData from '../../../utils/bootstrap/DeleteCreatedData';
import AddTestData from '../../../utils/bootstrap/AddTestData';
import DeleteTestData from '../../../utils/bootstrap/DeleteTestData';

let queryId = '';
let endpointId = '';
let environmentId = '';
let workspaceId = '';
let jsonApiId = '';

describe('Admin UI Creating', () => {
  before(() => {
    cy.visit(testConfig.adminURL);
    cy.login(testConfig.userName, testConfig.password);

    DeleteTestData();
    AddTestData();
  });

  after(() => {
    cy.logout();
    cy.login(testConfig.userName, testConfig.password);

    deleteCreatedData({
      endpointId,
      queryId,
      environmentId,
      workspaceId,
      jsonApiId
    });
    DeleteTestData();
    cy.logout();
  });

  describe('Create SPARQL Endpoint', () => {
    const endpoint = {
      name: '100 LACES Endpoint Cypress',
      url: `https://test3000.laces.tech/catalogs/test/repositories/buildings`,
      authenticationMethod: {
        type: 'BASIC',
        userName: 'Mike',
        password: 'Welcome1'
      }
    };

    it('Click on menu list endpoints', () => {
      cy.clickItem(testConfig.menuItems.endpoints);
      cy.checkURL(testConfig.urls.endpoints, true);
    });

    it('Click on the create button', () => {
      cy.clickItem('PlusButton');
    });

    it('Check if the create page for the endpoints appears', () => {
      cy.checkValueText('PageHeaderTitle', 'New Endpoint');
      cy.checkItemValue('#endpointName', 'value', '');
      cy.checkItemValue('#endpointURLName', 'value', '');
      cy.checkItemValue(autoCompleteFields.authType, 'value', 'NONE');
      cy.checkExistence('#applicationId', false, true);
      cy.checkExistence('#privateKey', false, true);
      cy.checkExistence('#userName', false, true);
      cy.checkExistence('#password', false, true);
    });

    it('Fill in the information', () => {
      cy.typeText('#endpointName', endpoint.name);
      cy.typeText('#endpointURLName', endpoint.url);

      cy.clickItem(autoCompleteFields.authType, true);
      cy.clickItem('BASIC');

      cy.typeText('#userName', endpoint.authenticationMethod.userName);
      cy.typeText('#password', endpoint.authenticationMethod.password);

      cy.clickItem('FinishAddButton');
    });

    it('Check if edit dialog appears', () => {
      cy.checkValueText('AlertDescription', 'Add the newly created endpoint');
    });

    it('Choose Yes', () => {
      cy.clickItem('AlertYesButton');
    });

    it('Check if card is added in the overview and get the id of it', () => {
      cy.getHeaderCard().then(item => {
        expect(item.text()).to.equals(endpoint.name);
        endpointId = item.attr('data-testid').split('_')[1];
      });
    });

    it('Log the id of the created endpoint', () => {
      cy.log('endpointId is', endpointId);
    });

    it('Check if card is added in the overview', () => {
      cy.checkExistence(`Card_sparqlendpoints_${endpointId}`, true);
    });

    it('Click on the action button and pick edit again', () => {
      cy.clickItem(`CardThreeDots_${endpointId}`);
      cy.clickItem('MenuItemEdit');
    });

    it('Check the updated values in the edit page', () => {
      cy.checkItemValue('#endpointName', 'value', endpoint.name);
      cy.checkItemValue('#endpointURLName', 'value', endpoint.url);
      cy.checkItemValue(
        autoCompleteFields.authType,
        'value',
        endpoint.authenticationMethod.type
      );
      cy.checkItemValue(
        '#userName',
        'value',
        endpoint.authenticationMethod.userName
      );
      cy.checkItemValue(
        '#password',
        'value',
        endpoint.authenticationMethod.password
      );
      cy.clickItem('CancelButton');
    });
  });

  describe('Create SPARQL Query', () => {
    const query = {
      chosenEndpoint: 'endpoint456',
      chosenEndpointTwo: 'endpoint123',
      name: '100 LACES Query Cypress',
      description: 'Query test description',
      query:
        'PREFIX rdf: <http://www.w3.org/1999/02/22-rdf-syntax-ns#> \n PREFIX rdfs: <http://www.w3.org/2000/01/rdf-schema#> \n \nSELECT * WHERE {\n ?sub ?pred ?obj .\n} \n LIMIT 5',
      importGraph:
        'http://www.laces-platform.tech/semmtech/ns/fetch/private/test/neanex/library/buildings',
      queryGraph:
        'PREFIX rdfs: <http://www.w3.org/2000/01/rdf-schema#> \n PREFIX skos: <http://www.w3.org/2004/02/skos/core#> \n PREFIX owl: <http://www.w3.org/2002/07/owl#> \n PREFIX rdf: <http://www.w3.org/1999/02/22-rdf-syntax-ns#> \n PREFIX asset: <http://dds.semmtech.nl/asset/> \n \nSELECT ?asset ?type ?label\n FROM <http://www.laces-platform.tech/semmtech/ns/fetch/private/test/neanex/library/devices>\n FROM <http://www.laces-platform.tech/semmtech/ns/fetch/private/test/neanex/library/buildings>\n {\n ?asset rdf:type / rdfs:subClassOf* asset:Asset ;\n rdfs:label ?label ;\n rdf:type ?type .\n  }\n'
    };

    it('Click on menu list queries', () => {
      cy.clickItem(testConfig.menuItems.queries);
      cy.checkURL(testConfig.urls.queries, true);
    });

    it('Click on the create button', () => {
      cy.clickItem('PlusButton');
    });

    it('Check if the create page for the queries appears', () => {
      cy.checkValueText('PageHeaderTitle', 'New Query');
      cy.checkItemValue('#queryName', 'value', '');
      cy.checkItemValue('#queryDescription', 'value', '');
      cy.get('.CodeMirror').should(item => expect(item).to.exist);
    });

    it('Fill in the information', () => {
      cy.typeText('#queryName', query.name);
      cy.typeText('#queryDescription', query.description);

      cy.clickItem(autoCompleteFields.endpoint, true);
      cy.clickItem(query.chosenEndpoint);

      cy.typeYasguiEditor('textarea', 2, query.query);
      cy.typeYasguiEditor('textarea', 2, '{control}{enter}');

      for (var i = 1; i < 6; i++) {
        cy.get(`.rt-tbody > :nth-child(${i}) > .rt-tr`).should('exist');
      }
      cy.checkVisibility('ResultNothingFound', false);
      cy.checkVisibility('Result', true);

      cy.clickItem('FinishAddButton');
    });

    it('Check if edit dialog appears', () => {
      cy.checkValueText('AlertDescription', 'Add the newly created query');
    });

    it('Choose Yes', () => {
      cy.clickItem('AlertYesButton');
    });

    it('Check if card is added in the overview and get the id of it', () => {
      cy.getHeaderCard().then(item => {
        expect(item.text()).to.equals(query.name);
        queryId = item.attr('data-testid').split('_')[1];
      });
    });

    it('Log the id of the created query', () => {
      cy.log('queryId is', queryId);
    });

    it('Check if card is added in the overview', () => {
      cy.checkExistence(`Card_sparqlqueries_${queryId}`, true);
    });

    it('Edit the query with default Graphs and check the result', () => {
      cy.clickItem(`CardThreeDots_${queryId}`);
      cy.clickItem('MenuItemEdit');

      cy.clickItem(autoCompleteFields.endpoint, true);
      cy.clickItem(query.chosenEndpoint);

      cy.typeYasguiEditor('textarea', 2, '{control}a{del}');
      cy.typeYasguiEditor('textarea', 2, query.queryGraph);
      cy.typeYasguiEditor('textarea', 2, '{control}{enter}');

      cy.get('.rt-tbody > :nth-child(1) > .rt-tr').should('not.exist');
      cy.checkVisibility('ResultNothingFound', true);
      cy.checkVisibility('Result', false);

      cy.clickItem(autoCompleteFields.endpoint, true);
      cy.clickItem(query.chosenEndpointTwo);

      cy.typeText('#defaultGraphs', query.importGraph);

      cy.typeYasguiEditor('textarea', 2, '{control}{enter}');

      cy.checkVisibility('ResultNothingFound', false);
      cy.checkVisibility('Result', true);

      for (var i = 1; i < 9; i++) {
        cy.get(`.rt-tbody > :nth-child(${i}) > .rt-tr`).should('exist');
      }

      cy.clickItem('CancelButton');
    });
  });

  describe('Create Environment', () => {
    const environment = {
      name: '100 LACES Environment Cypress',
      serviceUrl: 'https://semmtech100.com',
      namespace: 'CypressEnv',
      environmentId: 'e56f5475-b28c-41dd-84ea-1e189aa44da7'
    };

    it('Click on menu list Relatics', () => {
      cy.clickItem(testConfig.menuItems.relatics);
      cy.checkURL(testConfig.urls.relatics, true);
    });

    it('Click on the create button', () => {
      cy.clickFirstItem('PlusButton');
    });

    it('Check if the create page for the environments appears', () => {
      cy.checkValueText('PageHeaderTitle', 'New Environment');
      cy.checkItemValue('#envName', 'value', '');
      cy.checkItemValue('#serviceURL', 'value', '');
      cy.checkItemValue('#name', 'value', '');
      cy.checkItemValue('#EnvironmentId', 'value', '');
    });

    it('Fill in the information', () => {
      cy.typeText('#envName', environment.name);
      cy.typeText('#serviceURL', environment.serviceUrl);
      cy.typeText('#name', environment.namespace);
      cy.typeText('#EnvironmentId', environment.environmentId);
      cy.clickItem('FinishAddButton');
    });

    it('Check if edit dialog appears', () => {
      cy.checkValueText(
        'AlertDescription',
        'Add the newly created environment'
      );
    });

    it('Choose Yes', () => {
      cy.clickItem('AlertYesButton');
    });

    it('Check if card is added in the overview and get the id of it', () => {
      cy.getHeaderCard('Environments').then(item => {
        expect(item.text()).to.equals(environment.name);
        environmentId = item.attr('data-testid').split('_')[1];
      });
    });

    it('Log the id of the created query', () => {
      cy.log('environmentId is', environmentId);
    });

    it('Check if card is added in the overview', () => {
      cy.checkExistence(`Card_environments_${environmentId}`, true);
    });
  });

  describe('Create Workspace (with 2 targetsystems)', () => {
    const workspace = {
      environmentId: '123env',
      environmentName: 'TEST env 001',
      workspaceId: 'abc-def-ghi-jkl',
      name: '100 LACES Workspace Cypress',
      targetDataSystems: [
        {
          operationName: 'Cypress target 1',
          entryCode: '123',
          type: 'Sending',
          xpathExpression: 'x path 1'
        },
        {
          operationName: 'Cypress target 2',
          entryCode: '456',
          type: 'Receiving',
          xpathExpression: 'x path 2'
        }
      ]
    };

    it('Click on the create button', () => {
      cy.clickAnyItem('PlusButton', 1);
    });

    it('Check if the create page for the workspaces appears', () => {
      cy.checkValueText('PageHeaderTitle', 'New Workspace');
      cy.checkItemValue('#name', 'value', '');
      cy.checkItemValue('#WID', 'value', '');
      cy.checkItemValue(autoCompleteFields.environment, 'value', '');
      cy.checkExistence('TargetsystemsWorkspace_0', false);
    });

    it('Fill in the information', () => {
      cy.typeText('#name', workspace.name);
      cy.typeText('#WID', workspace.workspaceId);

      cy.clickItem(autoCompleteFields.environment, true);
      cy.clickItem(workspace.environmentId);

      cy.clickItem('Add_TargetSystem');
      cy.typeText(
        '#OperationName_0',
        workspace.targetDataSystems[0].operationName
      );
      cy.typeText(
        '#xpathExpression_0',
        workspace.targetDataSystems[0].xpathExpression
      );
      cy.typeText('#EntryCode_0', workspace.targetDataSystems[0].entryCode);
      cy.clickItem(autoCompleteFields.type, true);
      cy.clickItem(workspace.targetDataSystems[0].type);

      cy.clickItem('Add_TargetSystem');
      cy.typeText(
        '#OperationName_1',
        workspace.targetDataSystems[1].operationName
      );
      cy.typeText(
        '#xpathExpression_1',
        workspace.targetDataSystems[1].xpathExpression
      );
      cy.typeText('#EntryCode_1', workspace.targetDataSystems[1].entryCode);

      cy.clickItem(
        `[data-testid=TargetsystemsWorkspace_1] ${autoCompleteFields.type}`,
        true
      );

      cy.clickItem(workspace.targetDataSystems[1].type);

      cy.clickItem('FinishAddButton');
    });

    it('Check if edit dialog appears', () => {
      cy.checkValueText('AlertDescription', 'Add the newly created workspace');
    });

    it('Choose Yes', () => {
      cy.clickItem('AlertYesButton');
    });

    it('Check if card is added in the overview and get the id of it', () => {
      cy.getHeaderCard('Workspaces').then(item => {
        expect(item.text()).to.equals(workspace.name);
        workspaceId = item.attr('data-testid').split('_')[1];
      });
    });

    it('Log the id of the created query', () => {
      cy.log('workspaceId is', workspaceId);
    });

    it('Check if card is added in the overview', () => {
      cy.checkExistence(`Card_workspaces_${workspaceId}`, true);
    });
  });

  describe('Create Json API (with 2 endpoint)', () => {
    const jsonApi = {
      name: '100 LACES Json API Cypress',
      serviceUrl: 'https://api-service.neanex.com',
      endpoints: [
        {
          name: 'Cypress Sending',
          type: 'Sending',
          path: 'Path Sending'
        },
        {
          name: 'Cypress Receiving',
          type: 'Receiving',
          path: 'Path Receiving'
        }
      ]
    };

    it('Click on menu list Neanex', () => {
      cy.clickItem(testConfig.menuItems.neanex);
      cy.checkURL(testConfig.urls.neanex, true);
    });

    it('Click on the create button', () => {
      cy.clickItem('PlusButton');
    });

    it('Check if the create page for the json api appears', () => {
      cy.checkValueText('PageHeaderTitle', 'New Json Api');
      cy.checkItemValue('#name', 'value', '');
      cy.checkItemValue('#serviceURL', 'value', '');
      cy.checkExistence('EndpointsJsonApi_0', false);
    });

    it('Fill in the information', () => {
      cy.typeText('#name', jsonApi.name);
      cy.typeText('#serviceURL', jsonApi.serviceUrl);

      cy.clickItem('Add_Endpoint');
      cy.typeText('#Name_0', jsonApi.endpoints[0].name);
      cy.typeText('#Path_0', jsonApi.endpoints[0].path);
      cy.clickItem(
        `[data-testid=EndpointsJsonApi_0] ${autoCompleteFields.type}`,
        true
      );
      cy.clickItem(jsonApi.endpoints[0].type);

      cy.clickItem('Add_Endpoint');
      cy.typeText('#Name_1', jsonApi.endpoints[1].name);
      cy.typeText('#Path_1', jsonApi.endpoints[1].path);
      cy.clickItem(
        `[data-testid=EndpointsJsonApi_1] ${autoCompleteFields.type}`,
        true
      );
      cy.clickItem(jsonApi.endpoints[1].type);

      cy.clickItem('FinishAddButton');
    });

    it('Check if edit dialog appears', () => {
      cy.checkValueText('AlertDescription', 'Add the newly created json api');
    });

    it('Choose Yes', () => {
      cy.clickItem('AlertYesButton');
    });

    it('Check if card is added in the overview and get the id of it', () => {
      cy.getHeaderCard('JsonApis').then(item => {
        expect(item.text()).to.equals(jsonApi.name);
        jsonApiId = item.attr('data-testid').split('_')[1];
      });
    });

    it('Log the id of the created json api', () => {
      cy.log('jsonApiId is', jsonApiId);
    });

    it('Check if card is added in the overview', () => {
      cy.checkExistence(`Card_jsonapis_${jsonApiId}`, true);
    });
  });
});
