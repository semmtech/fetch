import { colors, testConfig } from '../../utils';

describe('Admin UI Menu First render', () => {
  before(() => {
    cy.visit(testConfig.adminURL);
    cy.login(testConfig.userName, testConfig.password);
  });

  after(() => {
    cy.logout();
  });

  it('Check if the start route is configurations', () => {
    cy.getCookie(testConfig.sessionId).should('exist');
    cy.clickItem(testConfig.menuItems.configurations);
    cy.checkURL(testConfig.urls.configurations, true);
  });

  it('Check if the menu and the application bar are there', () => {
    cy.checkVisibility('logoutButton', true);
    cy.checkVisibility('applicationTitle', true);
    cy.checkVisibility('menuButton', true);

    cy.checkVisibility('listItemIcon_Configurations', true);
    cy.checkVisibility('"listItemIcon_SPARQL endpoints"', true);
    cy.checkVisibility('"listItemIcon_SPARQL queries"', true);
    cy.checkVisibility('listItemIcon_Relatics', true);
    cy.checkVisibility('listItemIcon_Neanex', true);

    cy.checkVisibility('listItemText_Configurations', false);
    cy.checkVisibility('"listItemText_SPARQL endpoints"', false);
    cy.checkVisibility('"listItemText_SPARQL queries"', false);
    cy.checkVisibility('listItemText_Relatics', false);
    cy.checkVisibility('listItemText_Neanex', false);

    cy.checkValueText('PageHeaderTitle', 'Configurations');
  });

  it('Check width of the menu', () => {
    cy.checkCSS('menuList', true, 'width', '50px');
  });

  it('Check if the selected one has the right css', () => {
    cy.checkCSS(
      testConfig.menuItems.configurations,
      true,
      'background-color',
      colors.yellow
    );
    cy.checkCSS('listItemIcon_Configurations', true, 'color', colors.black);
  });

  it('Click on another menu item', () => {
    cy.clickItem(testConfig.menuItems.endpoints);
  });

  it('Check if the selected one has the right background color and the other one does not', () => {
    cy.checkCSS(
      testConfig.menuItems.endpoints,
      true,
      'background-color',
      colors.yellow
    );
    cy.checkCSS(
      testConfig.menuItems.configurations,
      false,
      'background-color',
      colors.yellow
    );
  });

  it('Check the page header changed', () => {
    cy.checkValueText('PageHeaderTitle', 'SPARQL Endpoints');
  });

  it('Check if the route has changed', () => {
    cy.checkURL(testConfig.urls.endpoints, true);
  });

  it('Click on the menu button', () => {
    cy.clickItem('menuButton');
  });

  it('Check width of the menu', () => {
    cy.checkCSS('menuList', true, 'width', '250px');
  });

  it('Check if the menu is visible with the text', () => {
    cy.checkVisibility('listItemIcon_Configurations', true);
    cy.checkVisibility('"listItemIcon_SPARQL endpoints"', true);
    cy.checkVisibility('"listItemIcon_SPARQL queries"', true);
    cy.checkVisibility('listItemIcon_Relatics', true);
    cy.checkVisibility('listItemIcon_Neanex', true);

    cy.checkVisibility('listItemText_Configurations', true);
    cy.checkVisibility('"listItemText_SPARQL endpoints"', true);
    cy.checkVisibility('"listItemText_SPARQL queries"', true);
    cy.checkVisibility('listItemText_Relatics', true);
    cy.checkVisibility('listItemText_Neanex', true);
  });

  it('Check if the selected one has the right menu text styling', () => {
    cy.checkCSS('"listItemText_SPARQL endpoints"', true, 'color', colors.black);
  });

  it('Click on the menu button', () => {
    cy.clickItem('menuButton');
  });

  it('Check width of the menu', () => {
    cy.checkCSS('menuList', true, 'width', '50px');
  });
});
