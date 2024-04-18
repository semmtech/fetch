import { buildRequest } from '..';

export default ({ configJsonApiId, configRelaticsId }) => {
  // Delete the mocked Relatics and Neanex configurations
  cy.request(
    buildRequest({
      body: [{ id: configJsonApiId }, { id: configRelaticsId }],
      endpoint: 'configurations',
      method: 'DELETE'
    })
  );
};
