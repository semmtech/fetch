export const colors = {
  black: 'rgb(76, 76, 76)',
  yellow: 'rgba(246, 229, 36, 0.75)',
  red: 'rgb(191, 54, 12)',
  green: 'rgb(175, 210, 117)'
};

export const buildRequest = ({ body, endpoint, method }) => ({
  body,
  headers: {
    'Content-Type': 'application/json',
    Accept: 'application/json'
  },
  method,
  url: `/api/${endpoint}/`
});

export const testConfig = {
  adminURL: '/admin/index.html',
  authenticationURL: '/authentication/index.html',
  userName: 'admin',
  password: 'welcome1',
  sessionId: 'JSESSIONID',
  menuItems: {
    configurations: 'sideList_Configurations',
    endpoints: '"sideList_SPARQL endpoints"',
    queries: '"sideList_SPARQL queries"',
    relatics: 'sideList_Relatics',
    neanex: 'sideList_Neanex'
  },
  urls: {
    configurations: '#/Configurations',
    endpoints: '#/SPARQLendpoints',
    queries: '#/SPARQLqueries',
    relatics: '#/Relatics',
    neanex: '#/Neanex'
  }
};

export const autoCompleteFields = {
  additionalInput: '#Autocomplete_Additional\\ input',
  authType: '#Autocomplete_Authentication\\ type',
  endpoint: '#Autocomplete_Endpoint',
  environment: '#Autocomplete_Environment',
  filterValue: '#Autocomplete_Filter\\ value',
  jsonApi: '#Autocomplete_Json\\ Api',
  mainFilter: '#Autocomplete_Main\\ filter\\ id',
  query: '#Autocomplete_Query',
  sparqlEndpoint: '#Autocomplete_SPARQL\\ Endpoint',
  sparqlRootsQuery: '#Autocomplete_SPARQL\\ roots\\ query',
  sparqlChildrenQuery: '#Autocomplete_SPARQL\\ children\\ query',
  subFilter: '#Autocomplete_Sub\\ filter\\ id',
  target: '#Autocomplete_Target',
  type: '#Autocomplete_Type',
  workspace: '#Autocomplete_Workspace'
};

export const idsToDelete = ids => ids.map(id => ({ id }));
