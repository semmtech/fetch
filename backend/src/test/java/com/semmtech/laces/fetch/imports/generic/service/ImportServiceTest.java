package com.semmtech.laces.fetch.imports.generic.service;

import com.semmtech.laces.fetch.restclient.OutboundRESTRequestLogger;
import com.semmtech.laces.fetch.configuration.entities.*;
import com.semmtech.laces.fetch.configuration.service.GenericService;
import com.semmtech.laces.fetch.configuration.service.JsonApiService;
import com.semmtech.laces.fetch.configuration.service.RelaticsEnvironmentService;
import com.semmtech.laces.fetch.configuration.service.RelaticsService;
import com.semmtech.laces.fetch.imports.generic.model.GenericImportResponse;
import com.semmtech.laces.fetch.imports.jsonapi.service.BIMPortalImportResponse;
import com.semmtech.laces.fetch.imports.jsonapi.service.JSONAPIClient;
import com.semmtech.laces.fetch.imports.relatics.service.ImportDataXmlProvider;
import com.semmtech.laces.fetch.imports.relatics.service.WebserviceClient;
import com.semmtech.laces.fetch.imports.relatics.model.ImportResponse;
import com.semmtech.laces.fetch.imports.relatics.service.RelaticsClient;
import com.semmtech.laces.fetch.sparql.*;
import com.semmtech.laces.fetch.visualization.model.QueryExecutionRequest;
import org.apache.commons.lang3.StringUtils;
import org.hamcrest.Matcher;
import org.jooq.lambda.Seq;
import org.jooq.lambda.tuple.Tuple2;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.ArgumentMatcher;
import org.mockito.ArgumentMatchers;
import org.mockito.Mockito;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.http.client.ClientHttpRequestInterceptor;
import org.springframework.web.client.RestTemplate;

import java.util.*;
import java.util.function.Consumer;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import static org.hamcrest.Matchers.*;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.*;

public class ImportServiceTest {

    private static final String TOTAL_ROWS_IMPORTED_4 = "Total rows imported: 4";

    @Test
    public void whenSuccessfullyImporting_intoRelaticsTarget_successMessageReturnedAndRelaticsClientCalled() {
        SparqlClient sparqlClient = mock(SparqlClient.class);
        WebserviceClient relaticsClient = mock(WebserviceClient.class);

        RelaticsEnvironmentService environmentService = mock(RelaticsEnvironmentService.class);
        EnvironmentEntity environment = EnvironmentEntity.builder().serviceUrl("url").build();
        when(environmentService.get("environmentId")).thenReturn(Optional.of(environment));


        RelaticsService relaticsService = mock(RelaticsService.class);
        ImportService service = prepareServiceLayer(sparqlClient, relaticsClient, new RestTemplateBuilder(), environmentService, relaticsService, null, null);

        String[] queries = fourTimes().mapToObj(this::createExecutedSparql).toArray(String[]::new);
        final var defaultGraphs = List.of("http://some.graph.url");
        ImportStepEntity step = prepareImportStep(defaultGraphs);

        AddOnEntity configuration = prepareAddOnConfiguration(step, EnvironmentType.Relatics, defaultGraphs);
        prepareStandardQueryResponses(sparqlClient, queries, configuration, defaultGraphs);

        when(relaticsService.getRequiredWorkspaceForConfiguration(any(AddOnEntity.class)))
                .thenReturn(
                        WorkspaceEntity.builder()
                                .id("workspaceId")
                                .workspaceId("wid")
                                .environmentId("environmentId")
                                .build()
                );
        when(relaticsService.getServiceUrl(configuration)).thenReturn("url");
        final var target = TargetDataSystemEntity.builder().build();
        when(relaticsService.getTargetDataSystem("webservice")).thenReturn(target);

        prepareRelaticsImportResponse(relaticsClient, step, configuration, target);

        QueryExecutionRequest request =
                QueryExecutionRequest.builder()
                        .values(createRequestValues())
                        .build();

        GenericImportResponse response = service.sendData(request, step, configuration, new HashMap<>());
        assertThat(response,
                allOf(
                        hasProperty("importStep", equalTo("First step")),
                        hasProperty("success", equalTo(true)),
                        hasProperty("successMessage", equalTo(TOTAL_ROWS_IMPORTED_4))
                )
        );

        verifyQueriesExecuted(sparqlClient, queries, configuration, defaultGraphs);
        verify(relaticsClient, times(1)).sendData(eq(step), argThat(workspace -> "workspaceId".equals(workspace.getId())), argThat(new UriAndUUIDMatcher()), eq("url"), any(TargetDataSystemEntity.class));
    }

