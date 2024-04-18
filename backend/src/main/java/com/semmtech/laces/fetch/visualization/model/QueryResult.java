package com.semmtech.laces.fetch.visualization.model;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.semmtech.laces.fetch.configuration.entities.AddOnEntity;
import com.semmtech.laces.fetch.configuration.entities.ColumnEntity;
import com.semmtech.laces.fetch.sparql.SparqlBinding;
import com.semmtech.laces.fetch.sparql.SparqlResponse;
import lombok.*;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;

import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
public class QueryResult {

    public static final String TITLE_COLUMN = "title";
    public static final String SUBTITLE_COLUMN = "subtitle";

    @Builder.Default
    private List<Column> columns = new ArrayList<>();
    @Builder.Default
    private List<Map<String, String>> values = new ArrayList<>();

    /**
     * For visualization, the information about column names and visibility
     * should come straight from the configuration. There is no need to look
     * at the columns by the sparql query
     *
     * @param response      the sparql response to supply the values
     * @param configuration the configuration to supply the visible columns
     * @return the combined treenode with all required information to display the right
     * information on screen.
     */
    public static QueryResult fromSparqlResponseForVisualization(SparqlResponse response, AddOnEntity configuration) {
        return fromSparqlResponse(
                response,
                configuration,
                QueryResult::extractColumnsForVisualization
        );
    }

    /**
     * For imports, the query itself is the driver of what columns to use for transmitting
     * to the external data system. The sparql response contains a list of variables in the
     * head, from which we only need the names.
     *
     * @param response the sparql response to supply the values and the columns
     * @return the combined treenode with all the information to generate the right import XML
     * or Json.
     */
    public static QueryResult fromSparqlResponseForImport(SparqlResponse response) {
        return fromSparqlResponse(
                response,
                response.getHead().getVariables(),
                QueryResult::extractColumnsForImport
        );
    }

    public static QueryResult fromSparqlResponseForTitle(SparqlResponse response) {
        return fromSparqlResponse(
                response,
                List.of(TITLE_COLUMN, SUBTITLE_COLUMN),
                QueryResult::extractColumnsForImport
        );
    }

    /**
     * Generic implementation for transforming sparql responses into TreeNodes. Since
     * extracting column information is the only thing that's different, the way to do
     * that and the source from which to get them is passed in as a function and source.
     *
     * @param response        the sparql response to supply the values
     * @param columnSource    the source from which we can extract the column metadata
     * @param columnExtractor a function that transforms the source into a list of columns
     * @param <T>             the inferred type of the source
     * @return the TreeNode object containing metadata on columns and the returned data
     */
    public static <T> QueryResult fromSparqlResponse(
            SparqlResponse response,
            T columnSource,
            Function<T, List<Column>> columnExtractor) {
        List<Column> columns = columnExtractor.apply(columnSource);
        List<Map<String, String>> values = extractValues(response);
        return QueryResult.builder().columns(columns).values(values).build();
    }

    private static List<Map<String, String>> extractValues(SparqlResponse response) {
        return response.getResults().getBindings()
                .stream()
                .map(QueryResult::toMap)
                .collect(Collectors.toList());
    }

    private static List<Column> extractColumnsForVisualization(AddOnEntity configuration) {
        if (configuration != null && configuration.getVisualization() != null && configuration.getVisualization().getColumns() != null) {
            return configuration.getVisualization()
                    .getColumns()
                    .values()
                    .stream()
                    .map(QueryResult::buildColumn)
                    .collect(Collectors.toList());
        }
        return List.of();
    }

    private static Column buildColumn(ColumnEntity columnEntity) {
        return Column.builder()
                .name(columnEntity.getBindingName())
                .show(columnEntity.isVisible())
                .display(columnEntity.getDisplayName())
                .build();
    }

    private static List<Column> extractColumnsForImport(List<String> names) {
        return names.stream()
                .map(name -> Column.builder().name(name).build())
                .collect(Collectors.toList());
    }

    private static Map<String, String> toMap(Map<String, SparqlBinding> record) {
        return record.entrySet()
                .stream()
                .collect(Collectors.toMap(Map.Entry::getKey, entry -> entry.getValue().getValue()));
    }

    public static QueryResult merge(QueryResult first, QueryResult second) {
        List<Column> columns = first.columns;
        if (CollectionUtils.isEmpty(first.getColumns())) {
            columns = second.columns;
        } else if (!first.getColumns().equals(second.getColumns())) {
            throw new IllegalArgumentException("You can only merge treenodes with the same columns");
        }

        List<Map<String, String>> values = new ArrayList<>(first.values);
        values.addAll(second.values);

        return QueryResult.builder()
                .columns(columns)
                .values(values)
                .build();
    }

    public boolean hasValues() {
        return CollectionUtils.isNotEmpty(values);
    }

}
