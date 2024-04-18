package com.semmtech.laces.fetch.configuration.entities;

import org.junit.Test;
import org.springframework.http.HttpHeaders;

import static org.hamcrest.Matchers.hasItem;
import static org.junit.Assert.assertThat;

public class BasicAuthenticationTest {

    @Test
    public void testBasicAuthenticationHeaderGenererated() {
        BasicAuthenticationEntity basicAuthentication = new BasicAuthenticationEntity();
        basicAuthentication.setPassword("password");
        basicAuthentication.setUserName("user");

        HttpHeaders headers = basicAuthentication.headers("configuration");

        assertThat(headers.get(HttpHeaders.AUTHORIZATION), hasItem("Basic dXNlcjpwYXNzd29yZA=="));
    }
}
