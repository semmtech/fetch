package com.semmtech.laces.fetch.configuration.entities;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.Test;
import org.springframework.test.util.ReflectionTestUtils;

import java.io.IOException;

import static org.hamcrest.Matchers.*;
import static org.junit.Assert.assertThat;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;

public class AuthenticationMethodMarshallingTest {

    @Test
    public void whenTypeIsLACES_TOKEN_correctAuthenticationGenerated() throws IOException {
        var json =
                "{\n" +
                "            \"type\": \"LACES_TOKEN\",\n" +
                "            \"applicationId\": \"laces-fetch-addon\",\n" +
                "            \"privateKey\": \"randomPrivateKey\"\n" +
                "}";

        ObjectMapper mapper = new ObjectMapper();
        AuthenticationMethodEntity tokenAuthentication = mapper.readValue(json, AuthenticationMethodEntity.class);

        assertThat(tokenAuthentication, instanceOf(SemmtechTokenAuthenticationEntity.class));
        assertThat(ReflectionTestUtils.getField(tokenAuthentication, "applicationId"), equalTo("laces-fetch-addon"));
        assertThat(ReflectionTestUtils.getField(tokenAuthentication, "privateKey"), equalTo("randomPrivateKey"));
    }

    @Test
    public void whenTypeIsBASIC_correctAuthenticationGenerated() throws IOException {
        var json =
                "{\n" +
                "            \"type\": \"BASIC\",\n" +
                "            \"userName\": \"user name\",\n" +
                "            \"password\": \"pass word\"\n" +
                "}";

        ObjectMapper mapper = new ObjectMapper();
        AuthenticationMethodEntity tokenAuthentication = mapper.readValue(json, AuthenticationMethodEntity.class);

        assertThat(tokenAuthentication, instanceOf(BasicAuthenticationEntity.class));
        assertThat(ReflectionTestUtils.getField(tokenAuthentication, "userName"), equalTo("user name"));
        assertThat(ReflectionTestUtils.getField(tokenAuthentication, "password"), equalTo("pass word"));
    }

    @Test
    public void givenSemmtechTokenAuthentication_whenMarshalled_allPropertiesArePresent() throws JsonProcessingException {
        var semmtechTokenAuthentication = new SemmtechTokenAuthenticationEntity("privateKey", "applicationId");
        ObjectMapper mapper = new ObjectMapper();
        String json = mapper.writeValueAsString(semmtechTokenAuthentication);

        assertThat(json, containsString("\"privateKey\":\"privateKey\""));

    }

}
