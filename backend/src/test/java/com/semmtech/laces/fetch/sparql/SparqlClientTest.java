package com.semmtech.laces.fetch.sparql;

import com.semmtech.laces.fetch.config.RestClientConfig;
import com.semmtech.laces.fetch.configuration.entities.PublicAuthenticationEntity;
import com.semmtech.laces.fetch.configuration.entities.SparqlEndpointEntity;
import org.hamcrest.Matcher;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.autoconfigure.web.client.RestClientTest;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.client.MockRestServiceServer;
import org.springframework.test.web.client.response.DefaultResponseCreator;
import org.springframework.test.web.client.response.MockRestResponseCreators;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.function.Consumer;
import java.util.function.Supplier;

import static org.hamcrest.Matchers.*;
import static org.junit.Assert.assertThat;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.method;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.requestTo;
import static org.springframework.test.web.client.response.MockRestResponseCreators.withSuccess;

@RunWith(SpringRunner.class)
@RestClientTest({SparqlClient.class, RestClientConfig.class})
public class SparqlClientTest {

    private static final String LITERAL = "literal";
    private static final String TYPED_LITERAL = "typed-literal";
    private static final String PLAIN_LITERAL = "plain-literal";
    private static final String LABEL = "label";
    private static final String URI = "uri";
    private static final String ENDPOINT = "/endpoint";
    private static final String HAS_CHILDREN = "hasChildren";


    @Test
    public void executeQuery_withDefaultGraphs_parametersAdded() {
        RestTemplate restTemplate = new RestTemplate();
        SparqlClient sparqlClient = new SparqlClient(restTemplate);

        MockRestServiceServer server = MockRestServiceServer.bindTo(restTemplate).build();

        server.expect(requestTo(ENDPOINT + "?default-graph-uri=graph1&default-graph-uri=graph2"))
                .andExpect(method(HttpMethod.POST))
                .andRespond(withSuccess());

        SparqlEndpointEntity configuration =
                SparqlEndpointEntity.builder()
                        .url(ENDPOINT)
                        .authenticationMethod(new PublicAuthenticationEntity())
                        .build();

        sparqlClient.executeQuery("test", List.of("graph1", "graph2"), configuration);

        server.verify();
    }

    @Test
    public void executeQuery_returningBooleanLiteral_parsedCorrectly() {
        Matcher<SparqlBinding> bindingMatcher = getValueMatcher(equalTo("false"), equalTo(LITERAL), equalTo("http://www.w3.org/2001/XMLSchema#boolean"), nullValue());

        executeValidQuery(bindingMatcher, RESPONSE_WITH_BOOLEAN_LITTERAL, HAS_CHILDREN);
    }

    @Test
    public void executeQuery_returningDutchLiteral_parsedCorrectly() {
        Matcher<SparqlBinding> valueMatcher = getValueMatcher(equalTo("Bouwwerk"), equalTo(LITERAL), nullValue(), equalTo("nl"));

        executeValidQuery(valueMatcher, RESPONSE_WITH_DUTCH_LITTERAL, LABEL);
    }

    @Test
    public void executeQuery_returningPlainLiteral_parsedCorrectly() {
        Matcher<SparqlBinding> valueMatcher = getValueMatcher(equalTo("Bouwwerk"), equalTo(PLAIN_LITERAL), nullValue(), equalTo("nl"));

        executeValidQuery(valueMatcher, RESPONSE_WITH_PLAIN_LITTERAL, LABEL);
    }

    @Test
    public void executeQuery_returningPlainLiteral_NoLang_parsedCorrectly() {
        Matcher<SparqlBinding> valueMatcher = getValueMatcher(equalTo("Bouwwerk"), equalTo(PLAIN_LITERAL), nullValue(), nullValue());

        executeValidQuery(valueMatcher, RESPONSE_WITH_PLAIN_LITTERAL_NOLANG, LABEL);
    }

    @Test
    public void executeQuery_returningTypedLiteral_parsedCorrectly() {
        Matcher<SparqlBinding> valueMatcher = getValueMatcher(equalTo("123"), equalTo(TYPED_LITERAL), equalTo("http://www.w3.org/2001/XMLSchema#integer"), nullValue());

        executeValidQuery(valueMatcher, RESPONSE_WITH_TYPED_LITERAL, LABEL);
    }

    @Test
    public void executeQuery_returningURI_parsedCorrectly() {
        Matcher<SparqlBinding> valueMatcher = getValueMatcher(equalTo("http://dds.semmtech.nl/asset/Gebouw"), equalTo(URI), nullValue(), nullValue());

        executeValidQuery(valueMatcher, RESPONSE_WITH_URI, URI);
    }

    @Test(expected = HttpClientErrorException.class)
    public void executeQuery_receive400_throwsException() {
        executeQueryAndValidate(
                () -> MockRestResponseCreators.withBadRequest().body("Invalid request"),
                response -> {});
    }

