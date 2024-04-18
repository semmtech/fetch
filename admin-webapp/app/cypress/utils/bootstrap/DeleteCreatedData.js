import { buildRequest } from '..';

// helper functions for deleting
const bodyForDeleting = idToDelete => [{ id: idToDelete }];
const deleteMethod = ({ endpoint, id }) =>
  cy.request(
    buildRequest({
      body: bodyForDeleting(id),
      endpoint,
      method: 'DELETE'
    })
  );

export default ({
  endpointId,
  queryId,
  environmentId,
  workspaceId,
  jsonApiId
}) => {
  // Delete the mocked SPARQL endpoint
  deleteMethod({ endpoint: 'sparqlendpoints', id: endpointId });

  // Delete the mocked SPARQL query
  deleteMethod({ endpoint: 'sparqlqueries', id: queryId });

  // Delete the mocked workspace
  deleteMethod({ endpoint: 'workspaces', id: workspaceId });

  // Delete the mocked environment
  deleteMethod({ endpoint: 'environments', id: environmentId });

  // Delete the mocked json api
  deleteMethod({ endpoint: 'jsonapis', id: jsonApiId });
};
