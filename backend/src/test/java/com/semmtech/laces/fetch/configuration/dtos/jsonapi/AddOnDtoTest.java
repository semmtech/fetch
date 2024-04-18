package com.semmtech.laces.fetch.configuration.dtos.jsonapi;

import com.semmtech.laces.fetch.configuration.dtos.common.*;
import com.semmtech.laces.fetch.configuration.entities.AddOnEntity;
import com.semmtech.laces.fetch.configuration.entities.PublicAuthenticationEntity;
import org.junit.Test;

import java.util.*;

import static org.hamcrest.Matchers.*;
import static org.junit.Assert.assertThat;

public class AddOnDtoTest {

    @Test
    public void whenConvertingToEntity_allFieldsArePresent_allFieldsAreMapped() {
        final var columns = new LinkedHashMap<String, ColumnDto>();
        columns.put("column1", ColumnDto.builder().bindingName("col1").displayName("Column1").visible(true).build());
        columns.put("column2", ColumnDto.builder().bindingName("col2").displayName("Column2").visible(true).build());

        AddOnDto dto =
                AddOnDto.addOnDtoBuilder()
                        .description("Description")
                        .name("Name")
                        .jsonApi(
                                JsonApiDto.builder()
                                        .id("jsonApiDtoId")
                                        .name("ApiName")
                                        .serviceUrl("ServiceUrl")
                                        .build()
                        )
                        .id("AddOnId")
                        .importStep(
                                ImportStepDto.<JsonApiEndpointDto>builder()
                                        .importTarget(
                                                JsonApiEndpointDto.builder()
                                                        .name("EndpointName")
                                                        .id("id")
                                                        .path("path")
                                                        .type("Type")
                                                        .build()
                                        )
                                        .build()
                        )
                        .visualization(
                                VisualizationDto.visualizationDtoBuilder()
                                        .additionalInputs(
                                                JsonApiEndpointDto.builder()
                                                        .name("VisEndpointName")
                                                        .id("VisId")
                                                        .path("VisPath")
                                                        .type("VisType")
                                                        .build()
                                        )
                                        .childrenQuery(
                                                SparqlQueryWithDefaultGraphsDto.builder()
                                                        .defaultGraphs(List.of("childrengraph1", "childrengraph2"))
                                                        .query(
                                                                SparqlQueryDto.builder()
                                                                        .query("ChildrenQuery")
                                                                        .description("ChildrenQueryDescription")
                                                                        .id("ChildrenQueryId")
                                                                        .name("ChildrenQueryName")
                                                                        .build()
                                                        )
                                                        .build()
                                        )
                                        .rootsQuery(
                                                SparqlQueryWithDefaultGraphsDto.builder()
                                                        .defaultGraphs(List.of("rootsgraph1", "rootsgraph2"))
                                                        .query(
                                                                SparqlQueryDto.builder()
                                                                        .query("RootsQuery")
                                                                        .description("RootsQueryDescription")
                                                                        .id("RootsQueryId")
                                                                        .name("RootsQueryName")
                                                                        .build()
                                                        )
                                                        .build()
                                        )
                                        .enablePagination(true)
                                        .visibleColumns(columns)
                                        .build()
                        )
                        .active(true)
                        .displayName("DisplayName")
                        .endDate(Date.from(new GregorianCalendar(2020, Calendar.JANUARY,6).toInstant()))
                        .startDate(Date.from(new GregorianCalendar(2020,Calendar.JANUARY,7).toInstant()))
                        .sparqlEndpoint(
                                SparqlEndpointDto.builder()
                                        .authenticationMethod(new PublicAuthenticationDto())
                                        .id("SparqlEndpointid")
                                        .name("SparqlEndpointName")
                                        .url("SparqlEndpointUrl")
                                        .build()
                        )
                        .build();

        AddOnEntity entity = dto.toEntity();

        assertThat(
                entity,
                allOf(
                        hasProperty("id", equalTo("AddOnId")),
                        hasProperty("active", equalTo(true)),
                        hasProperty("description", equalTo("Description")),
                        hasProperty("displayName", equalTo("DisplayName")),
                        hasProperty("endDate", equalTo(Date.from(new GregorianCalendar(2020,Calendar.JANUARY,6).toInstant()))),
                        hasProperty("name", equalTo("Name")),
                        hasProperty("startDate", equalTo(Date.from(new GregorianCalendar(2020,Calendar.JANUARY,7).toInstant()))),
                        hasProperty("dataTarget", equalTo("jsonApiDtoId")),
                        hasProperty("visualization", allOf(
                                hasProperty("enablePagination", equalTo(true)),
                                hasProperty("additionalInputsConfiguration", equalTo("VisId")),
                                hasProperty("rootsQuery", allOf(
                                        hasProperty("query", allOf(
                                                hasProperty("query", equalTo("RootsQuery")),
                                                hasProperty("description", equalTo("RootsQueryDescription")),
                                                hasProperty("id", equalTo("RootsQueryId")),
                                                hasProperty("name", equalTo("RootsQueryName"))
                                        )),
                                        hasProperty("defaultGraphs", hasItems("rootsgraph1", "rootsgraph2"))
                                )),
                                hasProperty("childrenQuery", allOf(
                                        hasProperty("query", allOf(
                                                hasProperty("query", equalTo("ChildrenQuery")),
                                                hasProperty("description", equalTo("ChildrenQueryDescription")),
                                                hasProperty("id", equalTo("ChildrenQueryId")),
                                                hasProperty("name", equalTo("ChildrenQueryName"))
                                        )),
                                        hasProperty("defaultGraphs", hasItems("childrengraph1", "childrengraph2"))
                                )),
                                hasProperty("columns", allOf(
                                        hasEntry(
                                                equalTo("column1"),
                                                allOf(
                                                        hasProperty("bindingName", equalTo("col1")),
                                                        hasProperty("displayName", equalTo("Column1")),
                                                        hasProperty("visible", equalTo(true))
                                                )
                                        ),
                                        hasEntry(
                                                equalTo("column2"),
                                                allOf(
                                                        hasProperty("bindingName", equalTo("col2")),
                                                        hasProperty("displayName", equalTo("Column2")),
                                                        hasProperty("visible", equalTo(true))
                                                )
                                        )
                                ))
                        )),
                        hasProperty("sparqlEndpoint", allOf(
                                hasProperty("authenticationMethod", instanceOf(PublicAuthenticationEntity.class)),
                                hasProperty("id", equalTo("SparqlEndpointid")),
                                hasProperty("name", equalTo("SparqlEndpointName")),
                                hasProperty("url", equalTo("SparqlEndpointUrl"))
                        )),
                        hasProperty("importConfiguration",
                                hasProperty("steps", hasItem(
                                      hasProperty("importTarget", equalTo("id"))
                                )))
                )
        );
    }

    /**
     * The goal of this test is to make sure that no errors
     * are thrown when parts of the configuration is left blank.
     */
    @Test
    public void whenConverting_noPropertiesFilledIn_NoErrorThrown() {
        AddOnDto dto = AddOnDto.addOnDtoBuilder().build();
        dto.toEntity();
    }
}
