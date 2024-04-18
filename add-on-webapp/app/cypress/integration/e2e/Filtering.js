import { config, data, options } from '../../utils';

const rowItem = 'DienstGebouw';
const filteredValues = [
  ({
    uri: 'Item1',
    children: false
  },
  {
    uri: 'Item2',
    children: true
  })
];
const orderedFilterValues = [
  'DienstGebouw',
  'Garage',
  'GarageFive',
  'GarageFour',
  'GarageThree',
  'GarageTwo',
  'Item'
];

describe('Add-on UI filtering with text filter', () => {
  beforeEach(() => {
    cy.server();
  });

  it('Visit the add-on', () => {
    cy.VisitAddon({ configId: data.configurationId });
  });

  it(`Expand "${rowItem}" and fill in text filter`, () => {
    cy.route(
      options({
        url: `/api/visualization/children?configurationId=${data.configurationId}`,
        response: 'fixture:children'
      })
    ).as('getChildren');
    cy.clickItem({ testId: config.right + rowItem });
    cy.clickItem({
      testId: config.filterButton
    });
    cy.TypeText({ testid: '#Filter_filterLabel', value: 'Item' });
  });

  it('Active the text filter', () => {
    cy.route(
      options({
        url: `/api/visualization/roots?configurationId=${data.configurationId}`,
        response: 'fixture:rootsFilteredValues'
      })
    ).as('getFilteredValues');

    cy.clickItem({
      testId: config.addFilterButton
    });
  });

  it('Check if the filter has changed the roots data (bug catch)', () => {
    filteredValues.map(({ uri, children }) => {
      cy.checkVisibility({
        testId: config.tableRow + uri,
        visible: true
      });
      // This part is for catching a bug that was happening when you expand the item before the filtered values where shown
      if (children)
        cy.checkVisibility({
          testId: config.right + uri,
          visible: true
        });
      else
        cy.checkExisting({
          testId: config.right + uri,
          exist: false
        });
    });

    cy.checkExisting({
      testId: `${config.tableRow}Garage`,
      exist: false
    });
  });

  it('Check fields are not disabled', () => {
    cy.clickItem({
      testId: config.filterButton
    });

    cy.checkDisabled({
      testId: '#SPARQL\\ filter',
      disabled: false,
      isDataTestIdPresent: false
    });
    cy.checkDisabled({
      testId: '#Filter_filterLabel',
      disabled: false,
      isDataTestIdPresent: false
    });
  });
});

describe('Add-on UI filtering with SPARQL filter', () => {
  beforeEach(() => {
    cy.server();
  });

  it('Visit the add-on', () => {
    cy.VisitAddon({ configId: data.configurationId });
  });

  it(`Expand "${rowItem}" and open filter dialog`, () => {
    cy.route(
      options({
        url: `/api/visualization/children?configurationId=${data.configurationId}`,
        response: 'fixture:children'
      })
    ).as('getChildren');
    cy.clickItem({ testId: config.right + rowItem });
    cy.clickItem({
      testId: config.filterButton
    });
  });

  it('Open the dropdown', () => {
    cy.route(
      options({
        url: `/api/visualization/filtervalues?configurationId=${data.configurationId}&queryId=12345`,
        response: 'fixture:filterValues'
      })
    ).as('getFilterValues');

    cy.clickItem({
      testId: '#SPARQL\\ filter',
      isDataTestIdPresent: false
    });
    cy.wait('@getFilterValues');
    for (var i = 0; i < 7; i++) {
      cy.checkVisibility({
        testId: `#SPARQL\\ filter-option-${i}`,
        visible: true,
        isDataTestIdPresent: false
      });
    }
  });

  it('Check if the filter values are sorted', () => {
    orderedFilterValues.map((item, index) => {
      cy.get(`#SPARQL\\ filter-option-${index}`).should('contain', item);
    });
  });

  it('Check if the search field works', () => {
    cy.TypeText({ testid: '#SPARQL\\ filter', value: 'It' });

    for (var i = 0; i < 7; i++) {
      cy.checkVisibility({
        testId: `#SPARQL\\ filter-option-${i}`,
        visible: i === 0 ? true : false,
        isDataTestIdPresent: false
      });
    }
  });

  it('Activate the SPARQL filter', () => {
    cy.route(
      options({
        url: `/api/visualization/roots?configurationId=${data.configurationId}`,
        response: 'fixture:rootsFilteredValues'
      })
    ).as('getFilteredValues');

    cy.clickItem({
      testId: '#SPARQL\\ filter-option-0',
      isDataTestIdPresent: false
    });
    cy.clickItem({
      testId: config.addFilterButton
    });
  });

  it('Check if the filter has changed the roots data', () => {
    filteredValues.map(({ uri, children }) => {
      cy.checkVisibility({
        testId: config.tableRow + uri,
        visible: true
      });
      // This part is for catching a bug that was happening when you expand the item before the filtered values where shown
      if (children)
        cy.checkVisibility({
          testId: config.right + uri,
          visible: true
        });
      else
        cy.checkExisting({
          testId: config.right + uri,
          exist: false
        });
    });

    cy.checkExisting({
      testId: `${config.tableRow}Garage`,
      exist: false
    });
  });

  it('Clear the active Filters', () => {
    cy.route(
      options({
        url: `/api/visualization/roots?configurationId=${data.configurationId}`,
        response: 'fixture:rootsColumnsFilters'
      })
    ).as('getRootsFilters');

    cy.clickItem({
      testId: config.clearActiveFiltersButton
    });

    cy.checkExisting({
      testId: `${config.tableRow}Garage`,
      exist: true
    });
  });
});
