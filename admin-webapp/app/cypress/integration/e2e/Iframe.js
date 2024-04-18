import { testConfig } from '../../utils';
import AddTestData from '../../utils/bootstrap/AddTestData';
import DeleteTestData from '../../utils/bootstrap/DeleteTestData';

describe('Admin UI generated code for iframe and check visibility generate button', () => {
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

  it('Check cookie', () => {
    cy.getCookie(testConfig.sessionId).should('exist');
  });

  it('Click on menu list endpoints', () => {
    cy.clickItem(testConfig.menuItems.endpoints);
  });

  it('Click on menu list configurations', () => {
    cy.clickItem(testConfig.menuItems.configurations);
  });

  it('Click on the create button', () => {
    cy.clickItem('PlusButton');
  });

  it('Click on Relatics', () => {
    cy.clickItem('MenuItemRelatics');
  });

  it('Check if button generate is not visible', () => {
    cy.checkExistence('GenerateButton', false);
    cy.clickItem('CancelButton');
  });

  it('Check if the config is there', () => {
    cy.checkExistence('Card_configurations_12345', true);
  });

  it('Click on the action button for first card and pick edit', () => {
    cy.clickItem('CardThreeDots_12345');
    cy.clickItem('MenuItemEdit');
  });

  it('Check if the edit page for the relatics configuration appears', () => {
    cy.checkValueText('PageHeaderTitle', 'Details Relatics Configuration');
    cy.checkExistence('GenerateButton', true);
  });

  it('Click on generate', () => {
    cy.clickItem('GenerateButton');
  });

  it('Check toast is appearing', () => {
    cy.checkVisibility('.Toastify__toast--success', true, true);
  });

  it('Click on cancel', () => {
    cy.clickItem('CancelButton');
  });
  // For now it is not possible to check the content of the clipboard in cypress (They are gonna add this later, but don't know when)
});
