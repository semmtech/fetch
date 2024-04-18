import { testConfig } from '../../utils';
import AddTestData from '../../utils/bootstrap/AddTestData';
import DeleteTestData from '../../utils/bootstrap/DeleteTestData';
import { autoCompleteFields } from '../../utils';

describe('Admin UI work flow of select field with search', () => {
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

  it('Click on the create button', () => {
    cy.clickItem('PlusButton');
  });

  it('Click on json api', () => {
    cy.clickItem('MenuItemJsonAPI');
  });

  it('Open select and test search functionality', () => {
    cy.clickItem(autoCompleteFields.sparqlEndpoint, true);

    cy.get(autoCompleteFields.sparqlEndpoint)
      .clear()
      .type('Endpoint');

    cy.checkSelectFields(['endpoint123', 'endpoint456', 'endpoint159753']);

    cy.clickItem('endpoint456');
  });

  it('Cancel the creation', () => {
    cy.clickItem('CancelButton');
  });
});