    private void executeValidQuery(Matcher<SparqlBinding> valueMatcher, String serverResponse, String uri) {
        executeQueryAndValidate(
                () -> withSuccess(serverResponse, MediaType.valueOf("application/sparql-results+json; charset=UTF-8")),
                response -> validateSuccessfulQuery(valueMatcher, uri, response)
        );
    }

    private Matcher<SparqlBinding> getValueMatcher(Matcher<String> valueMatcher, Matcher<String> typeMatcher, Matcher<Object> datatypeMatcher, Matcher<Object> languageMatcher) {
        return allOf(
                hasProperty("value", valueMatcher),
                hasProperty("type", typeMatcher),
                hasProperty("datatype", datatypeMatcher),
                hasProperty("language", languageMatcher)
        );
    }

    private void executeQueryAndValidate(Supplier<DefaultResponseCreator> serverResponseSupplier, Consumer<Optional<SparqlResponse>> interpretedResponseValidator) {
        Optional<SparqlResponse> response = executeQuery(serverResponseSupplier);

        interpretedResponseValidator.accept(response);
    }

    private void validateSuccessfulQuery(Matcher<SparqlBinding> valueMatcher, String variableName, Optional<SparqlResponse> response) {
        assertThat(response.isPresent(), is(equalTo(true)));

        assertThat(
                response.get(),
                hasProperty("head",
                        hasProperty("variables",
                                hasItem(equalTo(variableName))
                        )
                )
        );

        assertThat(
                response.get().getResults().bindings.get(0).get(variableName),
                valueMatcher
        );
    }

    private Optional<SparqlResponse> executeQuery(Supplier<DefaultResponseCreator> responseSupplier) {
        RestTemplate restTemplate = new RestTemplate();
        SparqlClient sparqlClient = new SparqlClient(restTemplate);

        MockRestServiceServer server = MockRestServiceServer.bindTo(restTemplate).build();

        server.expect(requestTo(ENDPOINT))
                .andExpect(method(HttpMethod.POST))
                .andRespond(responseSupplier.get());

        SparqlEndpointEntity configuration =
                SparqlEndpointEntity.builder()
                        .url(ENDPOINT)
                        .authenticationMethod(new PublicAuthenticationEntity())
                        .build();

        return sparqlClient.executeQuery("some query", Collections.emptyList(), configuration);
    }

    private final String RESPONSE_WITH_BOOLEAN_LITTERAL =
            buildResponse(HAS_CHILDREN,
                    "                \"hasChildren\": {\n" +
                    "                    \"type\": \"literal\",\n" +
                    "                    \"datatype\": \"http://www.w3.org/2001/XMLSchema#boolean\",\n" +
                    "                    \"value\": \"false\"\n");

    private final String RESPONSE_WITH_DUTCH_LITTERAL =
            buildResponse(
                    LABEL,
                    "                \"label\": {\n" +
                    "                    \"type\": \"literal\",\n" +
                    "                    \"xml:lang\": \"nl\",\n" +
                    "                    \"value\": \"Bouwwerk\"\n");

    private final String RESPONSE_WITH_PLAIN_LITTERAL =
            buildResponse(
                    LABEL,
                    "                \"label\": {\n" +
                            "                    \"type\": \"plain-literal\",\n" +
                            "                    \"xml:lang\": \"nl\",\n" +
                            "                    \"value\": \"Bouwwerk\"\n");

    private final String RESPONSE_WITH_PLAIN_LITTERAL_NOLANG =
            buildResponse(
                    LABEL,
                    "                \"label\": {\n" +
                            "                    \"type\": \"plain-literal\",\n" +
                            "                    \"value\": \"Bouwwerk\"\n");

    private final String RESPONSE_WITH_TYPED_LITERAL =
            buildResponse(
                    LABEL,
                    "                \"label\": {\n" +
                            "                    \"type\": \"typed-literal\",\n" +
                            "                    \"datatype\": \"http://www.w3.org/2001/XMLSchema#integer\",\n" +
                            "                    \"value\": \"123\"\n");

    private final String RESPONSE_WITH_URI =
            buildResponse(
                    URI,
                    "                \"uri\": {\n" +
                    "                        \"type\": \"uri\",\n" +
                    "                        \"value\": \"http://dds.semmtech.nl/asset/Gebouw\"\n");


    private String buildResponse(String variableName, String value) {
        return  "{ " +
                "    \"head\": {\n" +
                "        \"vars\": [\n" +
                "            \""+ variableName +"\"\n" +
                "        ]\n" +
                "    },\n" +
                "    \"results\": {\n" +
                "        \"bindings\": [\n" +
                "            {\n" +
                value +
                "                }" +
                "            }" +
                "        ]\n" +
                "    }\n" +
                "}";
    }
}

