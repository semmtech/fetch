import DeleteTestData from '../utils/bootstrap/DeleteTestData';
// ***********************************************************
// This example support/index.js is processed and
// loaded automatically before your test files.
//
// This is a great place to put global configuration and
// behavior that modifies Cypress.
//
// You can change the location of this file or turn off
// automatically serving support files with the
// 'supportFile' configuration option.
//
// You can read more here:
// https://on.cypress.io/configuration
// ***********************************************************

// Import commands.js using ES2015 syntax:
import './commands';

beforeEach(function() {
  Cypress.Cookies.preserveOnce('JSESSIONID');
});

// This is for stopping the end2end tests when there is a failed one
afterEach(function() {
  if (this.currentTest.state === 'failed') {
    DeleteTestData();
    Cypress.runner.stop();
  }
});