    @Test
    public void whenSuccessfullyImporting_intoJSONAPI_successMessageReturnedAndJSONAPIClientCalled() {
        SparqlClient sparqlClient = mock(SparqlClient.class);
        WebserviceClient relaticsClient = mock(WebserviceClient.class);
        RestTemplate template = mock(RestTemplate.class);
        RestTemplateBuilder templateBuilder = mock(RestTemplateBuilder.class);
        when(templateBuilder.interceptors(ArgumentMatchers.any(ClientHttpRequestInterceptor.class))).thenReturn(templateBuilder);
        when(templateBuilder.build()).thenReturn(template);

        EnvironmentEntity environment = EnvironmentEntity.builder().serviceUrl("url").build();
        RelaticsEnvironmentService environmentService = mock(RelaticsEnvironmentService.class);
        when(environmentService.get("environmentId")).thenReturn(Optional.of(environment));

        JsonApiService jsonApiService = mock(JsonApiService.class);
        when(jsonApiService.get("workspaceId")).thenReturn(Optional.of(new JsonApiEntity("id", "", "url")));

        GenericService<JsonApiEndpointEntity> jsonApiEndpointService = mock(GenericService.class);

        ImportService service = prepareServiceLayer(sparqlClient, relaticsClient, templateBuilder, environmentService, null, jsonApiService, jsonApiEndpointService);
        String[] queries = fourTimes().mapToObj(this::createExecutedSparql).toArray(String[]::new);

        final var defaultGraphs = List.of("http://some.graph.url");
        ImportStepEntity step = prepareImportStep(defaultGraphs);

        when(jsonApiEndpointService.get("webservice")).thenReturn(Optional.of(
                JsonApiEndpointEntity.builder().path("operation").build()
        ));

        AddOnEntity configuration = prepareAddOnConfiguration(step, EnvironmentType.JSON_API, defaultGraphs);
        prepareStandardQueryResponses(sparqlClient, queries, configuration, defaultGraphs);

        QueryExecutionRequest request =
                QueryExecutionRequest.builder()
                        .values(createRequestValues())
                        .build();

        when(template.exchange(eq("url/operation"), eq(HttpMethod.POST), any(), eq(BIMPortalImportResponse.class)))
                .thenReturn(ResponseEntity.ok(BIMPortalImportResponse.builder().result(List.of("id1", "id2", "id3", "id4")).build()));

        GenericImportResponse response = service.sendData(request, step, configuration, new HashMap<>());
        assertThat(response,
                allOf(
                        hasProperty("importStep", equalTo("First step")),
                        hasProperty("success", equalTo(true)),
                        hasProperty("successMessage", equalTo("Successfully created 4 instances."))
                )
        );

        verifyQueriesExecuted(sparqlClient, queries, configuration, defaultGraphs);
        ArgumentCaptor<HttpEntity<List<Map<String, String>>>> postEntity = ArgumentCaptor.forClass(HttpEntity.class);
        verify(template, times(1)).exchange(eq("url/operation"), eq(HttpMethod.POST), postEntity.capture(), eq(BIMPortalImportResponse.class));

        List<Map<String, String>> postedBody = postEntity.getValue().getBody();
        assertThat(postedBody, allOf(fourTimes().mapToObj(this::hasMatchingItem).toArray(Matcher[]::new)));

    }

