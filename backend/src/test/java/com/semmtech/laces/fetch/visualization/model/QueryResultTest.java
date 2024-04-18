package com.semmtech.laces.fetch.visualization.model;

import com.semmtech.laces.fetch.configuration.entities.AddOnEntity;
import com.semmtech.laces.fetch.configuration.entities.ColumnEntity;
import com.semmtech.laces.fetch.configuration.entities.VisualizationEntity;
import com.semmtech.laces.fetch.sparql.SparqlHead;
import com.semmtech.laces.fetch.sparql.SparqlResponse;
import com.semmtech.laces.fetch.sparql.SparqlResults;
import org.hamcrest.Matcher;
import org.junit.Test;

import java.util.Arrays;
import java.util.LinkedHashMap;

import static org.hamcrest.Matchers.*;
import static org.junit.Assert.assertThat;

public class QueryResultTest {

    public static final Matcher<String> NULL_VALUE = nullValue(String.class);

    @Test
    public void givenSparqlResponseWithColumns_visibleColumnsDefined_showAndDisplayApplied() {
        SparqlResponse response = getSparqlResponse();

        LinkedHashMap<String, ColumnEntity> visibleColumns = new LinkedHashMap<>();
        visibleColumns.put("uri", ColumnEntity.builder().bindingName("uri").displayName("uri").visible(false).build());
        visibleColumns.put("label", ColumnEntity.builder().bindingName("label").displayName("Label").visible(true).build());
        visibleColumns.put("hasChildren", ColumnEntity.builder().bindingName("hasChildren").build());
        visibleColumns.put("uuid", ColumnEntity.builder().bindingName("uuid").build());
        visibleColumns.put("isImported", ColumnEntity.builder().bindingName("isImported").displayName("Present").visible(true).build());

        AddOnEntity configuration =
                AddOnEntity.builder()
                        .visualization(
                                VisualizationEntity.builder()
                                        .columns(visibleColumns)
                                        .build()
                        )
                        .build();

        QueryResult node = QueryResult.fromSparqlResponseForVisualization(response, configuration);

        assertThat(node.getColumns(), hasSize(5));
        assertThat(node.getColumns(),
                hasItems(
                        getColumnMatcher("uri", equalTo("uri"), false),
                        getColumnMatcher("label", equalTo("Label"), true),
                        getColumnMatcher("hasChildren", NULL_VALUE, false),
                        getColumnMatcher("uuid", NULL_VALUE, false),
                        getColumnMatcher("isImported", equalTo("Present"), true)
                ));
    }

    @Test
    public void givenSparqlResponseWithColumns_noVisibleColumnsDefined_showFalseAndDisplayNullApplied() {
        SparqlResponse response = getSparqlResponse();

        LinkedHashMap<String, ColumnEntity> columns = new LinkedHashMap<>();
        columns.put("uri", ColumnEntity.builder().bindingName("uri").displayName(null).visible(false).build());
        columns.put("label", ColumnEntity.builder().bindingName("label").displayName(null).visible(false).build());
        columns.put("hasChildren", ColumnEntity.builder().bindingName("hasChildren").displayName(null).visible(false).build());
        columns.put("uuid", ColumnEntity.builder().bindingName("uuid").displayName(null).visible(false).build());
        columns.put("isImported", ColumnEntity.builder().bindingName("isImported").displayName(null).visible(false).build());

        AddOnEntity configuration =
                AddOnEntity.builder()
                        .visualization(
                                VisualizationEntity.builder()
                                        .columns(columns)
                                        .build()
                        )
                        .build();

        QueryResult node = QueryResult.fromSparqlResponseForVisualization(response, configuration);

        assertThat(node.getColumns(), hasSize(5));
        assertThat(node.getColumns(),
                hasItems(
                        getColumnMatcher("uri", NULL_VALUE, false),
                        getColumnMatcher("label", NULL_VALUE, false),
                        getColumnMatcher("hasChildren", NULL_VALUE, false),
                        getColumnMatcher("uuid", NULL_VALUE, false),
                        getColumnMatcher("isImported", NULL_VALUE, false)
                ));
    }

    public Matcher<Column> getColumnMatcher(String uri, Matcher<String> displayMatcher, boolean b) {
        return allOf(
                hasProperty("name", equalTo(uri)),
                hasProperty("display", displayMatcher),
                hasProperty("show", equalTo(b))
        );
    }

    public SparqlResponse getSparqlResponse() {
        return SparqlResponse.builder()
                .head(
                        SparqlHead.builder()
                                .variables(
                                        Arrays.asList("uri", "label", "hasChildren", "uuid", "isImported")
                                )
                                .build()
                )
                .results(SparqlResults.builder().build())
                .build();
    }
}
