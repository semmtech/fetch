package com.semmtech.laces.fetch.visualization.service;

import com.semmtech.laces.fetch.configuration.entities.AddOnEntity;
import com.semmtech.laces.fetch.configuration.entities.SparqlEndpointEntity;
import com.semmtech.laces.fetch.configuration.entities.SparqlQueryEntity;
import com.semmtech.laces.fetch.configuration.entities.SparqlQueryWithDefaultGraphs;
import com.semmtech.laces.fetch.configuration.entities.VisualizationEntity;
import com.semmtech.laces.fetch.imports.generic.service.ImportService;
import com.semmtech.laces.fetch.sparql.ParameterNodeFactory;
import com.semmtech.laces.fetch.sparql.SparqlBinding;
import com.semmtech.laces.fetch.sparql.SparqlClient;
import com.semmtech.laces.fetch.sparql.SparqlHead;
import com.semmtech.laces.fetch.sparql.SparqlResponse;
import com.semmtech.laces.fetch.sparql.SparqlResults;
import com.semmtech.laces.fetch.sparql.SparqlTypeCache;
import com.semmtech.laces.fetch.visualization.model.CommonParameter;
import com.semmtech.laces.fetch.visualization.model.QueryExecutionRequest;
import com.semmtech.laces.fetch.visualization.model.RootsQueryResponse;
import com.semmtech.laces.fetch.visualization.model.QueryResult;
import org.apache.jena.graph.NodeFactory;
import org.junit.Test;
import org.mockito.internal.verification.VerificationModeFactory;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import static org.hamcrest.Matchers.allOf;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.hasEntry;
import static org.hamcrest.Matchers.hasItem;
import static org.hamcrest.Matchers.hasProperty;
import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.isEmptyOrNullString;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.anyString;
import static org.mockito.Mockito.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class VisualizationServiceTest {

    @Test
    public void rootsQueryWithAdditionalInput_replacePlaceHolder() {
        AddOnEntity configuration =
                AddOnEntity.builder()
                        .sparqlEndpoint(
                                SparqlEndpointEntity.builder().build()
                        )
                        .displayName("displayName")
                        .visualization(
                                VisualizationEntity.builder()
                                        .rootsQuery(
                                                SparqlQueryWithDefaultGraphs.builder()
                                                        .query(
                                                                SparqlQueryEntity
                                                                        .builder()
                                                                        .query("PREFIX xsd: <http://www.w3.org/2001/XMLSchema#>\\nPREFIX rdfs: <http://www.w3.org/2000/01/rdf-schema#>\\nPREFIX skos: <http://www.w3.org/2004/02/skos/core#>\\nPREFIX owl: <http://www.w3.org/2002/07/owl#>\\nPREFIX rdf: <http://www.w3.org/1999/02/22-rdf-syntax-ns#>\\nPREFIX asset: <http://dds.semmtech.nl/asset/>\\n    \\nSELECT ?uri ?label ?hasChildren ?uuid ?isImported {\\n    BIND(<http://dds.semmtech.nl/asset/Gebouw> as ?parentUri) .\\n    ?uri rdf:type owl:Class ;\\n        rdfs:subClassOf ?parentUri .\\n   \\n    OPTIONAL {\\n        ?uri skos:prefLabel ?label\\n    }\\n     \\n    BIND(EXISTS { ?child rdfs:subClassOf ?uri } as ?hasChildren) .\\n \\n    OPTIONAL {\\n      # VALUES is populated by the result of the XML webservice:\\n      VALUES (?additional_foreignKey) {\\n?additional_values}\\n      FILTER (?uri = ?relatics_foreignKey) .\\n      BIND(true as ?inner_imported) .\\n    }\\n    BIND(COALESCE(?inner_imported, false) as ?isImported) . \\n    BIND(UUID() as ?uuid) .\\n}  LIMIT 1")
                                                                        .build())
                                                        .defaultGraphs(Collections.emptyList())
                                                        .build()
                                        )
                                        .titleQuery(
                                                SparqlQueryEntity
                                                        .builder()
                                                        .query("PREFIX dcterms: <http://purl.org/dc/terms/>\\nPREFIX xsd: <http://www.w3.org/2001/XMLSchema#>\\nPREFIX rdfs: <http://www.w3.org/2000/01/rdf-schema#>\\nPREFIX skos: <http://www.w3.org/2004/02/skos/core#>\\nPREFIX owl: <http://www.w3.org/2002/07/owl#>\\nPREFIX rdf: <http://www.w3.org/1999/02/22-rdf-syntax-ns#>\\nSELECT ?title ?subtitle\\nWHERE {\\n\\t?uri a owl:Ontology;\\n\\t\\t rdfs:label ?title .\\n OPTIONAL {\\n ?uri owl:versionInfo ?version . \\tOPTIONAL {\\n ?uri dcterms:creator ?creator . \\n}\\n BIND(CONCAT(?version,\" | Created by: \", ?creator) \\tas ?subtitle )\\n }")
                                                        .build()
                                        )
                                        .additionalInputsConfiguration("")
                                        .build()
                        ).build();

        ImportService importService = mock(ImportService.class);
        Map<String, String> readDataResponseMap = new HashMap<>();
        readDataResponseMap.put("foreignkey", "https://uri/");
        when(importService.readData(configuration)).thenReturn(Collections.singletonList(readDataResponseMap));

        //Title query response
        List<Map<String, SparqlBinding>> titleQueryResponse = new ArrayList<>();
        HashMap<String, SparqlBinding> titleResponse = new HashMap<>();
        titleResponse.put("title", new SparqlBinding("type", "string", "Title query", null));
        titleResponse.put("subtitle", new SparqlBinding("type", "string", "Subtitle query", null));
        titleQueryResponse.add(titleResponse);

        SparqlClient sparqlClient = mock(SparqlClient.class);
        when(sparqlClient.executeQuery(anyString(), any(List.class), any(SparqlEndpointEntity.class)))
                .thenReturn(
                        Optional.of(
                                SparqlResponse.builder()
                                        .head(
                                                SparqlHead.builder()
                                                        .variables(
                                                                Collections.singletonList("column")
                                                        )
                                                        .build())
                                        .results(
                                                SparqlResults.builder()
                                                        .bindings(Collections.emptyList())
                                                        .build())
                                        .build()
                        ),
                        Optional.of(
                                SparqlResponse.builder()
                                        .head(
                                                SparqlHead.builder()
                                                        .variables(
                                                                Collections.singletonList("column")
                                                        )
                                                        .build())
                                        .results(
                                                SparqlResults.builder()
                                                        .bindings(titleQueryResponse)
                                                        .build())
                                        .build()
                        )
                );

        VisualizationService visualizationService = new VisualizationService(sparqlClient, mock(SparqlTypeCache.class), null, importService);
        RootsQueryResponse treeNode = visualizationService.executeRootQuery(configuration, QueryExecutionRequest.builder().build());

        verify(sparqlClient, VerificationModeFactory.times(1)).executeQuery(eq("PREFIX xsd: <http://www.w3.org/2001/XMLSchema#>\\nPREFIX rdfs: <http://www.w3.org/2000/01/rdf-schema#>\\nPREFIX skos: <http://www.w3.org/2004/02/skos/core#>\\nPREFIX owl: <http://www.w3.org/2002/07/owl#>\\nPREFIX rdf: <http://www.w3.org/1999/02/22-rdf-syntax-ns#>\\nPREFIX asset: <http://dds.semmtech.nl/asset/>\\n    \\nSELECT ?uri ?label ?hasChildren ?uuid ?isImported {\\n    BIND(<http://dds.semmtech.nl/asset/Gebouw> as ?parentUri) .\\n    ?uri rdf:type owl:Class ;\\n        rdfs:subClassOf ?parentUri .\\n   \\n    OPTIONAL {\\n        ?uri skos:prefLabel ?label\\n    }\\n     \\n    BIND(EXISTS { ?child rdfs:subClassOf ?uri } as ?hasChildren) .\\n \\n    OPTIONAL {\\n      # VALUES is populated by the result of the XML webservice:\\n      VALUES (?additional_foreignKey) {\\n(<https://uri/>)}\\n      FILTER (?uri = ?relatics_foreignKey) .\\n      BIND(true as ?inner_imported) .\\n    }\\n    BIND(COALESCE(?inner_imported, false) as ?isImported) . \\n    BIND(UUID() as ?uuid) .\\n}  LIMIT 1"), any(List.class), any());

        verify(sparqlClient, VerificationModeFactory.times(1)).executeQuery(eq("PREFIX dcterms: <http://purl.org/dc/terms/>\\nPREFIX xsd: <http://www.w3.org/2001/XMLSchema#>\\nPREFIX rdfs: <http://www.w3.org/2000/01/rdf-schema#>\\nPREFIX skos: <http://www.w3.org/2004/02/skos/core#>\\nPREFIX owl: <http://www.w3.org/2002/07/owl#>\\nPREFIX rdf: <http://www.w3.org/1999/02/22-rdf-syntax-ns#>\\nSELECT ?title ?subtitle\\nWHERE {\\n\\t?uri a owl:Ontology;\\n\\t\\t rdfs:label ?title .\\n OPTIONAL {\\n ?uri owl:versionInfo ?version . \\tOPTIONAL {\\n ?uri dcterms:creator ?creator . \\n}\\n BIND(CONCAT(?version,\" | Created by: \", ?creator) \\tas ?subtitle )\\n }"), any(List.class), any());

        assertThat(treeNode.getVisualizationMetadata().getTitle(), equalTo("Title query"));
        assertThat(treeNode.getVisualizationMetadata().getSubtitle(), equalTo("Subtitle query"));
    }

    @Test
    public void rootsQueryWithAdditionalInput_withDefaultGraphs_replacePlaceHolder_parametersApplied() {
        AddOnEntity configuration =
                AddOnEntity.builder()
                        .sparqlEndpoint(
                                SparqlEndpointEntity.builder().build()
                        )
                        .displayName("displayName")
                        .visualization(
                                VisualizationEntity.builder()
                                        .rootsQuery(
                                                SparqlQueryWithDefaultGraphs.builder()
                                                        .query(
                                                                SparqlQueryEntity
                                                                        .builder()
                                                                        .query("PREFIX xsd: <http://www.w3.org/2001/XMLSchema#>\\nPREFIX rdfs: <http://www.w3.org/2000/01/rdf-schema#>\\nPREFIX skos: <http://www.w3.org/2004/02/skos/core#>\\nPREFIX owl: <http://www.w3.org/2002/07/owl#>\\nPREFIX rdf: <http://www.w3.org/1999/02/22-rdf-syntax-ns#>\\nPREFIX asset: <http://dds.semmtech.nl/asset/>\\n    \\nSELECT ?uri ?label ?hasChildren ?uuid ?isImported {\\n    BIND(<http://dds.semmtech.nl/asset/Gebouw> as ?parentUri) .\\n    ?uri rdf:type owl:Class ;\\n        rdfs:subClassOf ?parentUri .\\n   \\n    OPTIONAL {\\n        ?uri skos:prefLabel ?label\\n    }\\n     \\n    BIND(EXISTS { ?child rdfs:subClassOf ?uri } as ?hasChildren) .\\n \\n    OPTIONAL {\\n      # VALUES is populated by the result of the XML webservice:\\n      VALUES (?additional_foreignKey) {\\n?additional_values}\\n      FILTER (?uri = ?relatics_foreignKey) .\\n      BIND(true as ?inner_imported) .\\n    }\\n    BIND(COALESCE(?inner_imported, false) as ?isImported) . \\n    BIND(UUID() as ?uuid) .\\n}  LIMIT 1")
                                                                        .build())
                                                        .defaultGraphs(List.of("http://thegreatestdefault.graph/", "http://themostawesomedefault.graph/"))
                                                        .build()
                                        )
                                        .additionalInputsConfiguration("")
                                        .enablePagination(true)
                                        .build()
                        ).build();

        ImportService importService = mock(ImportService.class);
        Map<String, String> readDataResponseMap = new HashMap<>();
        readDataResponseMap.put("foreignkey", "https://uri/");
        when(importService.readData(configuration)).thenReturn(Collections.singletonList(readDataResponseMap));

        SparqlClient sparqlClient = mock(SparqlClient.class);
        when(sparqlClient.executeQuery(anyString(), eq(List.of("http://thegreatestdefault.graph/", "http://themostawesomedefault.graph/")), any(SparqlEndpointEntity.class)))
                .thenReturn(
                        Optional.of(
                                SparqlResponse.builder()
                                        .head(
                                                SparqlHead.builder()
                                                        .variables(
                                                                Collections.singletonList("column")
                                                        )
                                                        .build())
                                        .results(
                                                SparqlResults.builder()
                                                        .bindings(Collections.emptyList())
                                                        .build())
                                        .build()
                        )
                );

        VisualizationService visualizationService = new VisualizationService(sparqlClient, mock(SparqlTypeCache.class), null, importService);
        RootsQueryResponse treeNode = visualizationService.executeRootQuery(configuration, QueryExecutionRequest.builder().build());

        verify(sparqlClient, VerificationModeFactory.times(1)).executeQuery(eq("PREFIX xsd: <http://www.w3.org/2001/XMLSchema#>\\nPREFIX rdfs: <http://www.w3.org/2000/01/rdf-schema#>\\nPREFIX skos: <http://www.w3.org/2004/02/skos/core#>\\nPREFIX owl: <http://www.w3.org/2002/07/owl#>\\nPREFIX rdf: <http://www.w3.org/1999/02/22-rdf-syntax-ns#>\\nPREFIX asset: <http://dds.semmtech.nl/asset/>\\n    \\nSELECT ?uri ?label ?hasChildren ?uuid ?isImported {\\n    BIND(<http://dds.semmtech.nl/asset/Gebouw> as ?parentUri) .\\n    ?uri rdf:type owl:Class ;\\n        rdfs:subClassOf ?parentUri .\\n   \\n    OPTIONAL {\\n        ?uri skos:prefLabel ?label\\n    }\\n     \\n    BIND(EXISTS { ?child rdfs:subClassOf ?uri } as ?hasChildren) .\\n \\n    OPTIONAL {\\n      # VALUES is populated by the result of the XML webservice:\\n      VALUES (?additional_foreignKey) {\\n(<https://uri/>)}\\n      FILTER (?uri = ?relatics_foreignKey) .\\n      BIND(true as ?inner_imported) .\\n    }\\n    BIND(COALESCE(?inner_imported, false) as ?isImported) . \\n    BIND(UUID() as ?uuid) .\\n}  LIMIT 1"), any(List.class), any());

        assertThat(treeNode.getVisualizationMetadata(), hasProperty("enablePagination", equalTo(true)));
        assertThat(treeNode.getVisualizationMetadata().getTitle(), equalTo("displayName"));
        assertThat(treeNode.getVisualizationMetadata().getSubtitle(), isEmptyOrNullString());
    }

    @Test
    public void rootsQueryWithAdditionalInputAndParameters_replacePlaceHolders() {
        AddOnEntity configuration =
                AddOnEntity.builder()
                        .sparqlEndpoint(
                                SparqlEndpointEntity.builder().build()
                        )
                        .displayName("displayName")
                        .visualization(
                                VisualizationEntity.builder()
                                        .rootsQuery(
                                                SparqlQueryWithDefaultGraphs
                                                        .builder()
                                                        .query(
                                                                SparqlQueryEntity
                                                                        .builder()
                                                                        .query("PREFIX xsd: <http://www.w3.org/2001/XMLSchema#>\\nPREFIX rdfs: <http://www.w3.org/2000/01/rdf-schema#>\\nPREFIX skos: <http://www.w3.org/2004/02/skos/core#>\\nPREFIX owl: <http://www.w3.org/2002/07/owl#>\\nPREFIX rdf: <http://www.w3.org/1999/02/22-rdf-syntax-ns#>\\nPREFIX asset: <http://dds.semmtech.nl/asset/>\\n    \\nSELECT ?uri ?label ?hasChildren ?uuid ?isImported ?userAccount {\\n    BIND(?parameter_username as ?userAccount) .\\n BIND(<http://dds.semmtech.nl/asset/Gebouw> as ?parentUri) .\\n    ?uri rdf:type owl:Class ;\\n        rdfs:subClassOf ?parentUri .\\n   \\n    OPTIONAL {\\n        ?uri skos:prefLabel ?label\\n    }\\n     \\n    BIND(EXISTS { ?child rdfs:subClassOf ?uri } as ?hasChildren) .\\n \\n    OPTIONAL {\\n      # VALUES is populated by the result of the XML webservice:\\n      VALUES (?additional_foreignKey) {\\n?additional_values}\\n      FILTER (?uri = ?relatics_foreignKey) .\\n      BIND(true as ?inner_imported) .\\n    }\\n    BIND(COALESCE(?inner_imported, false) as ?isImported) . \\n    BIND(UUID() as ?uuid) .\\n}  LIMIT 1")
                                                                        .build())
                                                        .defaultGraphs(Collections.emptyList())
                                                        .build())

                                        .titleQuery(
                                                SparqlQueryEntity
                                                        .builder()
                                                        .query("PREFIX dcterms: <http://purl.org/dc/terms/>\\nPREFIX xsd: <http://www.w3.org/2001/XMLSchema#>\\nPREFIX rdfs: <http://www.w3.org/2000/01/rdf-schema#>\\nPREFIX skos: <http://www.w3.org/2004/02/skos/core#>\\nPREFIX owl: <http://www.w3.org/2002/07/owl#>\\nPREFIX rdf: <http://www.w3.org/1999/02/22-rdf-syntax-ns#>\\nSELECT ?title ?subtitle\\nWHERE {\\n\\t?uri a owl:Ontology;\\n\\t\\t rdfs:label ?title .\\n OPTIONAL {\\n ?uri owl:versionInfo ?version . \\tOPTIONAL {\\n ?uri dcterms:creator ?creator . \\n}\\n BIND(CONCAT(?version,\" | Created by: \", ?creator) \\tas ?subtitle )\\n }")
                                                        .build()
                                        )
                                        .additionalInputsConfiguration("")
                                        .build()

                        ).build();

        ImportService importService = mock(ImportService.class);
        Map<String, String> readDataResponseMap = new HashMap<>();
        readDataResponseMap.put("foreignkey", "https://uri/");
        when(importService.readData(configuration)).thenReturn(Collections.singletonList(readDataResponseMap));

        //Title query response
        List<Map<String, SparqlBinding>> titleQueryResponse = new ArrayList<>();
        HashMap<String, SparqlBinding> titleResponse = new HashMap<>();
        titleResponse.put("title", new SparqlBinding("type", "string", "Title query", null));
        titleQueryResponse.add(titleResponse);

        SparqlClient sparqlClient = mock(SparqlClient.class);
        when(sparqlClient.executeQuery(anyString(), any(List.class), any(SparqlEndpointEntity.class)))
                .thenReturn(
                        Optional.of(
                                SparqlResponse.builder()
                                        .head(
                                                SparqlHead.builder()
                                                        .variables(
                                                                Collections.singletonList("column")
                                                        )
                                                        .build())
                                        .results(
                                                SparqlResults.builder()
                                                        .bindings(Collections.emptyList())
                                                        .build())
                                        .build()
                        ),
                        Optional.of(
                                SparqlResponse.builder()
                                        .head(
                                                SparqlHead.builder()
                                                        .variables(
                                                                Collections.singletonList("column")
                                                        )
                                                        .build())
                                        .results(
                                                SparqlResults.builder()
                                                        .bindings(titleQueryResponse)
                                                        .build())
                                        .build()
                        )
                );

        var sparqlTypeCache = mock(SparqlTypeCache.class);
        ParameterNodeFactory parameterNodeFactory = new ParameterNodeFactory(sparqlTypeCache);

        VisualizationService visualizationService = new VisualizationService(sparqlClient, sparqlTypeCache, parameterNodeFactory, importService);
        List<CommonParameter> commonParameters = new ArrayList<>();
        commonParameters.add(CommonParameter.builder().type("literal").value("Chuck Norris").id("username").build());

        QueryExecutionRequest input =
                QueryExecutionRequest.builder()
                        .commonParameters(commonParameters)
                        .build();
        RootsQueryResponse treeNode = visualizationService.executeRootQuery(configuration, input);

        verify(sparqlClient, VerificationModeFactory.times(1)).executeQuery(eq("PREFIX xsd: <http://www.w3.org/2001/XMLSchema#>\\nPREFIX rdfs: <http://www.w3.org/2000/01/rdf-schema#>\\nPREFIX skos: <http://www.w3.org/2004/02/skos/core#>\\nPREFIX owl: <http://www.w3.org/2002/07/owl#>\\nPREFIX rdf: <http://www.w3.org/1999/02/22-rdf-syntax-ns#>\\nPREFIX asset: <http://dds.semmtech.nl/asset/>\\n    \\nSELECT ?uri ?label ?hasChildren ?uuid ?isImported ?userAccount {\\n    BIND(\"Chuck Norris\" as ?userAccount) .\\n BIND(<http://dds.semmtech.nl/asset/Gebouw> as ?parentUri) .\\n    ?uri rdf:type owl:Class ;\\n        rdfs:subClassOf ?parentUri .\\n   \\n    OPTIONAL {\\n        ?uri skos:prefLabel ?label\\n    }\\n     \\n    BIND(EXISTS { ?child rdfs:subClassOf ?uri } as ?hasChildren) .\\n \\n    OPTIONAL {\\n      # VALUES is populated by the result of the XML webservice:\\n      VALUES (?additional_foreignKey) {\\n(<https://uri/>)}\\n      FILTER (?uri = ?relatics_foreignKey) .\\n      BIND(true as ?inner_imported) .\\n    }\\n    BIND(COALESCE(?inner_imported, false) as ?isImported) . \\n    BIND(UUID() as ?uuid) .\\n}  LIMIT 1"), any(List.class), any());

        assertThat(treeNode.getVisualizationMetadata(), hasProperty("enablePagination", equalTo(false)));

        assertThat(treeNode.getVisualizationMetadata().getTitle(), equalTo("Title query"));
        assertThat(treeNode.getVisualizationMetadata().getSubtitle(), isEmptyOrNullString());
    }

    @Test
    public void childrenQueryWithAdditionalInput_replacePlaceHolder() {
        AddOnEntity configuration =
                AddOnEntity.builder()
                        .sparqlEndpoint(
                                SparqlEndpointEntity.builder().build()
                        )
                        .visualization(
                                VisualizationEntity.builder()
                                        .childrenQuery(
                                                SparqlQueryWithDefaultGraphs
                                                        .builder()
                                                        .query(
                                                                SparqlQueryEntity
                                                                        .builder()
                                                                        .query("PREFIX xsd: <http://www.w3.org/2001/XMLSchema#>\\nPREFIX rdfs: <http://www.w3.org/2000/01/rdf-schema#>\\nPREFIX skos: <http://www.w3.org/2004/02/skos/core#>\\nPREFIX owl: <http://www.w3.org/2002/07/owl#>\\nPREFIX rdf: <http://www.w3.org/1999/02/22-rdf-syntax-ns#>\\nPREFIX asset: <http://dds.semmtech.nl/asset/>\\n    \\nSELECT ?uri ?label ?hasChildren ?uuid ?isImported ?userAccount {\\n    BIND(?parameter_username as ?userAccount) .\\n BIND(?parent_uri as ?parentUri) .\\n    BIND(?parent_uuid as ?parentUuid) .\\n    ?uri rdf:type owl:Class ;\\n        rdfs:subClassOf ?parentUri .\\n   \\n    OPTIONAL {\\n        ?uri skos:prefLabel ?label\\n    }\\n     \\n    BIND(EXISTS { ?child rdfs:subClassOf ?uri } as ?hasChildren) .\\n \\n    OPTIONAL {\\n      # VALUES is populated by the result of the XML webservice:\\n      VALUES (?additional_foreignKey) {\\n?additional_values}\\n      FILTER (?uri = ?relatics_foreignKey) .\\n      BIND(true as ?inner_imported) .\\n    }\\n    BIND(COALESCE(?inner_imported, false) as ?isImported) . \\n    BIND(UUID() as ?uuid) .\\n}  LIMIT 1")
                                                                        .build())
                                                        .defaultGraphs(Collections.emptyList())
                                                        .build())
                                        .additionalInputsConfiguration("")
                                        .build()
                        ).build();

        ImportService importService = mock(ImportService.class);
        Map<String, String> readDataResponseMap = new HashMap<>();
        readDataResponseMap.put("foreignkey", "https://uri/");
        when(importService.readData(configuration)).thenReturn(Collections.singletonList(readDataResponseMap));

        SparqlClient sparqlClient = mock(SparqlClient.class);
        when(sparqlClient.executeQuery(anyString(), any(List.class), any(SparqlEndpointEntity.class)))
                .thenReturn(
                        Optional.of(
                                SparqlResponse.builder()
                                        .head(
                                                SparqlHead.builder()
                                                        .variables(
                                                                Collections.singletonList("column")
                                                        )
                                                        .build())
                                        .results(
                                                SparqlResults.builder()
                                                        .bindings(Collections.emptyList())
                                                        .build())
                                        .build()
                        )
                );

        Map<String, String> selectedNodeData = new HashMap<>();
        selectedNodeData.put("uri", "https://parenturi/");
        selectedNodeData.put("uuid", "urn:uuid:ea7020ae-bde5-cd34-4bec-525400767bfb");

        List<CommonParameter> commonParameters = new ArrayList<>();
        commonParameters.add(CommonParameter.builder().type("literal").value("Chuck Norris").id("username").build());

        QueryExecutionRequest input = QueryExecutionRequest.builder()
                .commonParameters(commonParameters)
                .values(
                        Collections.singletonList(selectedNodeData)
                )
                .build();

        ParameterNodeFactory parameterNodeFactory = mock(ParameterNodeFactory.class);
        when(parameterNodeFactory.createNode("userAccount", "Chuck Norris", "literal")).thenReturn(NodeFactory.createLiteral("Chuck Norris"));
        when(parameterNodeFactory.createNode(eq("parentUri"), anyString(), eq(null)))
                .thenAnswer(invocationOnMock -> NodeFactory.createURI(invocationOnMock.getArgument(1)));
        when(parameterNodeFactory.createNode(eq("parentUuid"), anyString(), eq(null)))
                .thenAnswer(invocationOnMock -> NodeFactory.createURI(invocationOnMock.getArgument(1)));


        VisualizationService visualizationService = new VisualizationService(sparqlClient, mock(SparqlTypeCache.class), parameterNodeFactory, importService);
        QueryResult queryResult = visualizationService.executeChildQuery(configuration, input);

        verify(sparqlClient, VerificationModeFactory.times(1)).executeQuery(eq("PREFIX xsd: <http://www.w3.org/2001/XMLSchema#>\\nPREFIX rdfs: <http://www.w3.org/2000/01/rdf-schema#>\\nPREFIX skos: <http://www.w3.org/2004/02/skos/core#>\\nPREFIX owl: <http://www.w3.org/2002/07/owl#>\\nPREFIX rdf: <http://www.w3.org/1999/02/22-rdf-syntax-ns#>\\nPREFIX asset: <http://dds.semmtech.nl/asset/>\\n    \\nSELECT ?uri ?label ?hasChildren ?uuid ?isImported ?userAccount {\\n    BIND(\"Chuck Norris\" as ?userAccount) .\\n BIND(<https://parenturi/> as ?parentUri) .\\n    BIND(<urn:uuid:ea7020ae-bde5-cd34-4bec-525400767bfb> as ?parentUuid) .\\n    ?uri rdf:type owl:Class ;\\n        rdfs:subClassOf ?parentUri .\\n   \\n    OPTIONAL {\\n        ?uri skos:prefLabel ?label\\n    }\\n     \\n    BIND(EXISTS { ?child rdfs:subClassOf ?uri } as ?hasChildren) .\\n \\n    OPTIONAL {\\n      # VALUES is populated by the result of the XML webservice:\\n      VALUES (?additional_foreignKey) {\\n(<https://uri/>)}\\n      FILTER (?uri = ?relatics_foreignKey) .\\n      BIND(true as ?inner_imported) .\\n    }\\n    BIND(COALESCE(?inner_imported, false) as ?isImported) . \\n    BIND(UUID() as ?uuid) .\\n}  LIMIT 1"), any(List.class), any());

    }

    @Test
    public void childrenQueryWithoutAdditionalInput_replacePlaceHolderWithUrnNothing() {
        AddOnEntity configuration =
                AddOnEntity.builder()
                        .sparqlEndpoint(
                                SparqlEndpointEntity.builder().build()
                        )
                        .visualization(
                                VisualizationEntity
                                        .builder()
                                        .childrenQuery(
                                                SparqlQueryWithDefaultGraphs
                                                        .builder()
                                                        .query(
                                                                SparqlQueryEntity
                                                                        .builder()
                                                                        .query("PREFIX xsd: <http://www.w3.org/2001/XMLSchema#>\\nPREFIX rdfs: <http://www.w3.org/2000/01/rdf-schema#>\\nPREFIX skos: <http://www.w3.org/2004/02/skos/core#>\\nPREFIX owl: <http://www.w3.org/2002/07/owl#>\\nPREFIX rdf: <http://www.w3.org/1999/02/22-rdf-syntax-ns#>\\nPREFIX asset: <http://dds.semmtech.nl/asset/>\\n    \\nSELECT ?uri ?label ?hasChildren ?uuid ?isImported ?userAccount {\\n    BIND(?parameter_username as ?userAccount) .\\n BIND(?parent_uri as ?parentUri) .\\n    BIND(?parent_uuid as ?parentUuid) .\\n    ?uri rdf:type owl:Class ;\\n        rdfs:subClassOf ?parentUri .\\n   \\n    OPTIONAL {\\n        ?uri skos:prefLabel ?label\\n    }\\n     \\n    BIND(EXISTS { ?child rdfs:subClassOf ?uri } as ?hasChildren) .\\n \\n    OPTIONAL {\\n      # VALUES is populated by the result of the XML webservice:\\n      VALUES (?additional_foreignKey) {\\n?additional_values}\\n      FILTER (?uri = ?relatics_foreignKey) .\\n      BIND(true as ?inner_imported) .\\n    }\\n    BIND(COALESCE(?inner_imported, false) as ?isImported) . \\n    BIND(UUID() as ?uuid) .\\n}  LIMIT 1")
                                                                        .build())
                                                        .defaultGraphs(Collections.emptyList())
                                                        .build())
                                        .build()
                        ).build();

        ImportService importService = mock(ImportService.class);
        when(importService.readData(configuration)).thenReturn(Collections.emptyList());

        SparqlClient sparqlClient = mock(SparqlClient.class);
        when(sparqlClient.executeQuery(anyString(), any(List.class), any(SparqlEndpointEntity.class)))
                .thenReturn(
                        Optional.of(
                                SparqlResponse.builder()
                                        .head(
                                                SparqlHead.builder()
                                                        .variables(
                                                                Collections.singletonList("column")
                                                        )
                                                        .build())
                                        .results(
                                                SparqlResults.builder()
                                                        .bindings(Collections.emptyList())
                                                        .build())
                                        .build()
                        )
                );

        Map<String, String> selectedNodeData = new HashMap<>();
        selectedNodeData.put("uri", "https://parenturi/");
        selectedNodeData.put("uuid", "urn:uuid:ea7020ae-bde5-cd34-4bec-525400767bfb");

        List<CommonParameter> commonParameters = new ArrayList<>();
        commonParameters.add(CommonParameter.builder().type("literal").value("Chuck Norris").id("username").build());

        QueryExecutionRequest input = QueryExecutionRequest.builder()
                .commonParameters(commonParameters)
                .values(
                        Collections.singletonList(selectedNodeData)
                )
                .build();

        ParameterNodeFactory parameterNodeFactory = mock(ParameterNodeFactory.class);
        when(parameterNodeFactory.createNode("userAccount", "Chuck Norris", "literal")).thenReturn(NodeFactory.createLiteral("Chuck Norris"));
        when(parameterNodeFactory.createNode(eq("parentUri"), anyString(), eq(null)))
                .thenAnswer(invocationOnMock -> NodeFactory.createURI(invocationOnMock.getArgument(1)));
        when(parameterNodeFactory.createNode(eq("parentUuid"), anyString(), eq(null)))
                .thenAnswer(invocationOnMock -> NodeFactory.createURI(invocationOnMock.getArgument(1)));

        VisualizationService visualizationService = new VisualizationService(sparqlClient, mock(SparqlTypeCache.class), parameterNodeFactory, importService);
        QueryResult queryResult = visualizationService.executeChildQuery(configuration, input);

        verify(sparqlClient, VerificationModeFactory.times(1)).executeQuery(eq("PREFIX xsd: <http://www.w3.org/2001/XMLSchema#>\\nPREFIX rdfs: <http://www.w3.org/2000/01/rdf-schema#>\\nPREFIX skos: <http://www.w3.org/2004/02/skos/core#>\\nPREFIX owl: <http://www.w3.org/2002/07/owl#>\\nPREFIX rdf: <http://www.w3.org/1999/02/22-rdf-syntax-ns#>\\nPREFIX asset: <http://dds.semmtech.nl/asset/>\\n    \\nSELECT ?uri ?label ?hasChildren ?uuid ?isImported ?userAccount {\\n    BIND(\"Chuck Norris\" as ?userAccount) .\\n BIND(<https://parenturi/> as ?parentUri) .\\n    BIND(<urn:uuid:ea7020ae-bde5-cd34-4bec-525400767bfb> as ?parentUuid) .\\n    ?uri rdf:type owl:Class ;\\n        rdfs:subClassOf ?parentUri .\\n   \\n    OPTIONAL {\\n        ?uri skos:prefLabel ?label\\n    }\\n     \\n    BIND(EXISTS { ?child rdfs:subClassOf ?uri } as ?hasChildren) .\\n \\n    OPTIONAL {\\n      # VALUES is populated by the result of the XML webservice:\\n      VALUES (?additional_foreignKey) {\\n(<urn:nothing>)}\\n      FILTER (?uri = ?relatics_foreignKey) .\\n      BIND(true as ?inner_imported) .\\n    }\\n    BIND(COALESCE(?inner_imported, false) as ?isImported) . \\n    BIND(UUID() as ?uuid) .\\n}  LIMIT 1"), any(List.class), any());
    }

    @Test
    public void whenExecutingFilterQuery_withoutDefaultGraphs_queryIsRun_resultsReturned() {
        SparqlClient sparqlClient = mock(SparqlClient.class);
        VisualizationService visualizationService = new VisualizationService(sparqlClient, mock(SparqlTypeCache.class), null, null);

        SparqlEndpointEntity endpoint =
                SparqlEndpointEntity.builder()
                        .build();

        AddOnEntity configuration =
                AddOnEntity.builder()
                        .sparqlEndpoint(endpoint)
                        .visualization(
                                VisualizationEntity.builder()
                                        .rootsQuery(
                                                SparqlQueryWithDefaultGraphs.builder()
                                                        .defaultGraphs(null)
                                                        .build()
                                        )
                                        .build()
                        )
                        .build();

        SparqlQueryEntity query =
                SparqlQueryEntity.builder()
                        .query("SPARQL")
                        .build();

        when(sparqlClient.executeQuery("SPARQL", null, endpoint))
                .thenReturn(
                        Optional.of(
                                SparqlResponse.builder()
                                        .head(
                                                SparqlHead.builder()
                                                        .variable("Variable1")
                                                        .variable("Variable2")
                                                        .build()
                                        )
                                        .results(
                                                SparqlResults.builder()
                                                        .bindings(
                                                                List.of(
                                                                        Map.of("Variable1", SparqlBinding.builder().value("value1").build(), "Variable2", SparqlBinding.builder().value("value2").build())
                                                                )
                                                        )
                                                        .build()
                                        )
                                        .build()
                        )
                );

        QueryResult result = visualizationService.executeParameterLessQuery(configuration, query);

        assertThat(result.getValues(),
                hasItem(
                        allOf(
                                hasEntry(equalTo("Variable1"), equalTo("value1")),
                                hasEntry(equalTo("Variable2"), equalTo("value2"))
                        )
                ));

        verify(sparqlClient, times(1)).executeQuery("SPARQL", null, endpoint);

    }

    @Test
    public void whenExecutingFilterQuery_withDefaultGraphs_queryIsRun_resultsReturned() {
        SparqlClient sparqlClient = mock(SparqlClient.class);
        VisualizationService visualizationService = new VisualizationService(sparqlClient, mock(SparqlTypeCache.class), null, null);

        SparqlEndpointEntity endpoint =
                SparqlEndpointEntity.builder()
                        .build();

        AddOnEntity configuration =
                AddOnEntity.builder()
                        .sparqlEndpoint(endpoint)
                        .visualization(
                                VisualizationEntity.builder()
                                        .rootsQuery(
                                                SparqlQueryWithDefaultGraphs.builder()
                                                        .defaultGraphs(List.of("DefaultGraph"))
                                                        .build()
                                        )
                                        .build()
                        )
                        .build();

        SparqlQueryEntity query =
                SparqlQueryEntity.builder()
                        .query("SPARQL")
                        .build();

        when(sparqlClient.executeQuery("SPARQL", List.of("DefaultGraph"), endpoint))
                .thenReturn(
                        Optional.of(
                                SparqlResponse.builder()
                                        .head(
                                                SparqlHead.builder()
                                                        .variable("Variable1")
                                                        .variable("Variable2")
                                                        .build()
                                        )
                                        .results(
                                                SparqlResults.builder()
                                                        .bindings(
                                                                List.of(
                                                                        Map.of("Variable1", SparqlBinding.builder().value("value1").build(), "Variable2", SparqlBinding.builder().value("value2").build())
                                                                )
                                                        )
                                                        .build()
                                        )
                                        .build()
                        )
                );

        QueryResult result = visualizationService.executeParameterLessQuery(configuration, query);

        assertThat(result.getValues(),
                hasItem(
                        allOf(
                                hasEntry(equalTo("Variable1"), equalTo("value1")),
                                hasEntry(equalTo("Variable2"), equalTo("value2"))
                        )
                ));

        verify(sparqlClient, times(1)).executeQuery("SPARQL", List.of("DefaultGraph"), endpoint);

    }
}
