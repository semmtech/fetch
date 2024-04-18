import { autoCompleteFields, testConfig } from '../../../utils';
import AddTestData from '../../../utils/bootstrap/AddTestData';
import DeleteTestData from '../../../utils/bootstrap/DeleteTestData';

describe('Admin UI Editing', () => {
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

  describe('Edit for SPARQL Endpoints', () => {
    const endpoint = {
      id: 'endpoint123',
      name: 'Endpoint 001',
      url:
        'https://www.laces-platform.tech/semmtech/ns/fetch/private/test/neanex/library/assets/sparql',
      authenticationMethod: {
        type: 'LACES TOKEN',
        privateKey:
          'MIICdgIBADANBgkqhkiG9w0BAQEFAASCAmAwggJcAgEAAoGBAJUoVTqcqqoxxlZibKe9pcrT4XciW+Qq7kLAitSXbEkjl6gL1dWv6kRNNPkqaOwsKwKUGSLEWEuQMDmNfbZeUWPYRKcU6+KMMPF847WBP+7cY+NtPvhxIM9ef/P+LzMoceQPx/xrzpVN3BtDz6C6YAHPRQt91YfFPc4tUsf1BIYRAgMBAAECgYAtB30bcbqQIPC4mYQl67oGjoqtlaDaNB+z5T7ESWZ2ehlJsTEADtiRgCFy61u7mOXvJFimR1JElaYJae6+xKCbJk0eVPbwy1MvVH/0ggOGZUY5aZfqveHXRWaDB1IUph478JmiZPMRgIIVw5uhSxO83qtfXsbgd5HtO/g46KmnBQJBANiixICyIEcZjiQ6/cM+GUYMAmInrCHlM4U3vSU/oIkQknVsoeNNbNERgT6nKLhv4txg1FDVqMQUeznLkJftZ6cCQQCwQq8X4JyTKoeKgnuMyVLxnt30B36/qoH+SmvZHZWl00kap0sTjhQWhrGZYh0dPTMkNlBGk68mZSGNGK3cgduHAkEAjK8nbWxACexOorisk16AizzBT3z0DA8MpjbMXqQzXM+mTRt/Bl4BjMQRat6jUyNV8EfxmY0nTC8A10ebXw6NgQJAXIomM3sRuZJSpz3qb/gjPAgUr9JfkXGL3l5kURFfSDit4PiESjgGA+2jwMvqTTecah659tQC2T2vZ8zVOzhScQJAcEjtCABEGHZKeiwGjnrgjG3yWycUMXjo6/c43zRBUimyE7uWi9jTbnlbM2k/4+iM0VZH7gYH9y/cVjfY1IDS8g==',
        applicationId: 'laces-fetch-addon'
      }
    };

    const updatedEndpoint = {
      name: 'Endpoint updated 001',
      url: `https://endpoint123.laces.tech/catalogs/test/repositories/buildings`,
      authenticationMethod: {
        type: 'BASIC',
        userName: 'Mike',
        password: 'Cypress',
        privateKey: '456',
        applicationId: '50'
      }
    };

    it('Click on menu list endpoints', () => {
      cy.clickItem(testConfig.menuItems.endpoints);
    });

    it('Check if the endpoints are there', () => {
      cy.checkExistence(`Card_sparqlendpoints_${endpoint.id}`, true);
      cy.checkExistence('Card_sparqlendpoints_endpoint456', true);
      cy.checkExistence('Card_sparqlendpoints_endpoint159753', true);
    });

    it('Click on the action button for first card and pick edit', () => {
      cy.clickItem(`CardThreeDots_${endpoint.id}`);
      cy.clickItem('MenuItemEdit');
    });

    it('Check if the edit page for the endpoints appears', () => {
      cy.checkValueText('PageHeaderTitle', 'Details Endpoint');
      cy.checkItemValue('#endpointName', 'value', endpoint.name);
      cy.checkItemValue('#endpointURLName', 'value', endpoint.url);
      cy.checkExistence('TestingZone', true);
      cy.checkItemValue(
        autoCompleteFields.authType,
        'value',
        endpoint.authenticationMethod.type
      );
      cy.checkItemValue(
        '#applicationId',
        'value',
        endpoint.authenticationMethod.applicationId
      );
      cy.checkItemValue(
        '#privateKey',
        'value',
        endpoint.authenticationMethod.privateKey
      );
    });

    it('Edit the endpoint', () => {
      cy.typeText('#endpointName', updatedEndpoint.name);
      cy.typeText('#endpointURLName', updatedEndpoint.url);

      cy.clickItem(autoCompleteFields.authType, true);

      cy.checkVisibility('LACES_TOKEN', true);
      cy.checkVisibility('BASIC', true);
      cy.checkVisibility('NONE', true);

      cy.clickItem('LACES_TOKEN');

      cy.typeText(
        '#privateKey',
        updatedEndpoint.authenticationMethod.privateKey
      );

      cy.typeText(
        '#applicationId',
        updatedEndpoint.authenticationMethod.applicationId
      );

      cy.clickItem(
        '.MuiInputAdornment-root > [data-testid=showPrivateKey]',
        true
      );
      cy.checkItemValue(
        '#privateKey',
        'value',
        updatedEndpoint.authenticationMethod.privateKey
      );
      cy.clickItem('hidePrivateKey');
      cy.clickItem(autoCompleteFields.authType, true);

      cy.clickItem('BASIC');

      cy.typeText('#userName', updatedEndpoint.authenticationMethod.userName);

      cy.typeText('#password', updatedEndpoint.authenticationMethod.password);

      cy.clickItem('FinishAddButton');
    });

    it('Check if edit dialog appears', () => {
      cy.checkValueText(
        'AlertDescription',
        `Update the following endpoint: ${endpoint.name} -> ${updatedEndpoint.name}`
      );
    });

    it('Choose Yes', () => {
      cy.clickItem('AlertYesButton');
      cy.wait(2000);
      cy.checkExistence(`Card_sparqlendpoints_${endpoint.id}`, true);
      cy.checkValueText(`CardTitle_${endpoint.id}`, updatedEndpoint.name);
    });

    it('Click on the action button and pick edit again', () => {
      cy.clickItem(`CardThreeDots_${endpoint.id}`);
      cy.clickItem('MenuItemEdit');
    });

    it('Check the updated values in the edit page', () => {
      cy.checkItemValue('#endpointName', 'value', updatedEndpoint.name);
      cy.checkItemValue('#endpointURLName', 'value', updatedEndpoint.url);
      cy.checkItemValue(
        autoCompleteFields.authType,
        'value',
        updatedEndpoint.authenticationMethod.type
      );
      cy.checkExistence('TestingZone', true);

      cy.checkItemValue(
        '#userName',
        'value',
        updatedEndpoint.authenticationMethod.userName
      );
      cy.checkItemValue(
        '#password',
        'value',
        updatedEndpoint.authenticationMethod.password
      );
      cy.clickItem('CancelButton');
    });
  });

  describe('Edit for Environments', () => {
    const environment = {
      id: '123env',
      name: 'TEST env 001',
      serviceUrl: 'https://semmtech.relaticsonline.com/DataExchange.asmx',
      namespace: '001',
      environmentId: 'e56f5475-b28c-41dd-84ea-1e189aa44da7'
    };

    const updatedEnvironment = {
      name: 'UPDATED TEST env 001',
      serviceUrl: 'https://semmtech001updated.com',
      namespace: 'updated 001',
      environmentId: 'e56f5475-b28c-41dd-84ea-1e189aa44d10'
    };

    it('Click on menu list relatics', () => {
      cy.clickItem(testConfig.menuItems.relatics);
    });

    it('Check if the environment are there', () => {
      cy.checkExistence(`Card_environments_${environment.id}`, true);
      cy.checkExistence('Card_environments_789env', true);
      cy.checkExistence('Card_environments_456env', true);
    });

    it('Click on the action button and pick edit', () => {
      cy.clickItem(`CardThreeDots_${environment.id}`);
      cy.clickItem('MenuItemEdit');
    });

    it('Check if the edit page for the environment appears', () => {
      cy.checkValueText('PageHeaderTitle', 'Details Environment');
      cy.checkItemValue('#envName', 'value', environment.name);
      cy.checkItemValue('#serviceURL', 'value', environment.serviceUrl);
      cy.checkItemValue('#name', 'value', environment.namespace);
      cy.checkItemValue('#EnvironmentId', 'value', environment.environmentId);
    });

    it('Edit the environment', () => {
      cy.typeText('#envName', updatedEnvironment.name);
      cy.typeText('#serviceURL', updatedEnvironment.serviceUrl);
      cy.typeText('#name', updatedEnvironment.namespace);
      cy.typeText('#EnvironmentId', updatedEnvironment.environmentId);

      cy.clickItem('FinishAddButton');
    });

    it('Check if edit dialog appears', () => {
      cy.checkValueText(
        'AlertDescription',
        `Update the following environment: ${environment.name} -> ${updatedEnvironment.name}`
      );
    });

    it('Choose Yes', () => {
      cy.clickItem('AlertYesButton');
      cy.wait(2000);
      cy.checkExistence(`Card_environments_${environment.id}`, true);
      cy.checkValueText(`CardTitle_${environment.id}`, updatedEnvironment.name);
    });

    it('Click on the action button and pick edit again', () => {
      cy.clickItem(`CardThreeDots_${environment.id}`);
      cy.clickItem('MenuItemEdit');
    });

    it('Check the updated values in the edit page', () => {
      cy.checkItemValue('#envName', 'value', updatedEnvironment.name);
      cy.checkItemValue('#serviceURL', 'value', updatedEnvironment.serviceUrl);
      cy.checkItemValue('#name', 'value', updatedEnvironment.namespace);
      cy.checkItemValue(
        '#EnvironmentId',
        'value',
        updatedEnvironment.environmentId
      );
      cy.clickItem('CancelButton');
    });
  });

  describe('Edit for Workspace (with targetsystems)', () => {
    const workspace = {
      environmentName: 'UPDATED TEST env 001',
      id: 'relatics123',
      workspaceId: 'workspace123',
      name: 'Workspace cypress 001',
      targetDataSystem: {
        operationName: 'GetExistingDocuments',
        entryCode: 'b93k9JkJS9g',
        type: 'Receiving',
        xpathExpression: '//ReportPart/generic/@ForeignKey'
      }
    };

    const updatedWorkspace = {
      name: 'Updated workspace of cypress 001',
      targetDataSystem: {
        operationName: 'GetExistingClassifications',
        type: 'Receiving'
      }
    };

    it('Click on menu list relatics', () => {
      cy.clickItem(testConfig.menuItems.relatics);
    });

    it('Check if the workspace is there', () => {
      cy.checkExistence(`Card_workspaces_${workspace.id}`, true);
    });

    it('Click on the action button and pick edit', () => {
      cy.clickItem(`CardThreeDots_${workspace.id}`);
      cy.clickItem('MenuItemEdit');
    });

    it('Check if the edit page for the workspace appears', () => {
      cy.checkValueText('PageHeaderTitle', 'Details Workspace');

      cy.checkItemValue('#name', 'value', workspace.name);
      cy.checkItemValue('#WID', 'value', workspace.workspaceId);
      cy.checkItemValue(
        autoCompleteFields.environment,
        'value',
        workspace.environmentName
      );
      cy.checkExistence('TargetsystemsWorkspace_0', true);
      cy.checkExistence('TargetsystemsWorkspace_1', true);

      cy.checkItemValue(
        '#OperationName_0',
        'value',
        workspace.targetDataSystem.operationName
      );
      cy.checkItemValue(
        '#xpathExpression_0',
        'value',
        workspace.targetDataSystem.xpathExpression
      );
      cy.checkItemValue(
        '#EntryCode_0',
        'value',
        workspace.targetDataSystem.entryCode
      );
      cy.checkItemValue(
        autoCompleteFields.type,
        'value',
        workspace.targetDataSystem.type
      );
    });

    it('Edit the workspace', () => {
      cy.typeText('#name', updatedWorkspace.name);
      cy.typeText(
        '#OperationName_0',
        updatedWorkspace.targetDataSystem.operationName
      );

      cy.clickItem(
        `[data-testid=TargetsystemsWorkspace_0] ${autoCompleteFields.type}`,
        true
      );

      cy.clickItem(updatedWorkspace.targetDataSystem.type);

      cy.clickItem('Delete_1');

      cy.clickItem('Add_TargetSystem');
      cy.clickItem('Add_TargetSystem');

      cy.clickItem(
        `[data-testid=TargetsystemsWorkspace_1] ${autoCompleteFields.type}`,
        true
      );

      cy.clickItem(updatedWorkspace.targetDataSystem.type);

      cy.clickItem('FinishAddButton');
    });

    it('Check if edit dialog appears', () => {
      cy.checkValueText(
        'AlertDescription',
        `Updating the following workspace: ${workspace.name} -> ${updatedWorkspace.name}`
      );
    });

    it('Choose Yes', () => {
      cy.clickItem('AlertYesButton');
      cy.wait(2000);
      cy.checkExistence(`Card_workspaces_${workspace.id}`, true);
      cy.checkValueText(`CardTitle_${workspace.id}`, updatedWorkspace.name);
    });

    it('Click on the action button and pick edit again', () => {
      cy.clickItem(`CardThreeDots_${workspace.id}`);
      cy.clickItem('MenuItemEdit');
    });

    it('Check the updated values in the edit page', () => {
      cy.checkItemValue('#name', 'value', updatedWorkspace.name);
      cy.checkExistence('TargetsystemsWorkspace_0', true);
      cy.checkExistence('TargetsystemsWorkspace_1', true);
      cy.checkExistence('TargetsystemsWorkspace_2', false);

      cy.checkItemValue(
        '#OperationName_0',
        'value',
        updatedWorkspace.targetDataSystem.operationName
      );
      cy.checkItemValue(
        autoCompleteFields.type,
        'value',
        updatedWorkspace.targetDataSystem.type
      );

      cy.clickItem('CancelButton');
    });
  });

  describe('Edit for json api (with two endpoints)', () => {
    const jsonApi = {
      id: '456jsonapi',
      serviceUrl: 'https://api-service.staging.neanex.com',
      name: 'TEST json api 002',
      endpoints: [
        {
          name: 'Endpoint objects',
          type: 'Receiving',
          path: '/objects'
        },
        {
          name: 'Endpoint attributes',
          type: 'Sending',
          path: '/attributeclasses'
        }
      ]
    };

    const updatedJsonApi = {
      serviceUrl: 'https://api-service.development.neanex.com',
      name: 'TEST json api',
      endpoint: {
        name: 'Endpoint updated attributes',
        type: 'Sending',
        path: '/updatedAttributeclasses'
      }
    };

    it('Click on menu list Neanex', () => {
      cy.clickItem(testConfig.menuItems.neanex);
    });

    it('Check if the jsonapis are there', () => {
      cy.checkExistence(`Card_jsonapis_${jsonApi.id}`, true);
      cy.checkExistence('Card_jsonapis_123jsonapi', true);
    });

    it('Click on the action button and pick edit', () => {
      cy.clickItem(`CardThreeDots_${jsonApi.id}`);
      cy.clickItem('MenuItemEdit');
    });

    it('Check if the edit page for the json api appears', () => {
      cy.checkValueText('PageHeaderTitle', 'Details Json Api');
      cy.checkItemValue('#name', 'value', jsonApi.name);
      cy.checkItemValue('#serviceURL', 'value', jsonApi.serviceUrl);
      cy.checkExistence('EndpointsJsonApi_0', true);
      cy.checkExistence('EndpointsJsonApi_1', true);

      cy.checkItemValue('#Name_0', 'value', jsonApi.endpoints[0].name);
      cy.checkItemValue('#Path_0', 'value', jsonApi.endpoints[0].path);
      cy.checkItemValue(
        `[data-testid=EndpointsJsonApi_0] ${autoCompleteFields.type}`,
        'value',
        jsonApi.endpoints[0].type
      );

      cy.checkItemValue('#Name_1', 'value', jsonApi.endpoints[1].name);
      cy.checkItemValue('#Path_1', 'value', jsonApi.endpoints[1].path);
      cy.checkItemValue(
        `[data-testid=EndpointsJsonApi_1] ${autoCompleteFields.type}`,
        'value',
        jsonApi.endpoints[1].type
      );
    });

    it('Edit the information', () => {
      cy.typeText('#name', updatedJsonApi.name);
      cy.typeText('#serviceURL', updatedJsonApi.serviceUrl);

      cy.typeText('#Name_1', updatedJsonApi.endpoint.name);
      cy.typeText('#Path_1', updatedJsonApi.endpoint.path);

      cy.clickItem(
        `[data-testid=EndpointsJsonApi_1] ${autoCompleteFields.type}`,
        true
      );
      cy.clickItem(updatedJsonApi.endpoint.type);

      cy.clickItem('Delete_0');
      cy.clickItem('Add_Endpoint');
      cy.clickItem('Add_Endpoint');
      cy.clickItem(
        `[data-testid=EndpointsJsonApi_1] ${autoCompleteFields.type}`,
        true
      );
      cy.clickItem(updatedJsonApi.endpoint.type);

      cy.clickItem('FinishAddButton');
    });

    it('Check if edit dialog appears', () => {
      cy.checkValueText(
        'AlertDescription',
        `Updating the following json api: ${jsonApi.name} -> ${updatedJsonApi.name}`
      );
    });

    it('Choose Yes', () => {
      cy.clickItem('AlertYesButton');
    });

    it('Click on the action button and pick edit again', () => {
      cy.clickItem(`CardThreeDots_${jsonApi.id}`);
      cy.clickItem('MenuItemEdit');
    });

    it('Check the updated values in the edit page', () => {
      cy.checkItemValue('#name', 'value', updatedJsonApi.name);
      cy.checkItemValue('#serviceURL', 'value', updatedJsonApi.serviceUrl);

      cy.checkExistence('EndpointsJsonApi_0', true);
      cy.checkExistence('EndpointsJsonApi_1', true);
      cy.checkExistence('EndpointsJsonApi_2', false);

      cy.checkItemValue('#Name_0', 'value', updatedJsonApi.endpoint.name);
      cy.checkItemValue('#Path_0', 'value', updatedJsonApi.endpoint.path);
      cy.checkItemValue(
        `[data-testid=EndpointsJsonApi_0] ${autoCompleteFields.type}`,
        'value',
        updatedJsonApi.endpoint.type
      );

      cy.clickItem('CancelButton');
    });
  });
});