    @Test
    public void whenSuccessfullyImportingWithPreviousIdentifiers_intoJSONAPI_successMessageReturnedAndJSONAPIClientCalled() {
        SparqlClient sparqlClient = mock(SparqlClient.class);
        WebserviceClient relaticsClient = mock(WebserviceClient.class);
        RestTemplate template = mock(RestTemplate.class);
        RestTemplateBuilder templateBuilder = mock(RestTemplateBuilder.class);
        when(templateBuilder.interceptors(ArgumentMatchers.any(ClientHttpRequestInterceptor.class))).thenReturn(templateBuilder);
        when(templateBuilder.build()).thenReturn(template);

        EnvironmentEntity environment = EnvironmentEntity.builder().serviceUrl("url").build();
        RelaticsEnvironmentService environmentService = mock(RelaticsEnvironmentService.class);
        when(environmentService.get("environmentId")).thenReturn(Optional.of(environment));

        JsonApiService jsonApiService = mock(JsonApiService.class);
        when(jsonApiService.get("workspaceId")).thenReturn(Optional.of(new JsonApiEntity("id", "", "url")));

        GenericService<JsonApiEndpointEntity> jsonApiEndpointService = mock(GenericService.class);
        ImportService service = prepareServiceLayer(sparqlClient, relaticsClient, templateBuilder, environmentService, null, jsonApiService, jsonApiEndpointService);
        String[] queries = fourTimes().mapToObj(i -> createExecutedSparqlWithImporteds(i)).toArray(String[]::new);

        ImportStepEntity step = prepareImportStepWithImporteds();

        AddOnEntity configuration = prepareAddOnConfiguration(step, EnvironmentType.JSON_API, new ArrayList<>());
        prepareStandardQueryResponsesWithQueriedIdentifiers(sparqlClient, queries, configuration);

        QueryExecutionRequest request =
                QueryExecutionRequest.builder()
                        .values(createRequestValues())
                        .build();

        when(template.exchange(eq("url/operation"), eq(HttpMethod.POST), any(), eq(BIMPortalImportResponse.class)))
                .thenReturn(ResponseEntity.ok(BIMPortalImportResponse.builder().result(List.of("id1", "id2", "id3", "id4")).build()));

        var linkedUuidsAndUrisByUuid = new HashMap<String, List<Map<String, String>>>();
        fourTimes().forEach(i -> prepareIdsForUuid(linkedUuidsAndUrisByUuid, i));

        when(jsonApiEndpointService.get("webservice")).thenReturn(Optional.of(
                JsonApiEndpointEntity.builder().path("operation").build()
        ));

        GenericImportResponse response = service.sendData(request, step, configuration, linkedUuidsAndUrisByUuid);
        assertThat(response,
                allOf(
                        hasProperty("importStep", equalTo("First step")),
                        hasProperty("success", equalTo(true)),
                        hasProperty("successMessage", equalTo("Successfully created 4 instances."))
                )
        );

        verifyQueriesExecuted(sparqlClient, queries, configuration, new ArrayList<>());
        ArgumentCaptor<HttpEntity<List<Map<String, String>>>> postEntity = ArgumentCaptor.forClass(HttpEntity.class);
        verify(template, times(1)).exchange(eq("url/operation"), eq(HttpMethod.POST), postEntity.capture(), eq(BIMPortalImportResponse.class));

        List<Map<String, String>> postedBody = postEntity.getValue().getBody();
        assertThat(postedBody, allOf(fourTimes().mapToObj(this::hasMatchingItemWitQueried).toArray(Matcher[]::new)));

        fourTimes().forEach(i -> validateLinkedMapIsUpdatedForResult(linkedUuidsAndUrisByUuid, i));
    }

    @Test
    public void whenUnsuccessfulImporting() {
        SparqlClient sparqlClient = mock(SparqlClient.class);
        WebserviceClient relaticsClient = mock(WebserviceClient.class);

        RelaticsEnvironmentService environmentService = mock(RelaticsEnvironmentService.class);
        EnvironmentEntity environment = EnvironmentEntity.builder().serviceUrl("url").build();
        when(environmentService.get("environmentId")).thenReturn(Optional.of(environment));


        RelaticsService relaticsService = mock(RelaticsService.class);
        ImportService service = prepareServiceLayer(sparqlClient, relaticsClient, new RestTemplateBuilder(), environmentService, relaticsService, null, null);

        String[] queries = fourTimes().mapToObj(this::createExecutedSparql).toArray(String[]::new);
        final var defaultGraphs = List.of("http://some.graph.url");
        ImportStepEntity step = prepareImportStep(defaultGraphs);

        AddOnEntity configuration = prepareAddOnConfiguration(step, EnvironmentType.Relatics, defaultGraphs);
        prepareStandardQueryResponses(sparqlClient, queries, configuration, defaultGraphs);

        when(relaticsService.getRequiredWorkspaceForConfiguration(any(AddOnEntity.class)))
                .thenReturn(
                        WorkspaceEntity.builder()
                                .id("workspaceId")
                                .workspaceId("wid")
                                .environmentId("environmentId")
                                .build()
                );
        when(relaticsService.getServiceUrl(configuration)).thenReturn("");
        final var target = TargetDataSystemEntity.builder().build();
        when(relaticsService.getTargetDataSystem("webservice")).thenReturn(target);

        prepareRelaticsImportResponse(relaticsClient, step, configuration, target);

        QueryExecutionRequest request =
                QueryExecutionRequest.builder()
                        .values(createRequestValues())
                        .build();

        GenericImportResponse response = service.sendData(request, step, configuration, new HashMap<>());
        assertThat(response,
                allOf(
                        hasProperty("importStep", equalTo("First step")),
                        hasProperty("success", equalTo(false)),
                        hasProperty("errors", equalTo(Collections.singletonList("Unexpected error occurred. Please contact your administrator")))
                )
        );

    }

