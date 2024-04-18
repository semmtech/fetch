import { testConfig } from '../../utils';

describe('Authentication User flow', () => {
  it('Visit the application', () => {
    cy.visit(testConfig.adminURL);
  });

  it('Check if the start page is the authentication page', () => {
    cy.checkURL(testConfig.authenticationURL, false);
  });

  it('Login in the app with false credentials', () => {
    cy.login(testConfig.userName, 'wel1');
  });

  it('Check if the url includes ?error', () => {
    cy.url().should('include', '?error');
  });

  it('Check if feedback message contains the right text', () => {
    cy.checkValueText(
      'feedbackMessage',
      'Login was unsuccessfull, please try again.'
    );
  });

  it('Login in the app with right credentials', () => {
    cy.login(testConfig.userName, testConfig.password);
  });

  it('Check if the menu and the application bar are there', () => {
    cy.getCookie(testConfig.sessionId).should('exist');

    cy.checkVisibility('logoutButton', true);
    cy.checkVisibility('applicationTitle', true);
    cy.checkVisibility('menuButton', true);

    cy.checkVisibility(testConfig.menuItems.configurations, true);
    cy.checkVisibility(testConfig.menuItems.endpoints, true);
    cy.checkVisibility(testConfig.menuItems.queries, true);
    cy.checkVisibility(testConfig.menuItems.relatics, true);
  });

  it('Logout', () => {
    cy.logout();
  });

  it('Check if feedback message contains the right text', () => {
    cy.checkValueText(
      'feedbackMessage',
      'You have been logged out, please login again if you want to use the application.'
    );
  });
});
