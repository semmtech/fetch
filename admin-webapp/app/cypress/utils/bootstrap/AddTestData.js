import { buildRequest } from '..';

export default () => {
  // Mock 2 workspaces
  cy.request(
    buildRequest({
      body: {
        id: 'relatics123',
        name: 'Workspace cypress 001',
        environmentId: '123env',
        workspaceId: 'workspace123',
        targetDataSystems: [
          {
            id: '12',
            operationName: 'GetExistingDocuments',
            entryCode: 'b93k9JkJS9g',
            type: 'Receiving',
            xpathExpression: '//ReportPart/generic/@ForeignKey'
          },
          {
            id: '34',
            operationName: 'GetExistingObjects',
            entryCode: 'b93k9JkJS9g',
            type: 'Sending',
            xpathExpression: '//ReportPart/generic/@ForeignKey'
          }
        ]
      },
      endpoint: 'workspaces',
      method: 'POST'
    })
  );
  cy.request(
    buildRequest({
      body: {
        id: 'relatics456',
        name: 'Workspace cypress 002',
        environmentId: '456env',
        workspaceId: 'workspace123',
        targetDataSystems: [
          {
            id: '56',
            operationName: 'Attributes',
            entryCode: 'b93k9JkJS9g',
            type: 'Receiving',
            xpathExpression: '//ReportPart/generic/@ForeignKey'
          },
          {
            id: '78',
            operationName: 'Members',
            entryCode: 'b93k9JkJS9g',
            type: 'Sending',
            xpathExpression: '//ReportPart/generic/@ForeignKey'
          }
        ]
      },
      endpoint: 'workspaces',
      method: 'POST'
    })
  );

  // Mock 4 sparql queries
  cy.request(
    buildRequest({
      body: {
        description: 'This is the query for cypress 001',
        id: '12345',
        name: 'CYPRESS_001',
        query: 'SELECT * WHERE {\n ?sub ?pred ?obj .\n }\n LIMIT 10',
        type: 'roots'
      },
      endpoint: 'sparqlqueries',
      method: 'POST'
    })
  );
  cy.request(
    buildRequest({
      body: {
        description: 'This is the query for cypress 002',
        id: '67890',
        name: 'CYPRESS_002',
        query: 'SELECT * WHERE {\n ?sub ?pred ?obj .\n }\n LIMIT 20',
        type: 'import'
      },
      endpoint: 'sparqlqueries',
      method: 'POST'
    })
  );
  cy.request(
    buildRequest({
      body: {
        description: 'This is the query for cypress 003',
        id: '123789',
        name: 'CYPRESS_003',
        query: 'SELECT * WHERE {\n ?sub ?pred ?obj .\n }\n LIMIT 30',
        type: 'import'
      },
      endpoint: 'sparqlqueries',
      method: 'POST'
    })
  );
  cy.request(
    buildRequest({
      body: {
        description: 'This is the query for cypress 004',
        id: '001',
        name: 'CYPRESS_004',
        query: 'SELECT * WHERE {\n ?sub ?pred ?obj .\n }\n LIMIT 30',
        type: 'filter'
      },
      endpoint: 'sparqlqueries',
      method: 'POST'
    })
  );

  // Mock 3 environments
  cy.request(
    buildRequest({
      body: {
        id: '123env',
        name: 'TEST env 001',
        serviceUrl: 'https://semmtech.relaticsonline.com/DataExchange.asmx',
        namespace: '001',
        environmentId: 'e56f5475-b28c-41dd-84ea-1e189aa44da7'
      },
      endpoint: 'environments',
      method: 'POST'
    })
  );
  cy.request(
    buildRequest({
      body: {
        id: '456env',
        name: 'TEST env 002',
        serviceUrl: 'https://semmtech.relaticsonline.com/DataExchange.asmx',
        namespace: '002',
        environmentId: 'e56f5475-b28c-41dd-84ea-1e189aa44da8'
      },
      endpoint: 'environments',
      method: 'POST'
    })
  );
  cy.request(
    buildRequest({
      body: {
        id: '789env',
        name: 'TEST env 003',
        serviceUrl: 'https://semmtech.relaticsonline.com/DataExchange.asmx',
        namespace: '003',
        environmentId: 'e56f5475-b28c-41dd-84ea-1e189aa44da9'
      },
      endpoint: 'environments',
      method: 'POST'
    })
  );

  // Mock 2 json apis
  cy.request(
    buildRequest({
      body: {
        id: '123jsonapi',
        serviceUrl: 'https://api-service.staging.neanex.com',
        name: 'TEST json api 001',
        endpoints: [
          {
            id: 'O23',
            name: 'Endpoint documents',
            apiId: '123jsonapi',
            type: 'Receiving',
            path: '/documents'
          },
          {
            id: 'O24',
            name: 'Endpoint members',
            apiId: '123jsonapi',
            type: 'Sending',
            path: '/members'
          }
        ]
      },
      endpoint: 'jsonapis',
      method: 'POST'
    })
  );
  cy.request(
    buildRequest({
      body: {
        id: '456jsonapi',
        serviceUrl: 'https://api-service.staging.neanex.com',
        name: 'TEST json api 002',
        endpoints: [
          {
            id: 'O12',
            name: 'Endpoint objects',
            apiId: '456jsonapi',
            type: 'Receiving',
            path: '/objects'
          },
          {
            id: 'O13',
            name: 'Endpoint attributes',
            apiId: '456jsonapi',
            type: 'Sending',
            path: '/attributeclasses'
          }
        ]
      },
      endpoint: 'jsonapis',
      method: 'POST'
    })
  );

  // Mock 4 configurations (SPARQL endpoints & (root or children, no import) queries included)
  cy.request(
    buildRequest({
      body: {
        description: 'This is the configuration for cypress 001',
        displayName: 'cypress_001',
        endDate: 1587765600000,
        startDate: 1556143200000,
        id: '12345',
        name: 'Cypress first mocked configuration',
        isActive: true,
        targetType: 'Relatics',
        workspace: {
          id: 'relatics123',
          name: 'Workspace cypress 001',
          environmentId: '123env',
          workspaceId: 'workspace123'
        },
        sparqlEndpoint: {
          id: 'endpoint123',
          name: 'Endpoint 001',
          url:
            'https://www.laces-platform.tech/semmtech/ns/fetch/private/test/neanex/library/assets/sparql',
          authenticationMethod: {
            applicationId: 'laces-fetch-addon',
            privateKey:
              'MIICdgIBADANBgkqhkiG9w0BAQEFAASCAmAwggJcAgEAAoGBAJUoVTqcqqoxxlZibKe9pcrT4XciW+Qq7kLAitSXbEkjl6gL1dWv6kRNNPkqaOwsKwKUGSLEWEuQMDmNfbZeUWPYRKcU6+KMMPF847WBP+7cY+NtPvhxIM9ef/P+LzMoceQPx/xrzpVN3BtDz6C6YAHPRQt91YfFPc4tUsf1BIYRAgMBAAECgYAtB30bcbqQIPC4mYQl67oGjoqtlaDaNB+z5T7ESWZ2ehlJsTEADtiRgCFy61u7mOXvJFimR1JElaYJae6+xKCbJk0eVPbwy1MvVH/0ggOGZUY5aZfqveHXRWaDB1IUph478JmiZPMRgIIVw5uhSxO83qtfXsbgd5HtO/g46KmnBQJBANiixICyIEcZjiQ6/cM+GUYMAmInrCHlM4U3vSU/oIkQknVsoeNNbNERgT6nKLhv4txg1FDVqMQUeznLkJftZ6cCQQCwQq8X4JyTKoeKgnuMyVLxnt30B36/qoH+SmvZHZWl00kap0sTjhQWhrGZYh0dPTMkNlBGk68mZSGNGK3cgduHAkEAjK8nbWxACexOorisk16AizzBT3z0DA8MpjbMXqQzXM+mTRt/Bl4BjMQRat6jUyNV8EfxmY0nTC8A10ebXw6NgQJAXIomM3sRuZJSpz3qb/gjPAgUr9JfkXGL3l5kURFfSDit4PiESjgGA+2jwMvqTTecah659tQC2T2vZ8zVOzhScQJAcEjtCABEGHZKeiwGjnrgjG3yWycUMXjo6/c43zRBUimyE7uWi9jTbnlbM2k/4+iM0VZH7gYH9y/cVjfY1IDS8g==',
            type: 'LACES_TOKEN'
          }
        },
        visualization: {
          rootsQuery: {
            query: {
              id: 'abcde',
              name: 'Roots cypress query',
              type: 'roots',
              description: 'All things that have gebouw as parent',
              query:
                'PREFIX xsd: <http://www.w3.org/2001/XMLSchema#>\nPREFIX rdfs: <http://www.w3.org/2000/01/rdf-schema#>\nPREFIX skos: <http://www.w3.org/2004/02/skos/core#>\nPREFIX owl: <http://www.w3.org/2002/07/owl#>\nPREFIX rdf: <http://www.w3.org/1999/02/22-rdf-syntax-ns#>\nPREFIX asset: <http://dds.semmtech.nl/asset/>\n    \nSELECT ?uri ?label ?hasChildren ?uuid ?isImported {\n    BIND(<http://dds.semmtech.nl/asset/Gebouw> as ?parentUri) .\n    BIND(?parent_uuid as ?parentUuid) .\n    ?uri rdf:type owl:Class ;\n        rdfs:subClassOf ?parentUri .\n   \n    OPTIONAL {\n        ?uri skos:prefLabel ?label\n    }\n     \n    BIND(EXISTS { ?child rdfs:subClassOf ?uri } as ?hasChildren) .\n \n    OPTIONAL {\n      # VALUES is populated by the result of the XML webservice:\n      VALUES (?additional_foreignKey) {\n?additional_values}\n      FILTER (?uri = ?additional_foreignKey) .\n      BIND(true as ?inner_imported) .\n    }\n    BIND(COALESCE(?inner_imported, false) as ?isImported) . \n    BIND(UUID() as ?uuid) .\n}'
            },
            defaultGraphs: []
          },
          childrenQuery: {
            query: {
              id: 'fghij',
              name: 'Children cypress query',
              type: 'children',
              description: 'All children of gebouw',
              query:
                'PREFIX xsd: <http://www.w3.org/2001/XMLSchema#>\nPREFIX rdfs: <http://www.w3.org/2000/01/rdf-schema#>\nPREFIX skos: <http://www.w3.org/2004/02/skos/core#>\nPREFIX owl: <http://www.w3.org/2002/07/owl#>\nPREFIX rdf: <http://www.w3.org/1999/02/22-rdf-syntax-ns#>\nPREFIX asset: <http://dds.semmtech.nl/asset/>\n    \nSELECT ?uri ?label ?hasChildren ?uuid ?parentUri ?parentUuid ?isImported {\n    BIND(?parent_uri as ?parentUri) .\n    BIND(?parent_uuid as ?parentUuid) .\n    ?uri rdf:type owl:Class ;\n        rdfs:subClassOf ?parentUri .\n   \n    OPTIONAL {\n        ?uri skos:prefLabel ?label\n    }\n     \n    BIND(EXISTS { ?child rdfs:subClassOf ?uri } as ?hasChildren) .\n \n    OPTIONAL {\n      # VALUES is populated by the result of the XML webservice:\n      VALUES (?additional_foreignKey) {\n?additional_values}\n      FILTER (?uri = ?additional_foreignKey) .\n      BIND(true as ?inner_imported) .\n    }\n    BIND(COALESCE(?inner_imported, false) as ?isImported) . \n    BIND(UUID() as ?uuid) .\n}'
            },
            defaultGraphs: []
          },
          columns: {
            isImported: {
              bindingName: 'isImported',
              displayName: 'Imported',
              visible: true
            },
            label: {
              bindingName: 'label',
              displayName: 'Label',
              visible: true
            },
            uri: {
              bindingName: 'uri',
              displayName: 'uri',
              visible: false
            },
            uuid: {
              bindingName: 'uuid',
              displayName: 'uuid',
              visible: false
            },
            hasChildren: {
              bindingName: 'hasChildren',
              displayName: 'hasChildren',
              visible: false
            }
          },
          additionalInputs: null,
          enablePagination: true
        },
        importSteps: [
          {
            name: 'Import hierarchy',
            sparqlQuery: {
              query: null,
              defaultGraphs: []
            },
            importTarget: null
          },
          {
            name: 'Import definition',
            sparqlQuery: {
              query: null,
              defaultGraphs: []
            },
            importTarget: null
          }
        ]
      },
      endpoint: 'configurations',
      method: 'POST'
    })
  );
  cy.request(
    buildRequest({
      body: {
        description: 'This is the configuration for cypress 002',
        displayName: 'cypress_002',
        endDate: 1607209200000,
        startDate: 1575586800000,
        id: '67890',
        name: 'Cypress second mocked configuration',
        isActive: true,
        targetType: 'Relatics',
        workspace: {
          id: 'relatics123',
          name: 'Workspace cypress 001',
          environmentId: '123env',
          workspaceId: 'workspace123'
        },
        sparqlEndpoint: {
          id: 'endpoint456',
          name: 'Endpoint 002',
          url:
            'https://agraph6-vps11.laces.tech/catalogs/test/repositories/assets',
          authenticationMethod: {
            type: 'NONE'
          }
        },
        visualization: {
          rootsQuery: {
            query: {
              id: 'hallo1',
              name: 'Roots One query',
              type: 'roots',
              description: 'All things that have Gebouw as parent',
              query:
                'PREFIX xsd: <http://www.w3.org/2001/XMLSchema#>\nPREFIX rdfs: <http://www.w3.org/2000/01/rdf-schema#>\nPREFIX skos: <http://www.w3.org/2004/02/skos/core#>\nPREFIX owl: <http://www.w3.org/2002/07/owl#>\nPREFIX rdf: <http://www.w3.org/1999/02/22-rdf-syntax-ns#>\nPREFIX asset: <http://dds.semmtech.nl/asset/>\n    \nSELECT ?uri ?label ?hasChildren ?uuid ?isImported {\n    BIND(<http://dds.semmtech.nl/asset/Gebouw> as ?parentUri) .\n    BIND(?parent_uuid as ?parentUuid) .\n    ?uri rdf:type owl:Class ;\n        rdfs:subClassOf ?parentUri .\n   \n    OPTIONAL {\n        ?uri skos:prefLabel ?label\n    }\n     \n    BIND(EXISTS { ?child rdfs:subClassOf ?uri } as ?hasChildren) .\n \n    OPTIONAL {\n      # VALUES is populated by the result of the XML webservice:\n      VALUES (?additional_foreignKey) {\n?additional_values}\n      FILTER (?uri = ?additional_foreignKey) .\n      BIND(true as ?inner_imported) .\n    }\n    BIND(COALESCE(?inner_imported, false) as ?isImported) . \n    BIND(UUID() as ?uuid) .\n}'
            },
            defaultGraphs: []
          },
          childrenQuery: {
            query: {
              id: 'hallo2',
              name: 'Children One query',
              type: 'children',
              description: 'All children of Gebouw',
              query:
                'PREFIX xsd: <http://www.w3.org/2001/XMLSchema#>\nPREFIX rdfs: <http://www.w3.org/2000/01/rdf-schema#>\nPREFIX skos: <http://www.w3.org/2004/02/skos/core#>\nPREFIX owl: <http://www.w3.org/2002/07/owl#>\nPREFIX rdf: <http://www.w3.org/1999/02/22-rdf-syntax-ns#>\nPREFIX asset: <http://dds.semmtech.nl/asset/>\n    \nSELECT ?uri ?label ?hasChildren ?uuid ?parentUri ?parentUuid ?isImported {\n    BIND(?parent_uri as ?parentUri) .\n    BIND(?parent_uuid as ?parentUuid) .\n    ?uri rdf:type owl:Class ;\n        rdfs:subClassOf ?parentUri .\n   \n    OPTIONAL {\n        ?uri skos:prefLabel ?label\n    }\n     \n    BIND(EXISTS { ?child rdfs:subClassOf ?uri } as ?hasChildren) .\n \n    OPTIONAL {\n      # VALUES is populated by the result of the XML webservice:\n      VALUES (?additional_foreignKey) {\n?additional_values}\n      FILTER (?uri = ?additional_foreignKey) .\n      BIND(true as ?inner_imported) .\n    }\n    BIND(COALESCE(?inner_imported, false) as ?isImported) . \n    BIND(UUID() as ?uuid) .\n}'
            },
            defaultGraphs: []
          },
          columns: {
            isImported: {
              bindingName: 'isImported',
              displayName: 'Imported',
              visible: true
            },
            label: {
              bindingName: 'label',
              displayName: 'Label',
              visible: true
            },
            uri: {
              bindingName: 'uri',
              displayName: 'URI',
              visible: true
            },
            uuid: {
              bindingName: 'uuid',
              displayName: 'ID',
              visible: true
            },
            hasChildren: {
              bindingName: 'hasChildren',
              displayName: 'Children',
              visible: true
            }
          },
          additionalInputs: null,
          enablePagination: false
        },
        importSteps: [
          {
            name: 'Import hierarchy',
            sparqlQuery: {
              query: null,
              defaultGraphs: []
            },
            importTarget: null
          },
          {
            name: 'Import definition',
            sparqlQuery: {
              query: null,
              defaultGraphs: []
            },
            importTarget: null
          }
        ]
      },
      endpoint: 'configurations',
      method: 'POST'
    })
  );
  cy.request(
    buildRequest({
      body: {
        description: 'This is the configuration for cypress 003',
        displayName: 'cypress_003',
        endDate: 1607468400000,
        startDate: 1575846000000,
        id: '159753',
        name: 'Cypress third mocked configuration',
        isActive: false,
        targetType: 'JSON API',
        jsonapi: {
          id: '456jsonapi',
          serviceUrl: 'https://api-service.staging.neanex.com',
          name: 'TEST json api 002'
        },
        sparqlEndpoint: {
          id: 'endpoint159753',
          name: 'Endpoint 003',
          url:
            'https://www.laces-platform.tech/semmtech/ns/fetch/private/test/neanex/library/assets/sparql',
          authenticationMethod: {
            type: 'BASIC',
            userName: 'Mike',
            password: 'Meyers'
          }
        },
        visualization: {
          rootsQuery: {
            query: {
              id: 'hallo5',
              name: 'TEST query',
              type: 'roots',
              description: 'All things that have Gebouw as parent',
              query:
                'PREFIX xsd: <http://www.w3.org/2001/XMLSchema#>\nPREFIX rdfs: <http://www.w3.org/2000/01/rdf-schema#>\nPREFIX skos: <http://www.w3.org/2004/02/skos/core#>\nPREFIX owl: <http://www.w3.org/2002/07/owl#>\nPREFIX rdf: <http://www.w3.org/1999/02/22-rdf-syntax-ns#>\nPREFIX asset: <http://dds.semmtech.nl/asset/>\n    \nSELECT ?uri ?label ?hasChildren ?uuid ?isImported {\n    BIND(<http://dds.semmtech.nl/asset/Gebouw> as ?parentUri) .\n    BIND(?parent_uuid as ?parentUuid) .\n    ?uri rdf:type owl:Class ;\n        rdfs:subClassOf ?parentUri .\n   \n    OPTIONAL {\n        ?uri skos:prefLabel ?label\n    }\n     \n    BIND(EXISTS { ?child rdfs:subClassOf ?uri } as ?hasChildren) .\n \n    OPTIONAL {\n      # VALUES is populated by the result of the XML webservice:\n      VALUES (?additional_foreignKey) {\n?additional_values}\n      FILTER (?uri = ?additional_foreignKey) .\n      BIND(true as ?inner_imported) .\n    }\n    BIND(COALESCE(?inner_imported, false) as ?isImported) . \n    BIND(UUID() as ?uuid) .\n}'
            },
            defaultGraphs: []
          },
          childrenQuery: {
            query: {
              id: 'hallo6',
              name: 'Second test query',
              type: 'children',
              description: 'All children of Gebouw',
              query:
                'PREFIX xsd: <http://www.w3.org/2001/XMLSchema#>\nPREFIX rdfs: <http://www.w3.org/2000/01/rdf-schema#>\nPREFIX skos: <http://www.w3.org/2004/02/skos/core#>\nPREFIX owl: <http://www.w3.org/2002/07/owl#>\nPREFIX rdf: <http://www.w3.org/1999/02/22-rdf-syntax-ns#>\nPREFIX asset: <http://dds.semmtech.nl/asset/>\n    \nSELECT ?uri ?label ?hasChildren ?uuid ?parentUri ?parentUuid ?isImported {\n    BIND(?parent_uri as ?parentUri) .\n    BIND(?parent_uuid as ?parentUuid) .\n    ?uri rdf:type owl:Class ;\n        rdfs:subClassOf ?parentUri .\n   \n    OPTIONAL {\n        ?uri skos:prefLabel ?label\n    }\n     \n    BIND(EXISTS { ?child rdfs:subClassOf ?uri } as ?hasChildren) .\n \n    OPTIONAL {\n      # VALUES is populated by the result of the XML webservice:\n      VALUES (?additional_foreignKey) {\n?additional_values}\n      FILTER (?uri = ?additional_foreignKey) .\n      BIND(true as ?inner_imported) .\n    }\n    BIND(COALESCE(?inner_imported, false) as ?isImported) . \n    BIND(UUID() as ?uuid) .\n}'
            },
            defaultGraphs: []
          },
          columns: {
            isImported: {
              bindingName: 'isImported',
              displayName: 'Imported',
              visible: true
            },
            label: {
              bindingName: 'label',
              displayName: 'Label',
              visible: true
            },
            uri: {
              bindingName: 'uri',
              displayName: 'uri',
              visible: false
            },
            uuid: {
              bindingName: 'uuid',
              displayName: 'uuid',
              visible: false
            },
            hasChildren: {
              bindingName: 'hasChildren',
              displayName: 'hasChildren',
              visible: false
            }
          },
          additionalInputs: null,
          enablePagination: true
        },
        importSteps: [
          {
            name: 'Import hierarchy',
            sparqlQuery: {
              query: null,
              defaultGraphs: []
            },
            importTarget: null
          },
          {
            name: 'Import definition',
            sparqlQuery: {
              query: null,
              defaultGraphs: []
            },
            importTarget: null
          }
        ]
      },
      endpoint: 'configurations',
      method: 'POST'
    })
  );
  cy.request(
    buildRequest({
      body: {
        description: 'This is the configuration for cypress 004',
        displayName: 'cypress_004',
        endDate: 1607209200000,
        startDate: 1575586800000,
        id: '01230',
        name: 'Cypress fourth mocked configuration',
        isActive: true,
        targetType: 'Relatics',
        workspace: {
          id: 'relatics123',
          name: 'Workspace cypress 001',
          environmentId: '123env',
          workspaceId: 'workspace123'
        },
        sparqlEndpoint: {
          id: 'endpoint456',
          name: 'Endpoint 002',
          url:
            'https://agraph6-vps11.laces.tech/catalogs/test/repositories/assets',
          authenticationMethod: {
            type: 'NONE'
          }
        },
        visualization: {
          rootsQuery: {
            query: {
              id: 'hallo5',
              name: 'TEST query',
              description: 'All things that have Gebouw as parent',
              query:
                'PREFIX xsd: <http://www.w3.org/2001/XMLSchema#>\nPREFIX rdfs: <http://www.w3.org/2000/01/rdf-schema#>\nPREFIX skos: <http://www.w3.org/2004/02/skos/core#>\nPREFIX owl: <http://www.w3.org/2002/07/owl#>\nPREFIX rdf: <http://www.w3.org/1999/02/22-rdf-syntax-ns#>\nPREFIX asset: <http://dds.semmtech.nl/asset/>\n    \nSELECT ?uri ?label ?hasChildren ?uuid ?isImported {\n    BIND(<http://dds.semmtech.nl/asset/Gebouw> as ?parentUri) .\n    BIND(?parent_uuid as ?parentUuid) .\n    ?uri rdf:type owl:Class ;\n        rdfs:subClassOf ?parentUri .\n   \n    OPTIONAL {\n        ?uri skos:prefLabel ?label\n    }\n     \n    BIND(EXISTS { ?child rdfs:subClassOf ?uri } as ?hasChildren) .\n \n    OPTIONAL {\n      # VALUES is populated by the result of the XML webservice:\n      VALUES (?additional_foreignKey) {\n?additional_values}\n      FILTER (?uri = ?additional_foreignKey) .\n      BIND(true as ?inner_imported) .\n    }\n    BIND(COALESCE(?inner_imported, false) as ?isImported) . \n    BIND(UUID() as ?uuid) .\n}'
            },
            defaultGraphs: []
          },
          childrenQuery: {
            query: null,
            defaultGraphs: []
          },
          columns: {
            isImported: {
              bindingName: 'isImported',
              displayName: 'Imported',
              visible: true
            },
            label: {
              bindingName: 'label',
              displayName: 'Label',
              visible: true
            },
            uri: {
              bindingName: 'uri',
              displayName: 'URI',
              visible: false
            },
            uuid: {
              bindingName: 'uuid',
              displayName: 'ID',
              visible: true
            },
            hasChildren: {
              bindingName: 'hasChildren',
              displayName: 'Children',
              visible: false
            }
          },
          additionalInputs: null,
          enablePagination: false
        },
        importSteps: [
          {
            name: 'Import hierarchy',
            sparqlQuery: {
              query: null,
              defaultGraphs: []
            },
            importTarget: null
          },
          {
            name: 'Import definition',
            sparqlQuery: {
              query: null,
              defaultGraphs: []
            },
            importTarget: null
          }
        ]
      },
      endpoint: 'configurations',
      method: 'POST'
    })
  );
};