    public IntStream fourTimes() {
        return IntStream.rangeClosed(1, 4);
    }

    public void validateLinkedMapIsUpdatedForResult(HashMap<String, List<Map<String, String>>> linkedUuidsAndUrisByUuid, int number) {
        final var actualList = linkedUuidsAndUrisByUuid.get("uuid" + number);
        assertThat(actualList,
                allOf(
                        hasItem(
                                allOf(
                                        instanceOf(Map.class),
                                        hasEntry("key", "value" + number)
                                )
                        ),
                        hasItem(
                                allOf(
                                        instanceOf(Map.class),
                                        hasEntry("queriedForeignKey", "queried" + number)
                                )
                        )
                )
        );
    }

    public Matcher<Iterable<? super Map<String, String>>> hasMatchingItem(int number) {
        return hasItem(
                allOf(
                        hasEntry("parentForeignKey", "uri" + number),
                        hasEntry("childForeignKey", "uuid" + number)
                )
        );
    }

    public Matcher<Iterable<? super Map<String, String>>> hasMatchingItemWitQueried(int number) {
        return hasItem(
                allOf(
                        hasEntry("parentForeignKey", "uri" + number),
                        hasEntry("childForeignKey", "uuid" + number),
                        hasEntry("queriedForeignKey", "queried" + number),
                        hasEntry("importedForeignKey", "value" + number)
                )
        );
    }

    public void prepareIdsForUuid(HashMap<String, List<Map<String, String>>> linkedUuidsAndUrisByUuid, int number) {
        var idsForUuid = new HashMap<String, String>();
        idsForUuid.put("key", "value" + number);
        List<Map<String, String>> list = new ArrayList<>();
        list.add(idsForUuid);
        linkedUuidsAndUrisByUuid.put("uuid" + number, list);
    }

    public void verifyQueriesExecuted(SparqlClient sparqlClient, String[] queries, AddOnEntity configuration, List<String> defaultGraphs) {
        Arrays.stream(queries)
                .forEach(query -> verify(sparqlClient, Mockito.times(1)).executeQuery(query, defaultGraphs, configuration.getSparqlEndpoint()));
    }

    public void prepareRelaticsImportResponse(WebserviceClient relaticsClient, ImportStepEntity step, AddOnEntity configuration, TargetDataSystemEntity target) {
        ImportResponse.ImportResult.Import.Message message = new ImportResponse.ImportResult.Import.Message();
        message.setResult("Progress");
        message.setValue(TOTAL_ROWS_IMPORTED_4);

        ImportResponse importResponse = new ImportResponse();
        importResponse.setImportResult(new ImportResponse.ImportResult());
        importResponse.getImportResult().setImport(new ImportResponse.ImportResult.Import());
        importResponse.getImportResult().getImport().getMessage().add(message);

        when(relaticsClient.sendData(
                eq(step),
                argThat(workspace -> "workspaceId".equals(workspace.getId())),
                argThat(new UriAndUUIDMatcher()),
                eq("url"),
                eq(target)))
                .thenReturn(importResponse);
    }

