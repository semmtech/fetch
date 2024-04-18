import { buildRequest, idsToDelete } from '..';

const configs = ['12345', '67890', '159753', '01230'];
const endpoints = ['endpoint159753', 'endpoint456', 'endpoint123'];
const queries = [
  'abcde',
  'fghij',
  'hallo1',
  'hallo2',
  'hallo5',
  'hallo6',
  '12345',
  '67890',
  '123789',
  '001'
];
const workspaces = ['relatics123', 'relatics456'];
const jsonApis = ['123jsonapi', '456jsonapi'];
const environments = ['123env', '456env', '789env'];

export default () => {
  // Delete the mocked configurations
  cy.request(
    buildRequest({
      body: idsToDelete(configs),
      endpoint: 'configurations',
      method: 'DELETE'
    })
  );

  // Delete the mocked sparql endpoints
  cy.request(
    buildRequest({
      body: idsToDelete(endpoints),
      endpoint: 'sparqlendpoints',
      method: 'DELETE'
    })
  );

  // Delete the mocked sparql queries
  cy.request(
    buildRequest({
      body: idsToDelete(queries),
      endpoint: 'sparqlqueries',
      method: 'DELETE'
    })
  );

  // Delete the mocked json api's
  cy.request(
    buildRequest({
      body: idsToDelete(jsonApis),
      endpoint: 'jsonapis',
      method: 'DELETE'
    })
  );

  // Delete the mocked workspaces
  cy.request(
    buildRequest({
      body: idsToDelete(workspaces),
      endpoint: 'workspaces',
      method: 'DELETE'
    })
  );

  // Delete the mocked environments
  cy.request(
    buildRequest({
      body: idsToDelete(environments),
      endpoint: 'environments',
      method: 'DELETE'
    })
  );
};
