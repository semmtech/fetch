// ***********************************************
// This example commands.js shows you how to
// create various custom commands and overwrite
// existing commands.
//
// For more comprehensive examples of custom
// commands please read more here:
// https://on.cypress.io/custom-commands
// ***********************************************
//
//
// -- This is a parent command --
// Cypress.Commands.add("login", (email, password) => { ... })
//
//
// -- This is a child command --
// Cypress.Commands.add("drag", { prevSubject: 'element'}, (subject, options) => { ... })
//
//
// -- This is a dual command --
// Cypress.Commands.add("dismiss", { prevSubject: 'optional'}, (subject, options) => { ... })
//
//
// -- This will overwrite an existing command --
// Cypress.Commands.overwrite("visit", (originalFn, url, options) => { ... })

Cypress.Commands.add('checkVisibility', (testId, visible, noTestId) => {
  cy.get(noTestId ? testId : `[data-testid=${testId}]`).should(
    visible ? 'be.visible' : 'not.be.visible'
  );
});

Cypress.Commands.add('checkCSS', (testId, shouldHave, key, value) => {
  cy.get(`[data-testid=${testId}]`).should(
    shouldHave ? 'have.css' : 'not.have.css',
    key,
    value
  );
});

Cypress.Commands.add('checkURL', (pathname, hash) => {
  hash
    ? cy.location('hash').should('eq', pathname)
    : cy.location().should(loc => {
        expect(loc.pathname).to.eq(pathname);
      });
});

Cypress.Commands.add('clickItem', (testId, noTestId) => {
  cy.get(noTestId ? testId : `[data-testid=${testId}]`).click();
});

Cypress.Commands.add('clickFirstItem', testId => {
  cy.get(`[data-testid=${testId}]`)
    .first()
    .click();
});

Cypress.Commands.add('clickAnyItem', (testId, amount, noTestId) => {
  cy.get(noTestId ? testId : `[data-testid=${testId}]`)
    .eq(amount)
    .click();
});

Cypress.Commands.add('checkDateValue', (testId, value) => {
  cy.get(
    `[data-testid=${testId}] > .MuiInputBase-root > .MuiInputBase-input`
  ).then(item => {
    expect(item[0].value).to.equals(value);
  });
});

Cypress.Commands.add('checkSelectedValue', (testId, value) => {
  cy.get(`[data-testid=${testId}] > .MuiInputBase-root > .MuiSelect-root`).then(
    item => {
      expect(item[0].textContent).to.equals(value);
    }
  );
});

Cypress.Commands.add('checkValueText', (testId, value, noTestId) => {
  cy.get(noTestId ? testId : `[data-testid=${testId}]`).then(item => {
    expect(item.text()).to.equals(value);
  });
});

Cypress.Commands.add('checkItemValue', (testId, key, value) => {
  cy.get(testId).should($item => {
    expect($item[0][key]).to.equal(value);
  });
});

Cypress.Commands.add('checkExistence', (testId, exist, noTestId) => {
  cy.get(noTestId ? testId : `[data-testid=${testId}]`).should($item => {
    exist ? expect($item).to.exist : expect($item).not.to.exist;
  });
});

Cypress.Commands.add('typeText', (testId, value) => {
  cy.get(testId)
    .clear()
    .type(value);
});

Cypress.Commands.add('typeYasguiEditor', (testId, index, value) => {
  cy.get(testId)
    .eq(index)
    .type(value, {
      force: true
    });
});

Cypress.Commands.add('checkSelectFields', data => {
  data.forEach(item => {
    cy.checkExistence(item, true);
  });
});

Cypress.Commands.add('addFilter', (firstClick, secondClick, value) => {
  cy.clickItem(firstClick);

  cy.typeText('#value', value);

  cy.clickItem(secondClick);
});

Cypress.Commands.add('login', (userName, password) => {
  cy.typeText('#userName', userName);
  cy.typeText('#password', password);
  cy.clickItem('LoginButton');
  cy.wait(1000);
});

Cypress.Commands.add('logout', () => {
  cy.clickItem('logoutButton');
  cy.wait(1000);
  cy.url().should('include', '?logout');
});

Cypress.Commands.add('dragAndDrop', ({ selector, current, destination }) => {
  const triggerObject = (x, y) => ({
    button: 0,
    pageX: x,
    pageY: y,
    clientX: x,
    clientY: y
  });

  cy.clock();
  cy.get(`[data-testid=${selector}]`)
    .trigger('mousedown', triggerObject(current.x, current.y))
    .trigger('mousemove', triggerObject(current.x, current.y + 8))
    .trigger('mousemove', {
      ...triggerObject(destination.x, destination.y),
      force: true
    })
    .tick(1000);
  cy.get(`[data-testid=${selector}]`).trigger('mouseup', {
    ...triggerObject(destination.x, destination.y),
    force: true
  });
  cy.wait(1000);
});

Cypress.Commands.add('getHeaderCard', testId => {
  cy.wait(1000);
  testId !== undefined
    ? cy
        .get(`[data-testid=${testId}]`)
        .find('h3')
        .eq(0)
    : cy.get('h3').eq(0);
});

Cypress.Commands.add(
  'checkColumnItems',
  ({ columnsLength, visible, notVisible }) => {
    cy.get('[data-testid="Visible"]').should('have.length', visible);
    cy.get('[data-testid="VisibleOff"]').should('have.length', notVisible);
    cy.get('[data-testid="DragIcon"]').should('have.length', columnsLength);
    cy.get('[data-testid="Chip"]').should('have.length', columnsLength);
  }
);