    public AddOnEntity prepareAddOnConfiguration(ImportStepEntity step, EnvironmentType environmentType, List<String> defaultGraphs) {
        AddOnEntity configuration =
                AddOnEntity.builder()
                        .importConfiguration(
                                ImportEntity.builder()
                                        .steps(Collections.singletonList(step))
                                        .build()
                        )
                        .sparqlEndpoint(
                                SparqlEndpointEntity.builder()
                                        .url("url")
                                        .build()
                        )
                        .targetType(environmentType.name())
                        .dataTarget("workspaceId")
                        .visualization(
                                VisualizationEntity.builder()
                                        .build()
                        )
                        .build();


        return configuration;
    }

    private void prepareStandardQueryResponses(SparqlClient sparqlClient, String[] queries, AddOnEntity configuration, List<String> defaultGraphs) {
        prepareResponses(
                queries,
                queryWithIndex ->
                        when(sparqlClient.executeQuery(queryWithIndex.v1, defaultGraphs, configuration.getSparqlEndpoint()))
                                .thenReturn(Optional.of(createSparqlResponse(queryWithIndex.v2 + 1))));
    }

    private void prepareStandardQueryResponsesWithQueriedIdentifiers(SparqlClient sparqlClient, String[] queries, AddOnEntity configuration) {
        prepareResponses(
                queries,
                queryWithIndex ->
                        when(sparqlClient.executeQuery(queryWithIndex.v1, new ArrayList<>(), configuration.getSparqlEndpoint()))
                                .thenReturn(Optional.of(createSparqlResponseWithQueriedIdentifiers(queryWithIndex.v2 + 1))));
    }

    private void prepareResponses(String[] queries, Consumer<Tuple2<String, Long>> queryWithIndexConsumer) {
        Seq.of(queries)
                .zipWithIndex()
                .forEach(queryWithIndexConsumer);
    }

    private ImportStepEntity prepareImportStep(List<String> defaultGraphs) {
        final var webservice = TargetDataSystemEntity.builder()
                .entryCode("123")
                .operationName("operation")
                .build();
        return ImportStepEntity.builder()
                .name("First step")
                .importTarget("webservice")
                .sparqlQuery(
                        SparqlQueryWithDefaultGraphs
                                .builder()
                                .query(
                                        SparqlQueryEntity
                                                .builder()
                                                .query(
                                                        "SELECT ?parentForeignKey ?childForeignKey\n" +
                                                                "{\n" +
                                                                "    BIND(?uri as ?parentForeignKey)\n" +
                                                                "    BIND(?uuid as ?childForeignKey)\n" +
                                                                "}")
                                                .build())
                                .defaultGraphs(defaultGraphs)
                                .build())
                .build();
    }

    private ImportStepEntity prepareImportStepWithImporteds() {
        final var webservice = TargetDataSystemEntity.builder()
                .entryCode("123")
                .operationName("operation")
                .build();
        return ImportStepEntity.builder()
                .name("First step")
                .importTarget("webservice")
                .sparqlQuery(
                        SparqlQueryWithDefaultGraphs
                                .builder()
                                .query(
                                        SparqlQueryEntity
                                                .builder()
                                                .query(
                                                        "SELECT ?parentForeignKey ?childForeignKey ?queriedForeignKey\n" +
                                                                "{\n" +
                                                                "    BIND(?uri as ?parentForeignKey)\n" +
                                                                "    BIND(?uuid as ?childForeignKey)\n" +
                                                                "    BIND(?imported_key as ?importedForeignKey)\n" +
                                                                "   VALUES (?imported_key) {\n" +
                                                                "       ?import_placeholder \n" +
                                                                "   } \n" +
                                                                "    BIND(?queriedUri as ?queriedForeignKey)\n" +
                                                                "}")
                                                .build())
                                .defaultGraphs(new ArrayList<>())
                                .build())
                .build();
    }

    public ImportService prepareServiceLayer(SparqlClient sparqlClient, WebserviceClient relaticsClient, RestTemplateBuilder restTemplateBuilder, RelaticsEnvironmentService environmentService, RelaticsService relaticsService, JsonApiService jsonApiService, GenericService<JsonApiEndpointEntity> jsonApiEndpointService) {
        ParameterNodeFactory parameterNodeFactory = new ParameterNodeFactory(new SparqlTypeCache());
        ImportService service = new ImportService(sparqlClient, parameterNodeFactory, null, null, new SparqlQueryUtils(), environmentService);
        ImportDataXmlProvider xmlProvider = new ImportDataXmlProvider();
        RelaticsClient importService = new RelaticsClient(service, relaticsClient, relaticsService, xmlProvider);
        var logger = new OutboundRESTRequestLogger();
        JSONAPIClient secondClient = new JSONAPIClient(service, restTemplateBuilder, logger, jsonApiService, jsonApiEndpointService);
        return service;
    }

