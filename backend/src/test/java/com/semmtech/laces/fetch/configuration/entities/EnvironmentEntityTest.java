package com.semmtech.laces.fetch.configuration.entities;

import org.junit.Test;

import static org.hamcrest.Matchers.*;
import static org.junit.Assert.assertThat;

public class EnvironmentEntityTest {
    @Test
    public void whenGetNameCalled_nameFilledIn_nameReturned() {
        EnvironmentEntity environment =
                EnvironmentEntity.builder()
                        .name("A name")
                        .serviceUrl("An URL")
                        .build();

        assertThat(environment, hasProperty("name", equalTo("A name")));
    }

    @Test
    public void whenGetNameCalled_nameNotFilledInAndServiceUrlPresent_serviceUrlReturned() {
        EnvironmentEntity environment =
                EnvironmentEntity.builder()
                        .name(null)
                        .serviceUrl("An URL")
                        .build();

        assertThat(environment, hasProperty("name", equalTo("An URL")));
    }

    @Test
    public void whenGetNameCalled_nameNotFilledInAndServiceUrlNotPresent_emptyNameReturned() {
        EnvironmentEntity environment =
                EnvironmentEntity.builder()
                        .name(null)
                        .serviceUrl(null)
                        .build();

        assertThat(environment, hasProperty("name", nullValue()));
    }
}
