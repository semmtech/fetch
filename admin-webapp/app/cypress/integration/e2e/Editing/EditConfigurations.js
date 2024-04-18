import { autoCompleteFields, testConfig } from '../../../utils';
import AddTestData from '../../../utils/bootstrap/AddTestData';
import DeleteTestData from '../../../utils/bootstrap/DeleteTestData';

describe('Admin UI Editing Configurations', () => {
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

  describe('Edit for Relatics Configurations', () => {
    const relaticsConfig = {
      additionalInputs: '',
      childrenQuery: 'Children cypress query',
      description: 'This is the configuration for cypress 001',
      displayName: 'cypress_001',
      enablePagination: true,
      endDate: '25/04/2020',
      id: '12345',
      importStep: {
        importTarget: '',
        name: 'Import hierarchy',
        nameTwo: 'Import definition',
        sparqlQuery: ''
      },
      isActive: true,
      name: 'Cypress first mocked configuration',
      rootsQuery: 'Roots cypress query',
      sparqlEndpoint: 'Endpoint 001',
      startDate: '25/04/2019',
      columns: ['isImported', 'label', 'uri', 'uuid', 'hasChildren'],
      workspace: 'Workspace cypress 001'
    };

    const updatedRelaticsConfig = {
      additionalInput: '34',
      description: 'This is the updated configuration for cypress 001',
      displayName: 'cypress_updated_001',
      enablePagination: false,
      importTarget: '12',
      isActive: false,
      name: 'Cypress first updated configuration',
      sparqlEndpoint: 'endpoint456',
      sparqlEndpointName: 'Endpoint 002',
      workspace: {
        additionalInput: '78',
        id: 'relatics456',
        importTarget: '56',
        name: 'Workspace cypress 002',
        targetsystemReceiving: 'Attributes',
        targetsystemSending: 'Members'
      }
    };

    it('Check if the sessionId exists', () => {
      cy.getCookie(testConfig.sessionId).should('exist');
      cy.reload(true);
    });

    it('Check if the Relatics configs are there', () => {
      cy.checkExistence(`Card_configurations_${relaticsConfig.id}`, true);
      cy.checkExistence('Card_configurations_67890', true);
    });

    it('Click on the action button and pick edit', () => {
      cy.clickItem(`CardThreeDots_${relaticsConfig.id}`);

      cy.checkVisibility('MenuItemEdit', true);
      cy.checkVisibility('MenuItemDelete', true);
      cy.checkVisibility('CardEdit', true);
      cy.checkVisibility('CardEditText', true);

      cy.clickItem('MenuItemEdit');
    });

    it('Check if the edit page for the relatics configuration appears', () => {
      cy.checkVisibility('CancelButton', true);
      cy.checkVisibility('FinishAddButton', true);

      cy.checkValueText('PageHeaderTitle', 'Details Relatics Configuration');
      cy.checkItemValue('#inputName', 'value', relaticsConfig.name);
      cy.checkItemValue(
        '#inputDisplayName',
        'value',
        relaticsConfig.displayName
      );
      cy.checkItemValue(
        '#inputDescription',
        'value',
        relaticsConfig.description
      );
      cy.checkDateValue('ConfigDateStart', relaticsConfig.startDate);
      cy.checkDateValue('ConfigDateEnd', relaticsConfig.endDate);
      cy.checkItemValue(
        '#isActiveCheckbox',
        'checked',
        relaticsConfig.isActive
      );
      cy.checkItemValue(
        '#EnablePagination',
        'checked',
        relaticsConfig.enablePagination
      );

      relaticsConfig.columns.map((columnName, index) => {
        cy.checkVisibility(`Column_${columnName}_${index}`, true);
      });

      cy.checkItemValue(
        autoCompleteFields.sparqlEndpoint,
        'value',
        relaticsConfig.sparqlEndpoint
      );
      cy.checkItemValue(
        autoCompleteFields.workspace,
        'value',
        relaticsConfig.workspace
      );
      cy.checkItemValue(
        autoCompleteFields.sparqlRootsQuery,
        'value',
        relaticsConfig.rootsQuery
      );
      cy.checkItemValue(
        autoCompleteFields.sparqlChildrenQuery,
        'value',
        relaticsConfig.childrenQuery
      );
      cy.checkItemValue(
        autoCompleteFields.additionalInput,
        'value',
        relaticsConfig.additionalInputs
      );

      cy.checkVisibility('RelaticsImgSearch', true);
      cy.checkExistence('DND-Step0', true);
      cy.checkExistence('DND-Step1', true);

      cy.checkItemValue('#StepName_0', 'value', relaticsConfig.importStep.name);
      cy.checkItemValue('#DefaultGraphs_0', 'value', '');
      cy.checkItemValue(
        `[data-testid=DND-Step0] ${autoCompleteFields.query}`,
        'value',
        relaticsConfig.importStep.sparqlQuery
      );
      cy.checkItemValue(
        `[data-testid=DND-Step0] ${autoCompleteFields.target}`,
        'value',
        relaticsConfig.importStep.importTarget
      );

      cy.checkItemValue(
        '#StepName_1',
        'value',
        relaticsConfig.importStep.nameTwo
      );
      cy.checkItemValue('#DefaultGraphs_1', 'value', '');
      cy.checkItemValue(
        `[data-testid=DND-Step1] ${autoCompleteFields.query}`,
        'value',
        relaticsConfig.importStep.sparqlQuery
      );
      cy.checkItemValue(
        `[data-testid=DND-Step1] ${autoCompleteFields.target}`,
        'value',
        relaticsConfig.importStep.importTarget
      );
    });

    it('Click on cancel', () => {
      cy.clickItem('CancelButton');
      cy.checkExistence(`Card_configurations_${relaticsConfig.id}`, true);
      cy.checkValueText(`CardTitle_${relaticsConfig.id}`, relaticsConfig.name);
      cy.checkExistence(
        '[data-testid=CardHeader_12345] > .MuiAvatar-root > [data-testid=RelaticsLogo]',
        true,
        true
      );
      cy.checkExistence(
        '[data-testid=CardHeader_67890] > .MuiAvatar-root > [data-testid=RelaticsLogo]',
        true,
        true
      );
      cy.checkExistence(
        '[data-testid=CardHeader_159753] > .MuiAvatar-root > [data-testid=NeanexLogo]',
        true,
        true
      );
    });

    it('Click on the action button and pick edit again', () => {
      cy.clickItem(`CardThreeDots_${relaticsConfig.id}`);
      cy.clickItem('MenuItemEdit');
    });

    it('Edit the configuration', () => {
      cy.typeText('#inputName', updatedRelaticsConfig.name);
      cy.typeText('#inputDescription', updatedRelaticsConfig.description);
      cy.typeText('#inputDisplayName', updatedRelaticsConfig.displayName);

      cy.clickItem('#isActiveCheckbox', true);
      cy.clickItem('#EnablePagination', true);

      cy.clickItem(autoCompleteFields.sparqlEndpoint, true);
      cy.clickItem(updatedRelaticsConfig.sparqlEndpoint);

      cy.clickItem(autoCompleteFields.additionalInput, true);
      cy.clickItem(updatedRelaticsConfig.additionalInput);

      cy.clickItem(
        `[data-testid=DND-Step0] ${autoCompleteFields.target}`,
        true
      );
      cy.clickItem(updatedRelaticsConfig.importTarget);

      cy.clickItem(autoCompleteFields.workspace, true);
      cy.clickItem(updatedRelaticsConfig.workspace.id);

      cy.checkItemValue(autoCompleteFields.additionalInput, 'value', '');
      cy.clickItem(autoCompleteFields.additionalInput, true);
      cy.checkSelectFields([updatedRelaticsConfig.workspace.additionalInput]);
      cy.clickItem(updatedRelaticsConfig.workspace.additionalInput);

      cy.checkItemValue(
        autoCompleteFields.additionalInput,
        'value',
        updatedRelaticsConfig.workspace.targetsystemSending
      );
      cy.checkItemValue(
        `[data-testid=DND-Step0] ${autoCompleteFields.target}`,
        'value',
        ''
      );
      cy.clickItem(
        `[data-testid=DND-Step0] ${autoCompleteFields.target}`,
        true
      );
      cy.checkSelectFields([updatedRelaticsConfig.workspace.importTarget]);
      cy.clickItem(updatedRelaticsConfig.workspace.importTarget);

      cy.clickItem('FinishAddButton');
    });

    it('Check if edit dialog appears', () => {
      cy.checkVisibility('AlertTitle', true);
      cy.checkVisibility('AlertDescription', true);
      cy.checkValueText(
        'AlertDescription',
        `Update the following Relatics configuration: ${relaticsConfig.name} -> ${updatedRelaticsConfig.name}`
      );
      cy.checkVisibility('AlertNoButton', true);
      cy.checkVisibility('AlertYesButton', true);
    });

    it('Choose No', () => {
      cy.clickItem('AlertNoButton');
    });

    it('Choose Yes', () => {
      cy.clickItem('FinishAddButton');
      cy.clickItem('AlertYesButton');
      cy.wait(2000);
      cy.checkExistence(`Card_configurations_${relaticsConfig.id}`, true);
      cy.checkValueText(
        `CardTitle_${relaticsConfig.id}`,
        updatedRelaticsConfig.name
      );
    });

    it('Click on the action button and pick edit again', () => {
      cy.clickItem(`CardThreeDots_${relaticsConfig.id}`);
      cy.clickItem('MenuItemEdit');
    });

    it('Check the updated values in the edit page', () => {
      cy.checkItemValue('#inputName', 'value', updatedRelaticsConfig.name);
      cy.checkItemValue(
        '#inputDescription',
        'value',
        updatedRelaticsConfig.description
      );
      cy.checkItemValue(
        '#inputDisplayName',
        'value',
        updatedRelaticsConfig.displayName
      );
      cy.checkItemValue(
        '#isActiveCheckbox',
        'checked',
        updatedRelaticsConfig.isActive
      );
      cy.checkItemValue(
        '#EnablePagination',
        'checked',
        updatedRelaticsConfig.enablePagination
      );
      cy.checkItemValue(
        autoCompleteFields.sparqlEndpoint,
        'value',
        updatedRelaticsConfig.sparqlEndpointName
      );
      cy.checkItemValue(
        autoCompleteFields.workspace,
        'value',
        updatedRelaticsConfig.workspace.name
      );
      cy.checkItemValue(
        autoCompleteFields.additionalInput,
        'value',
        updatedRelaticsConfig.workspace.targetsystemSending
      );
      cy.checkItemValue(
        `[data-testid=DND-Step0] ${autoCompleteFields.target}`,
        'value',
        updatedRelaticsConfig.workspace.targetsystemReceiving
      );
      cy.clickItem('CancelButton');
    });
  });

  describe('Edit for Json Api Configurations', () => {
    const neanexConfig = {
      additionalInputs: '',
      childrenQuery: 'Second test query',
      description: 'This is the configuration for cypress 003',
      displayName: 'cypress_003',
      enablePagination: true,
      endDate: '09/12/2020',
      id: '159753',
      importStep: {
        importTarget: '',
        name: 'Import hierarchy',
        nameTwo: 'Import definition',
        sparqlQuery: ''
      },
      isActive: false,
      jsonApi: '',
      name: 'Cypress third mocked configuration',
      rootsQuery: 'TEST query',
      sparqlEndpoint: 'Endpoint 003',
      startDate: '09/12/2019',
      columns: ['isImported', 'label', 'uri', 'uuid', 'hasChildren']
    };

    const updatedNeanexConfig = {
      additionalInput: 'O13',
      importTarget: 'O12',
      jsonApi: {
        additionalInput: 'O24',
        id: '456jsonapi',
        importTarget: 'O23',
        name: 'TEST json api 001',
        endpointReceiving: 'Endpoint documents',
        endpointSending: 'Endpoint members'
      },
      jsonApiId: '123jsonapi',
      sparqlEndpoint: 'endpoint456',
      sparqlEndpointName: 'Endpoint 002'
    };

    it('Check if the sessionId exists', () => {
      cy.getCookie(testConfig.sessionId).should('exist');
      cy.reload(true);
    });

    it('Check if the Neanex config is there', () => {
      cy.checkExistence(`Card_configurations_${neanexConfig.id}`, true);
    });

    it('Click on the action button and pick edit', () => {
      cy.clickItem(`CardThreeDots_${neanexConfig.id}`);
      cy.clickItem('MenuItemEdit');
    });

    it('Check if the edit page for the neanex configuration appears', () => {
      cy.checkValueText('PageHeaderTitle', 'Details JSON API Configuration');
      cy.checkItemValue('#inputName', 'value', neanexConfig.name);
      cy.checkItemValue('#inputDisplayName', 'value', neanexConfig.displayName);
      cy.checkItemValue('#inputDescription', 'value', neanexConfig.description);
      cy.checkDateValue('ConfigDateStart', neanexConfig.startDate);
      cy.checkDateValue('ConfigDateEnd', neanexConfig.endDate);
      cy.checkItemValue('#isActiveCheckbox', 'checked', neanexConfig.isActive);
      cy.checkItemValue(
        '#EnablePagination',
        'checked',
        neanexConfig.enablePagination
      );

      neanexConfig.columns.map((columnName, index) => {
        cy.checkVisibility(`Column_${columnName}_${index}`, true);
      });

      cy.checkItemValue(
        autoCompleteFields.sparqlEndpoint,
        'value',
        neanexConfig.sparqlEndpoint
      );
      cy.checkItemValue(
        autoCompleteFields.jsonApi,
        'value',
        neanexConfig.jsonApi
      );
      cy.checkItemValue(
        autoCompleteFields.sparqlRootsQuery,
        'value',
        neanexConfig.rootsQuery
      );
      cy.checkItemValue(
        autoCompleteFields.sparqlChildrenQuery,
        'value',
        neanexConfig.childrenQuery
      );
      cy.checkItemValue(
        autoCompleteFields.additionalInput,
        'value',
        neanexConfig.additionalInputs
      );

      cy.checkVisibility('NeanexImgSearch', false);
      cy.checkExistence('DND-Step0', true);
      cy.checkExistence('DND-Step1', true);

      cy.checkItemValue('#StepName_0', 'value', neanexConfig.importStep.name);
      cy.checkItemValue('#DefaultGraphs_0', 'value', '');
      cy.checkItemValue(
        `[data-testid=DND-Step0] ${autoCompleteFields.query}`,
        'value',
        neanexConfig.importStep.sparqlQuery
      );
      cy.checkItemValue(
        `[data-testid=DND-Step0] ${autoCompleteFields.target}`,
        'value',
        neanexConfig.importStep.importTarget
      );

      cy.checkItemValue(
        '#StepName_1',
        'value',
        neanexConfig.importStep.nameTwo
      );
      cy.checkItemValue('#DefaultGraphs_1', 'value', '');
      cy.checkItemValue(
        `[data-testid=DND-Step1] ${autoCompleteFields.query}`,
        'value',
        neanexConfig.importStep.sparqlQuery
      );
      cy.checkItemValue(
        `[data-testid=DND-Step1] ${autoCompleteFields.target}`,
        'value',
        neanexConfig.importStep.importTarget
      );
    });

    it('Click on cancel', () => {
      cy.clickItem('CancelButton');
      cy.checkExistence(`Card_configurations_${neanexConfig.id}`, true);
      cy.checkValueText(`CardTitle_${neanexConfig.id}`, neanexConfig.name);
    });

    it('Click on the action button and pick edit again', () => {
      cy.clickItem(`CardThreeDots_${neanexConfig.id}`);
      cy.clickItem('MenuItemEdit');
    });

    it('Edit the configuration', () => {
      cy.clickItem(autoCompleteFields.sparqlEndpoint, true);
      cy.clickItem(updatedNeanexConfig.sparqlEndpoint);

      cy.clickItem(autoCompleteFields.jsonApi, true);
      cy.clickItem(updatedNeanexConfig.jsonApi.id);
      cy.checkVisibility('NeanexImgSearch', true);

      cy.clickItem(autoCompleteFields.additionalInput, true);
      cy.clickItem(updatedNeanexConfig.additionalInput);
      cy.clickItem(
        `[data-testid=DND-Step0] ${autoCompleteFields.target}`,
        true
      );
      cy.clickItem(updatedNeanexConfig.importTarget);

      cy.clickItem(autoCompleteFields.jsonApi, true);
      cy.clickItem(updatedNeanexConfig.jsonApiId);

      cy.checkItemValue(autoCompleteFields.additionalInput, 'value', '');
      cy.clickItem(autoCompleteFields.additionalInput, true);
      cy.checkSelectFields([updatedNeanexConfig.jsonApi.additionalInput]);
      cy.clickItem(updatedNeanexConfig.jsonApi.additionalInput);

      cy.checkItemValue(
        autoCompleteFields.additionalInput,
        'value',
        updatedNeanexConfig.jsonApi.endpointSending
      );
      cy.checkItemValue(
        `[data-testid=DND-Step0] ${autoCompleteFields.target}`,
        'value',
        ''
      );
      cy.clickItem(
        `[data-testid=DND-Step0] ${autoCompleteFields.target}`,
        true
      );
      cy.checkSelectFields([updatedNeanexConfig.jsonApi.importTarget]);
      cy.clickItem(updatedNeanexConfig.jsonApi.importTarget);

      cy.clickItem('FinishAddButton');
    });

    it('Check if edit dialog appears', () => {
      cy.checkVisibility('AlertTitle', true);
      cy.checkVisibility('AlertDescription', true);
      cy.checkValueText(
        'AlertDescription',
        `Update the following JSON API configuration: ${neanexConfig.name} -> ${neanexConfig.name}`
      );
    });

    it('Choose No', () => {
      cy.clickItem('AlertNoButton');
    });

    it('Choose Yes', () => {
      cy.clickItem('FinishAddButton');
      cy.clickItem('AlertYesButton');
      cy.wait(2000);
      cy.checkExistence(`Card_configurations_${neanexConfig.id}`, true);
    });

    it('Click on the action button and pick edit again', () => {
      cy.clickItem(`CardThreeDots_${neanexConfig.id}`);
      cy.clickItem('MenuItemEdit');
    });

    it('Check the updated values in the edit page', () => {
      cy.checkItemValue(
        autoCompleteFields.sparqlEndpoint,
        'value',
        updatedNeanexConfig.sparqlEndpointName
      );
      cy.checkItemValue(
        autoCompleteFields.jsonApi,
        'value',
        updatedNeanexConfig.jsonApi.name
      );
      cy.checkItemValue(
        autoCompleteFields.additionalInput,
        'value',
        updatedNeanexConfig.jsonApi.endpointSending
      );
      cy.checkItemValue(
        `[data-testid=DND-Step0] ${autoCompleteFields.target}`,
        'value',
        updatedNeanexConfig.jsonApi.endpointReceiving
      );
      cy.clickItem('CancelButton');
    });
  });
});
