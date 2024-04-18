package com.semmtech.laces.fetch.sparql;

import com.semmtech.laces.fetch.configuration.entities.SparqlEndpointEntity;
import java.nio.charset.StandardCharsets;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections.CollectionUtils;
import org.apache.jena.query.QueryExecException;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

@Component
@Slf4j
public class SparqlClient {

    private final RestTemplate restTemplate;

    public SparqlClient(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    public Optional<SparqlResponse> executeQuery(String query, List<String> defaultGraphs, SparqlEndpointEntity configuration) {
        return execute(query, defaultGraphs, configuration, SparqlResponse.class);
    }

    public Optional<String> executeQueryRaw(String query, List<String> defaultGraphs, SparqlEndpointEntity configuration) {
        return execute(query, defaultGraphs, configuration, String.class);
    }

    private <T> Optional<T> execute(String query, List<String> defaultGraphs, SparqlEndpointEntity configuration, Class<T> type) {

        var uriComponentsBuilder = UriComponentsBuilder.fromUriString(configuration.getUrl());
        if (CollectionUtils.isNotEmpty(defaultGraphs)) {
            uriComponentsBuilder.queryParam("default-graph-uri", defaultGraphs.toArray());
        }
        String url = uriComponentsBuilder.toUriString();

        HttpHeaders headers = configuration.getAuthenticationMethod().headers(url);
        headers.setAccept(Collections.singletonList(MediaType.valueOf("application/sparql-results+json")));
        headers.setContentType(MediaType.valueOf("application/sparql-query"));

        HttpEntity<String> entity = new HttpEntity<>(query, headers);

        try {
            ResponseEntity<T> response = restTemplate.postForEntity(url, entity, type);
            if (!response.getStatusCode().is2xxSuccessful()) {
                log.error(response.toString());
            }
            return response.getStatusCode().is2xxSuccessful() ? Optional.ofNullable(response.getBody()) : Optional.empty();
        } catch (HttpClientErrorException ex) {
            throw new HttpClientErrorException(ex.getStatusCode(), ex.getStatusText(), (ex.getResponseBodyAsString() + "\\n Query: " + query).getBytes(StandardCharsets.UTF_8), StandardCharsets.UTF_8);
        } catch (Exception ex) {
            throw new QueryExecException(ex.getMessage() + "\\n Query: " + query);
        }
    }

}
