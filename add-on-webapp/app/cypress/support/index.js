import './commands';

let polyfill;
before(() => {
  const polyfillUrl = 'https://unpkg.com/unfetch@4.1.0/dist/unfetch.umd.js';
  cy.request(polyfillUrl).then(response => {
    polyfill = response.body;
  });
});

Cypress.on('window:before:load', win => {
  delete win.fetch;
  win.eval(polyfill);
  win.fetch = args => {
    if (typeof args === 'string') {
      return win.unfetch(args, { method: 'POST', credentials: 'include' });
    }
    if (typeof args === 'object' && args !== null) {
      const { url, ...rest } = args;
      return win.unfetch(url, {
        method: 'POST',
        credentials: 'include',
        ...rest
      });
    }
    console.error('Invalid request');
  };
});

// This is for stopping the end2end tests when there is a failed one
afterEach(function() {
  if (this.currentTest.state === 'failed') {
    Cypress.runner.stop();
  }
});
