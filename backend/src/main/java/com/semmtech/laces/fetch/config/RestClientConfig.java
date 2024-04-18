package com.semmtech.laces.fetch.config;

import com.semmtech.laces.fetch.restclient.OutboundRESTRequestLogger;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestTemplate;

@Configuration
public class RestClientConfig {

    @Bean
    public OutboundRESTRequestLogger logger() {
        return new OutboundRESTRequestLogger();
    }

    @Bean
    public RestTemplate restTemplate(RestTemplateBuilder restTemplateBuilder, OutboundRESTRequestLogger logger) {
        return restTemplateBuilder.interceptors(logger).build();
    }
}