    private List<Map<String, String>> createRequestValues() {
        return fourTimes()
                .mapToObj(this::createRecord)
                .collect(Collectors.toList());

    }

    private Map<String, String> createRecord(int number) {
        Map<String, String> record = new HashMap<>();
        record.put("uri", "uri" + number);
        record.put("uuid", "uuid" + number);

        return record;
    }

    private String createExecutedSparql(int number) {
        return "SELECT ?parentForeignKey ?childForeignKey\n" +
                "{\n" +
                "    BIND(<uri" + number + "> as ?parentForeignKey)\n" +
                "    BIND(<uuid" + number + "> as ?childForeignKey)\n" +
                "}";
    }

    private String createExecutedSparqlWithImporteds(int number) {
        return "SELECT ?parentForeignKey ?childForeignKey ?queriedForeignKey\n" +
                "{\n" +
                "    BIND(<uri" + number + "> as ?parentForeignKey)\n" +
                "    BIND(<uuid" + number + "> as ?childForeignKey)\n" +
                "    BIND(?imported_key as ?importedForeignKey)\n" +
                "   VALUES (?imported_key) {\n" +
                "       (<value" + number + ">) \n" +
                "   } \n" +
                "    BIND(?queriedUri as ?queriedForeignKey)\n" +
                "}";
    }

    private SparqlResponse createSparqlResponse(long number) {
        List<Map<String, SparqlBinding>> returnData = new ArrayList<>();
        Map<String, SparqlBinding> response = new HashMap<>();
        response.put("parentForeignKey",
                SparqlBinding.builder()
                        .type("uri")
                        .value("uri" + number)
                        .build());
        response.put("childForeignKey",
                SparqlBinding.builder()
                        .type("uri")
                        .value("uuid" + number)
                        .build()
        );
        returnData.add(response);

        return SparqlResponse.builder()
                .head(SparqlHead.builder()
                        .variables(Arrays.asList("parentForeignKey", "childForeignKey"))
                        .build())
                .results(
                        SparqlResults.builder()
                                .bindings(
                                        returnData
                                )
                                .build()
                )
                .build();
    }

    private SparqlResponse createSparqlResponseWithQueriedIdentifiers(long number) {
        List<Map<String, SparqlBinding>> returnData =
                List.of(
                        Map.of(
                                "parentForeignKey",
                                SparqlBinding.builder()
                                        .type("uri")
                                        .value("uri" + number)
                                        .build(),
                                "childForeignKey",
                                SparqlBinding.builder()
                                        .type("uri")
                                        .value("uuid" + number)
                                        .build(),
                                "importedForeignKey",
                                SparqlBinding.builder()
                                        .type("uri")
                                        .value("value" + number)
                                        .build(),
                                "queriedForeignKey",
                                SparqlBinding.builder()
                                        .type("uri")
                                        .value("queried" + number)
                                        .build()
                        )
                );

        return SparqlResponse.builder()
                .head(SparqlHead.builder()
                        .variables(Arrays.asList("parentForeignKey", "childForeignKey", "importedForeignKey", "queriedForeignKey"))
                        .build())
                .results(
                        SparqlResults.builder()
                                .bindings(
                                        returnData
                                )
                                .build()
                )
                .build();
    }

    private static class UriAndUUIDMatcher implements ArgumentMatcher<String> {
        @Override
        public boolean matches(String message) {
            return StringUtils.contains(message, "uri1") && StringUtils.contains(message, "uuid1")
                    && StringUtils.contains(message, "uri2") && StringUtils.contains(message, "uuid2")
                    && StringUtils.contains(message, "uri3") && StringUtils.contains(message, "uuid3")
                    && StringUtils.contains(message, "uri4") && StringUtils.contains(message, "uuid4");

        }
    }
}
