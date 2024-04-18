import { testConfig, autoCompleteFields } from '../../../utils';
import DeleteCreatedConfigurations from '../../../utils/bootstrap/DeleteCreatedConfigurations';
import AddTestData from '../../../utils/bootstrap/AddTestData';
import DeleteTestData from '../../../utils/bootstrap/DeleteTestData';

const formattedDate = d => {
  return [d.getDate(), d.getMonth() + 1, d.getFullYear()]
    .map(n => (n < 10 ? `0${n}` : `${n}`))
    .join('/');
};

let configJsonApiId = '';
let configRelaticsId = '';

const graph =
  '   http://www.laces-platform.tech/semmtech/ns/fetch/private/test/neanex/library/buildings,    http://www.laces-platform.tech/semmtech/ns/fetch/private/test/neanex/library/devices   ';
const graphNoSpaces =
  'http://www.laces-platform.tech/semmtech/ns/fetch/private/test/neanex/library/buildings,http://www.laces-platform.tech/semmtech/ns/fetch/private/test/neanex/library/devices';

describe('Admin UI Creating Configurations', () => {
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
    DeleteCreatedConfigurations({ configJsonApiId, configRelaticsId });
    cy.logout();
  });

  describe('Create Relatics Configurations', () => {
    const configRelatics = {
      name: '100 LACES Relatics Configuration Cypress',
      displayName: 'Cypress 100 laces fetch',
      startDate: formattedDate(new Date()),
      endDate: '',
      description: 'This is a test description for cypress.',
      workspace: 'relatics123',
      workspaceName: 'Workspace cypress 001',
      sparqlEndpoint: 'endpoint456',
      sparqlEndpointName: 'Endpoint 002',
      sparqlRootsQuery: 'abcde',
      sparqlRootsQueryName: 'Roots cypress query',
      additionalInput: '34',
      additionalInputName: 'GetExistingObjects',
      importSteps: [
        {
          importTarget: '12',
          importTargetName: 'GetExistingDocuments',
          name: 'Step 1',
          query: '123789',
          queryName: 'CYPRESS_003'
        },
        {
          importTarget: '12',
          importTargetName: 'GetExistingDocuments',
          name: 'Step 2',
          query: '67890',
          queryName: 'CYPRESS_002'
        }
      ],
      columns: ['uri', 'label', 'hasChildren', 'uuid', 'isImported']
    };

    it('Check if the sessionId exists', () => {
      cy.getCookie(testConfig.sessionId).should('exist');
    });

    it('Click on the create button', () => {
      cy.clickItem('PlusButton');
    });

    it('Check if the menu appears with the 2 items', () => {
      cy.checkVisibility('MenuItemRelatics', true);
      cy.checkVisibility('MenuItemJsonAPI', true);
    });

    it('Click on Relatics (create config for Relatics)', () => {
      cy.clickItem('MenuItemRelatics');
    });

    it('Check if the create page for the configurations appears', () => {
      cy.checkValueText('PageHeaderTitle', 'New Relatics Configuration');

      cy.checkVisibility('CancelButton', true);
      cy.checkVisibility('FinishAddButton', true);

      cy.checkItemValue('#inputName', 'value', '');
      cy.checkItemValue('#inputDescription', 'value', '');
      cy.checkItemValue('#inputDisplayName', 'value', '');
      cy.checkExistence('ConfigDateStart', true);
      cy.checkExistence('ConfigDateEnd', true);

      cy.checkItemValue('#isActiveCheckbox', 'checked', false);
      cy.checkItemValue('#EnablePagination', 'checked', false);
      cy.checkItemValue(autoCompleteFields.sparqlEndpoint, 'value', '');
      cy.checkItemValue(autoCompleteFields.sparqlRootsQuery, 'value', '');
      cy.checkItemValue(autoCompleteFields.sparqlChildrenQuery, 'value', '');
      cy.checkItemValue(autoCompleteFields.additionalInput, 'value', '');
      cy.checkItemValue(autoCompleteFields.workspace, 'value', '');
      cy.checkVisibility('RelaticsImgSearch', false);
      cy.checkExistence('DND-Step0', false);

      configRelatics.columns.map((columnName, index) => {
        cy.checkVisibility(`Column_${columnName}_${index}`, false);
      });

      cy.checkItemValue('#DefaultGraphsRoot', 'value', '');
      cy.checkItemValue('#DefaultGraphsChildren', 'value', '');
    });

    it('Fill in the information', () => {
      cy.typeText('#inputName', configRelatics.name);
      cy.typeText('#inputDescription', configRelatics.description);
      cy.typeText('#inputDisplayName', configRelatics.displayName);

      cy.clickItem('#isActiveCheckbox', true);
      cy.clickItem('#EnablePagination', true);

      cy.clickItem(autoCompleteFields.sparqlEndpoint, true);
      cy.clickItem(configRelatics.sparqlEndpoint);

      cy.clickItem(autoCompleteFields.workspace, true);
      cy.clickItem(configRelatics.workspace);
      cy.checkVisibility('RelaticsImgSearch', true);

      cy.clickItem(autoCompleteFields.sparqlRootsQuery, true);
      cy.clickItem(configRelatics.sparqlRootsQuery);

      cy.typeText('#DefaultGraphsRoot', graph);

      cy.clickItem(autoCompleteFields.additionalInput, true);
      cy.clickItem(configRelatics.additionalInput);
      cy.checkItemValue(
        autoCompleteFields.additionalInput,
        'value',
        configRelatics.additionalInputName
      );

      configRelatics.columns.map((columnName, index) => {
        cy.checkVisibility(`Column_${columnName}_${index}`, true);
      });

      cy.checkColumnItems({
        columnsLength: 5,
        visible: 5,
        notVisible: 0
      });

      cy.typeText('#uuid', 'ID');

      cy.clickItem('Add_Step');

      cy.typeText('#StepName_0', configRelatics.importSteps[0].name);
      cy.clickItem(`[data-testid=DND-Step0] ${autoCompleteFields.query}`, true);
      cy.clickItem(configRelatics.importSteps[0].query);
      cy.typeText('#DefaultGraphs_0', graph);
      cy.clickItem(
        `[data-testid=DND-Step0] ${autoCompleteFields.target}`,
        true
      );
      cy.clickItem(configRelatics.importSteps[0].importTarget);

      cy.clickItem('Add_Step');

      cy.typeText('#StepName_1', configRelatics.importSteps[1].name);

      cy.clickItem(`[data-testid=DND-Step1] ${autoCompleteFields.query}`, true);
      cy.clickItem(configRelatics.importSteps[1].query);
      cy.typeText('#DefaultGraphs_1', graph);
      cy.clickItem(
        `[data-testid=DND-Step1] ${autoCompleteFields.target}`,
        true
      );
      cy.clickItem(configRelatics.importSteps[1].importTarget);

      cy.clickItem('Add_Step');

      cy.dragAndDrop({
        selector: 'DND-Step0',
        current: {
          x: 527,
          y: 822
        },
        destination: {
          x: 527,
          y: 900
        }
      });

      cy.clickItem('FinishAddButton');
    });

    it('Check if create dialog appears', () => {
      cy.checkVisibility('AlertTitle', true);
      cy.checkVisibility('AlertDescription', true);
      cy.checkValueText(
        'AlertDescription',
        'Add the newly created Relatics configuration'
      );
    });

    it('Choose Yes', () => {
      cy.clickItem('AlertYesButton');
    });

    it('Check if card is added in the overview and get the id of it', () => {
      cy.getHeaderCard().then(item => {
        expect(item.text()).to.equals(configRelatics.name);
        configRelaticsId = item.attr('data-testid').split('_')[1];
      });
    });

    it('Log the id of the created relatics configuration', () => {
      cy.log('relatics config id is', configRelaticsId);
    });

    it('Check if card is added in the overview', () => {
      cy.checkExistence(`Card_configurations_${configRelaticsId}`, true);
    });

    it('Click on the action button and pick edit again', () => {
      cy.clickItem(`CardThreeDots_${configRelaticsId}`);
      cy.clickItem('MenuItemEdit');
    });

    it('Check the updated values in the edit page', () => {
      cy.checkItemValue('#inputName', 'value', configRelatics.name);
      cy.checkItemValue(
        '#inputDescription',
        'value',
        configRelatics.description
      );
      cy.checkItemValue(
        '#inputDisplayName',
        'value',
        configRelatics.displayName
      );
      cy.checkDateValue('ConfigDateStart', configRelatics.startDate);
      cy.checkDateValue('ConfigDateEnd', configRelatics.endDate);
      cy.checkItemValue('#isActiveCheckbox', 'checked', true);
      cy.checkItemValue('#EnablePagination', 'checked', true);
      cy.checkItemValue('#DefaultGraphsRoot', 'value', graphNoSpaces);

      cy.checkItemValue(
        autoCompleteFields.sparqlEndpoint,
        'value',
        configRelatics.sparqlEndpointName
      );
      cy.checkItemValue(
        autoCompleteFields.workspace,
        'value',
        configRelatics.workspaceName
      );
      cy.checkItemValue(
        autoCompleteFields.sparqlRootsQuery,
        'value',
        configRelatics.sparqlRootsQueryName
      );
      cy.checkItemValue(
        autoCompleteFields.additionalInput,
        'value',
        configRelatics.additionalInputName
      );

      configRelatics.columns.map((columnName, index) => {
        cy.checkVisibility(`Column_${columnName}_${index}`, true);
      });

      cy.checkColumnItems({
        columnsLength: 5,
        visible: 5,
        notVisible: 0
      });

      cy.checkItemValue('#uuid', 'value', 'ID');

      // Here I am checking if the DnD worked good by checking the values of the fields
      cy.checkItemValue(
        '#StepName_0',
        'value',
        configRelatics.importSteps[1].name
      );
      cy.checkItemValue('#DefaultGraphs_0', 'value', graphNoSpaces);
      cy.checkItemValue(
        `[data-testid=DND-Step0] ${autoCompleteFields.query}`,
        'value',
        configRelatics.importSteps[1].queryName
      );
      cy.checkItemValue(
        `[data-testid=DND-Step0] ${autoCompleteFields.target}`,
        'value',
        configRelatics.importSteps[1].importTargetName
      );

      cy.checkItemValue(
        '#StepName_1',
        'value',
        configRelatics.importSteps[0].name
      );
      cy.checkItemValue('#DefaultGraphs_1', 'value', graphNoSpaces);
      cy.checkItemValue(
        `[data-testid=DND-Step1] ${autoCompleteFields.query}`,
        'value',
        configRelatics.importSteps[0].queryName
      );
      cy.checkItemValue(
        `[data-testid=DND-Step1] ${autoCompleteFields.target}`,
        'value',
        configRelatics.importSteps[0].importTargetName
      );

      cy.clickItem('CancelButton');
    });
  });

  describe('Create Json API Configurations', () => {
    const configJsonApi = {
      name: '001 LACES Json Api Configuration Cypress',
      displayName: 'Cypress 001 laces fetch',
      startDate: formattedDate(new Date()),
      endDate: '',
      description: 'This is a test description for cypress.',
      jsonApi: '123jsonapi',
      jsonApiName: 'TEST json api 001',
      sparqlEndpoint: 'endpoint456',
      sparqlEndpointName: 'Endpoint 002',
      sparqlRootsQuery: 'abcde',
      sparqlRootsQueryName: 'Roots cypress query',
      additionalInput: 'O24',
      additionalInputName: 'Endpoint members',
      importSteps: [
        {
          importTarget: 'O23',
          importTargetName: 'Endpoint documents',
          name: 'Step 1',
          query: '123789',
          queryName: 'CYPRESS_003'
        },
        {
          importTarget: 'O23',
          importTargetName: 'Endpoint documents',
          name: 'Step 2',
          query: '67890',
          queryName: 'CYPRESS_002'
        }
      ]
    };

    it('Check if the start route is configurations', () => {
      cy.getCookie(testConfig.sessionId).should('exist');
    });

    it('Click on the create button', () => {
      cy.clickItem('PlusButton');
    });

    it('Click on Json api (create config for Neanex)', () => {
      cy.clickItem('MenuItemJsonAPI');
    });

    it('Check if the create page for the configurations appears', () => {
      cy.checkValueText('PageHeaderTitle', 'New JSON API Configuration');

      cy.checkVisibility('CancelButton', true);
      cy.checkVisibility('FinishAddButton', true);

      cy.checkItemValue('#inputName', 'value', '');
      cy.checkItemValue('#inputDescription', 'value', '');
      cy.checkItemValue('#inputDisplayName', 'value', '');
      cy.checkExistence('ConfigDateStart', true);
      cy.checkExistence('ConfigDateEnd', true);

      cy.checkItemValue('#isActiveCheckbox', 'checked', false);
      cy.checkItemValue(autoCompleteFields.sparqlEndpoint, 'value', '');
      cy.checkItemValue(autoCompleteFields.sparqlRootsQuery, 'value', '');
      cy.checkItemValue(autoCompleteFields.sparqlChildrenQuery, 'value', '');
      cy.checkItemValue(autoCompleteFields.additionalInput, 'value', '');
      cy.checkItemValue(autoCompleteFields.jsonApi, 'value', '');
      cy.checkExistence('DND-Step0', false);

      cy.checkItemValue('#DefaultGraphsRoot', 'value', '');
      cy.checkItemValue('#DefaultGraphsChildren', 'value', '');
    });

    it('Fill in the information', () => {
      cy.typeText('#inputName', configJsonApi.name);
      cy.typeText('#inputDescription', configJsonApi.description);
      cy.typeText('#inputDisplayName', configJsonApi.displayName);

      cy.clickItem('#isActiveCheckbox', true);
      cy.clickItem('#EnablePagination', true);

      cy.clickItem(autoCompleteFields.sparqlEndpoint, true);
      cy.clickItem(configJsonApi.sparqlEndpoint);

      cy.clickItem(autoCompleteFields.jsonApi, true);
      cy.clickItem(configJsonApi.jsonApi);
      cy.checkVisibility('NeanexImgSearch', true);

      cy.clickItem(autoCompleteFields.sparqlRootsQuery, true);
      cy.clickItem(configJsonApi.sparqlRootsQuery);

      cy.typeText('#DefaultGraphsRoot', graph);

      cy.clickItem(autoCompleteFields.additionalInput, true);
      cy.clickItem(configJsonApi.additionalInput);
      cy.checkItemValue(
        autoCompleteFields.additionalInput,
        'value',
        configJsonApi.additionalInputName
      );

      cy.clickItem('Add_Step');

      cy.typeText('#StepName_0', configJsonApi.importSteps[0].name);
      cy.clickItem(`[data-testid=DND-Step0] ${autoCompleteFields.query}`, true);
      cy.clickItem(configJsonApi.importSteps[0].query);

      cy.typeText('#DefaultGraphs_0', graph);
      cy.clickItem(
        `[data-testid=DND-Step0] ${autoCompleteFields.target}`,
        true
      );
      cy.clickItem(configJsonApi.importSteps[0].importTarget);

      cy.clickItem('Add_Step');

      cy.typeText('#StepName_1', configJsonApi.importSteps[1].name);

      cy.clickItem(`[data-testid=DND-Step1] ${autoCompleteFields.query}`, true);
      cy.clickItem(configJsonApi.importSteps[1].query);

      cy.typeText('#DefaultGraphs_1', graph);

      cy.clickItem(
        `[data-testid=DND-Step1] ${autoCompleteFields.target}`,
        true
      );
      cy.clickItem(configJsonApi.importSteps[1].importTarget);

      cy.clickItem('Add_Step');

      cy.dragAndDrop({
        selector: 'DND-Step0',
        current: {
          x: 527,
          y: 822
        },
        destination: {
          x: 527,
          y: 900
        }
      });

      cy.clickItem('FinishAddButton');
    });

    it('Check if create dialog appears', () => {
      cy.checkVisibility('AlertTitle', true);
      cy.checkVisibility('AlertDescription', true);
      cy.checkValueText(
        'AlertDescription',
        'Add the newly created JSON API configuration'
      );
    });

    it('Choose Yes', () => {
      cy.clickItem('AlertYesButton');
    });

    it('Check if card is added in the overview and get the id of it', () => {
      cy.getHeaderCard().then(item => {
        expect(item.text()).to.equals(configJsonApi.name);
        configJsonApiId = item.attr('data-testid').split('_')[1];
      });
    });

    it('Log the id of the created neanex configuration', () => {
      cy.log('neanex config id is', configJsonApiId);
    });

    it('Check if card is added in the overview', () => {
      cy.checkExistence(`Card_configurations_${configJsonApiId}`, true);
    });

    it('Click on the action button and pick edit again', () => {
      cy.clickItem(`CardThreeDots_${configJsonApiId}`);
      cy.clickItem('MenuItemEdit');
    });

    it('Check the updated values in the edit page', () => {
      cy.checkItemValue('#inputName', 'value', configJsonApi.name);
      cy.checkItemValue(
        '#inputDescription',
        'value',
        configJsonApi.description
      );
      cy.checkItemValue(
        '#inputDisplayName',
        'value',
        configJsonApi.displayName
      );
      cy.checkDateValue('ConfigDateStart', configJsonApi.startDate);
      cy.checkDateValue('ConfigDateEnd', configJsonApi.endDate);
      cy.checkItemValue('#isActiveCheckbox', 'checked', true);
      cy.checkItemValue('#EnablePagination', 'checked', true);
      cy.checkItemValue('#DefaultGraphsRoot', 'value', graphNoSpaces);
      cy.checkItemValue(
        autoCompleteFields.sparqlEndpoint,
        'value',
        configJsonApi.sparqlEndpointName
      );
      cy.checkItemValue(
        autoCompleteFields.jsonApi,
        'value',
        configJsonApi.jsonApiName
      );
      cy.checkItemValue(
        autoCompleteFields.sparqlRootsQuery,
        'value',
        configJsonApi.sparqlRootsQueryName
      );
      cy.checkItemValue(
        autoCompleteFields.additionalInput,
        'value',
        configJsonApi.additionalInputName
      );

      // Here I am checking if the DnD worked good by checking the values of the fields
      cy.checkItemValue(
        '#StepName_0',
        'value',
        configJsonApi.importSteps[1].name
      );
      cy.checkItemValue('#DefaultGraphs_0', 'value', graphNoSpaces);
      cy.checkItemValue(
        `[data-testid=DND-Step0] ${autoCompleteFields.query}`,
        'value',
        configJsonApi.importSteps[1].queryName
      );
      cy.checkItemValue(
        `[data-testid=DND-Step0] ${autoCompleteFields.target}`,
        'value',
        configJsonApi.importSteps[1].importTargetName
      );

      cy.checkItemValue(
        '#StepName_1',
        'value',
        configJsonApi.importSteps[0].name
      );
      cy.checkItemValue('#DefaultGraphs_1', 'value', graphNoSpaces);
      cy.checkItemValue(
        `[data-testid=DND-Step1] ${autoCompleteFields.query}`,
        'value',
        configJsonApi.importSteps[0].queryName
      );
      cy.checkItemValue(
        `[data-testid=DND-Step1] ${autoCompleteFields.target}`,
        'value',
        configJsonApi.importSteps[0].importTargetName
      );

      cy.clickItem('CancelButton');
    });
  });
});
