// ***********************************************
// This example commands.js shows you how to
// create various custom commands and overwrite
// existing commands.
//
// For more comprehensive examples of custom
// commands please read more here:
// https://on.cypress.io/custom-commands
// ***********************************************
import { colors } from '.././../src/utils/colors';
import { options, config } from '../utils';

Cypress.Commands.add('setRoutes', ({ configId }) => {
  cy.route(
    options({
      url: `/api/visualization/roots?configurationId=${configId}`,
      response: 'fixture:rootsColumnsLabel'
    })
  ).as('getRoots');
  cy.route(
    options({
      url: `/api/visualization/children?configurationId=${configId}`,
      response: 'fixture:children'
    })
  ).as('getChildren');
  cy.route(
    options({
      url: `/api/import/?configurationId=${configId}`,
      response: 'fixture:imports'
    })
  ).as('imports');
});

Cypress.Commands.add(
  'checkVisibility',
  ({ testId, visible, isDataTestIdPresent = true }) => {
    cy.get(isDataTestIdPresent ? `[data-testid=${testId}]` : testId).should(
      visible ? 'be.visible' : 'not.be.visible'
    );
  }
);

Cypress.Commands.add('checkTableCells', ({ testId, visible, length }) => {
  cy.checkVisibility({ testId, visible }).should(cell => {
    expect(cell).to.have.lengthOf(length);
  });
});

Cypress.Commands.add('checkIncludes', ({ testId, selectors }) => {
  cy.get(testId).should(item => {
    if (Array.isArray(selectors))
      selectors.map(selector => {
        expect(item.text().includes(selector)).to.be.true;
      });
    else expect(item.text().includes(selectors)).to.be.true;
  });
});

Cypress.Commands.add('clickItem', ({ testId, isDataTestIdPresent = true }) => {
  cy.get(isDataTestIdPresent ? `[data-testid=${testId}]` : testId).click();
});

Cypress.Commands.add('clickItemForce', ({ testId }) => {
  cy.get(`[data-testid=${testId}]`).click({ force: true });
});

Cypress.Commands.add('checkSelectedColor', ({ testId, check }) => {
  cy.get(`[data-testid=${testId}]`).should(
    check ? 'have.css' : 'not.have.css',
    'background-color',
    colors.silver
  );
});

Cypress.Commands.add(
  'checkDisabled',
  ({ testId, disabled, isDataTestIdPresent = true }) => {
    cy.get(isDataTestIdPresent ? `[data-testid=${testId}]` : testId).should(
      disabled ? 'be.disabled' : 'not.be.disabled'
    );
  }
);

Cypress.Commands.add('checkExisting', ({ testId, exist }) => {
  cy.get(`[data-testid=${testId}]`).should(exist ? 'exist' : 'not.exist');
});

Cypress.Commands.add(
  'checkValueText',
  ({ testId, value, isDataTestIdPresent = true }) => {
    cy.get(isDataTestIdPresent ? `[data-testid=${testId}]` : testId).then(
      item => {
        expect(item.text()).to.equals(value);
      }
    );
  }
);

Cypress.Commands.add('checkPagination', ({ previous, next, content }) => {
  cy.checkDisabled({ testId: config.firstPage, disabled: previous });
  cy.checkDisabled({ testId: config.previousPage, disabled: previous });
  cy.checkDisabled({ testId: config.nextPage, disabled: next });
  cy.checkDisabled({ testId: config.lastPage, disabled: next });
  cy.checkValueText({ testId: config.totalText, value: content });
});

Cypress.Commands.add('checkItemsLength', ({ testId, length }) => {
  cy.get(testId).should(item => {
    expect(item).to.have.lengthOf(length);
  });
});

Cypress.Commands.add('checkMultipleValues', ({ index, value }) => {
  cy.get(config.expanderText).eq(index).should('contain', value);
});

Cypress.Commands.add('CheckFirstCell', ({ testId, equals }) => {
  cy.get(`[data-testid=${testId}]`)
    .first()
    .then(item => {
      equals
        ? expect(item.text()).to.equals('...')
        : expect(item.text()).to.not.equal('...');
    });
});

Cypress.Commands.add('CheckLastCell', ({ testId, equals }) => {
  cy.get(`[data-testid=${testId}]`)
    .last()
    .then(item => {
      equals
        ? expect(item.text()).to.equals('...')
        : expect(item.text()).to.not.equal('...');
    });
});

// FILTER and CELLMENU commands

Cypress.Commands.add('VisitAddon', ({ configId }) => {
  cy.route(
    options({
      url: `/api/visualization/roots?configurationId=${configId}`,
      response: 'fixture:rootsColumnsFilters'
    })
  ).as('getRootsFilters');

  cy.visit(`/add-on/index.html?configurationId=${configId}`);
});

Cypress.Commands.add('TypeText', ({ testid, value }) => {
  cy.get(testid).clear().type(value);
});

Cypress.Commands.add('CheckAllChildrenExist', ({ row, amount }) => {
  let me = row;
  for (var i = 0; i < amount; i++) {
    cy.checkExisting({
      testId: `'${me}_MenuButton'`,
      exist: true
    });
    me = `${me}.1`;
  }
});

Cypress.Commands.add('GetSelectedAmountOfChildren', ({ testId, selecting }) => {
  cy.get(`[data-testid=${testId}]`).then(item => {
    const amountChildren = item.text();
    if (selecting) {
      const amountOfSelectedIds = amountChildren.substring(4, 7);
      expect(amountChildren).to.include(' children)');
      expect(Number(amountOfSelectedIds)).to.be.greaterThan(10);
    } else {
      expect(amountChildren).to.equals('');
    }
  });
});

// With this 150ms time i can get children 10 times (-> to test a nested tree)
// After the wait is over it goes to last mocked call where I have foreseen that there are no more children so the fetching will stop
Cypress.Commands.add(
  'MockNestedChildren',
  ({ menuItem, configId, menuButton }) => {
    cy.route(
      options({
        url: `/api/visualization/children?configurationId=${configId}`,
        response: 'fixture:childrenLevel'
      })
    ).as('getChildren');

    cy.clickItem({ testId: menuButton });
    cy.clickItem({ testId: menuItem });

    cy.wait(150);
    cy.wait('@getChildren').then(() => {
      cy.route(
        options({
          url: `/api/visualization/children?configurationId=${configId}`,
          response: 'fixture:childrenWithoutChildren'
        })
      ).as('getChildren');
    });
  }
);
