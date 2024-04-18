package com.semmtech.laces.fetch.restclient;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpRequest;
import org.springframework.http.client.ClientHttpRequestExecution;
import org.springframework.http.client.ClientHttpRequestInterceptor;
import org.springframework.http.client.ClientHttpResponse;

import java.io.IOException;

@Slf4j
public class OutboundRESTRequestLogger implements ClientHttpRequestInterceptor {

    @Override
    public ClientHttpResponse intercept(
            HttpRequest request, byte[] body,
            ClientHttpRequestExecution execution) throws IOException {

        logRequestDetails(request, body);
        return execution.execute(request, body);
    }
    private void logRequestDetails(HttpRequest request, byte[] body) {
        log.info("Headers: {}", request.getHeaders());
        log.info("Request Method: {}", request.getMethod());
        log.info("Request URI: {}", request.getURI());
        log.info("Request body: {}", new String(body));
    }
}
