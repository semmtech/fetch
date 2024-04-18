package com.semmtech.laces.fetch.visualization.service;

import com.semmtech.laces.fetch.configuration.dtos.common.FilterFieldDto;
import com.semmtech.laces.fetch.configuration.entities.AddOnEntity;
import com.semmtech.laces.fetch.configuration.entities.SparqlQueryEntity;
import com.semmtech.laces.fetch.imports.generic.service.ImportService;
import com.semmtech.laces.fetch.sparql.ParameterNodeFactory;
import com.semmtech.laces.fetch.sparql.SparqlClient;
import com.semmtech.laces.fetch.sparql.SparqlResponse;
import com.semmtech.laces.fetch.sparql.SparqlTypeCache;
import com.semmtech.laces.fetch.streams.StreamUtils;
import com.semmtech.laces.fetch.visualization.model.CommonParameter;
import com.semmtech.laces.fetch.visualization.model.QueryExecutionRequest;
import com.semmtech.laces.fetch.visualization.model.RootsQueryResponse;
import com.semmtech.laces.fetch.visualization.model.QueryResult;
import com.semmtech.laces.fetch.visualization.model.VisualizationMetadata;
import lombok.Getter;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.jena.query.ParameterizedSparqlString;
import org.apache.jena.rdf.model.RDFNode;
import org.apache.jena.rdf.model.ResourceFactory;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.Function;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static com.semmtech.laces.fetch.visualization.model.QueryResult.SUBTITLE_COLUMN;
import static com.semmtech.laces.fetch.visualization.model.QueryResult.TITLE_COLUMN;

@Service
public class VisualizationService {
    private static final String ADDITIONAL_VALUES = "additional_values";
    private static final String ADDITIONAL_PREFIX = "?additional_";
    private static final String PARENT_PREFIX = "parent_";
    private static final String PARAMETER_PREFIX = "parameter_";


    private final SparqlClient sparqlClient;

    private final SparqlTypeCache sparqlTypeCache;

    private final ParameterNodeFactory parameterNodeFactory;

    private final ImportService importService;


    private SingleParameterTypePattern noPrefixPattern = new SingleParameterTypePattern("", null);
    private SingleParameterTypePattern parentsPattern = new SingleParameterTypePattern(PARENT_PREFIX, null);
    private SingleParameterTypePattern parametersPattern = new SingleParameterTypePattern(PARAMETER_PREFIX, "literal");

    /**
     * A regular expression to extract all "VALUES (?<valueKey> ) { ?additional_values }
     * The different value keys are used to be able to build the value objects to be bound in the right order.
     */
    private Pattern valuesPattern = Pattern.compile("VALUES\\s*\\(((?:\\?\\w+\\s*)+)\\)", Pattern.CASE_INSENSITIVE);

    public VisualizationService(SparqlClient sparqlClient, SparqlTypeCache sparqlTypeCache, ParameterNodeFactory parameterNodeFactory, ImportService importService) {
        this.sparqlClient = sparqlClient;
        this.sparqlTypeCache = sparqlTypeCache;
        this.parameterNodeFactory = parameterNodeFactory;
        this.importService = importService;
    }

    public RootsQueryResponse executeRootQuery(AddOnEntity configuration, QueryExecutionRequest request) {
        final var rootsQuery = configuration.getVisualization().getRootsQuery();
        RootsQueryResponse result =
                new RootsQueryResponse(
                        executeAndConvertToTreeNode(
                                rootsQuery.getQuery().getQuery(),
                                rootsQuery.getDefaultGraphs(),
                                configuration,
                                query -> applyAdditionalValues(configuration, query),
                                query -> applyParametersByPattern(query, noPrefixPattern, StreamUtils.streamCollection(request.getFilterParameters())),
                                query -> applyParametersByPattern(query, parametersPattern, request.getSparqlParameters())),
                        getVisualizationMetadata(configuration),
                        StreamUtils.transformList(configuration.getFilterFields(), FilterFieldDto::new)
                );
        return result;
    }

    public QueryResult executeChildQuery(AddOnEntity configuration, QueryExecutionRequest request) {
        final var childrenQuery = configuration.getVisualization().getChildrenQuery();
        return executeAndConvertToTreeNode(
                childrenQuery.getQuery().getQuery(),
                childrenQuery.getDefaultGraphs(),
                configuration,
                query -> applyParametersByPattern(query, parentsPattern, request.getSelectedRecord()),
                query -> applyParametersByPattern(query, parametersPattern, request.getSparqlParameters()),
                query -> applyAdditionalValues(configuration, query));
    }

    public QueryResult executeParameterLessQuery(AddOnEntity configuration, SparqlQueryEntity query) {
        return executeAndConvertToTreeNode(
                query.getQuery(),
                configuration.getVisualization().getRootsQuery().getDefaultGraphs(),
                configuration
        );
    }

    /**
     * Apply the necessary parameters, execute the query and transform the result into a TreeNode we can send back to the add-on.
     *
     * @param queryString   the configured query string to execute (either roots- or children query)
     * @param configuration the full add-on configuration
     * @param queryAdapters a vararg to which you can pass functions that add parameters or values to the query. We use
     *                      consumers to allow each variant to supply tailored functions to adapt the query.
     * @return a TreeNode that represents a number of values to be displayed in the add-on tree.
     */
    @SafeVarargs
    private QueryResult executeAndConvertToTreeNode(String queryString, List<String> defaultGraphs, AddOnEntity configuration, Function<String, String>... queryAdapters) {
        String adaptedQuery = applyAllQueryAdapters(queryString, queryAdapters);

        return sparqlClient.executeQuery(adaptedQuery, defaultGraphs, configuration.getSparqlEndpoint())
                .map(sparqlResponse -> convertSparqlResponseToTreeNode(sparqlResponse, configuration))
                .get();
    }

    /**
     * Combine all adapter functions into on chain and apply it to the input query
     *
     * @param queryString   the base query to start with
     * @param queryAdapters an array of adapters to apply to the base query
     * @return a query with all adapters applied.
     */
    private String applyAllQueryAdapters(String queryString, Function<String, String>[] queryAdapters) {
        return Arrays.stream(queryAdapters)
                .reduce(Function.identity(), Function::andThen)
                .apply(queryString);
    }

    /**
     * Apply all values from the input to the query, for which the query has a binding ?parent_<variablename>.
     *
     * @param queryString the children query configured in the add-on configuration, required for extracting the variables.
     * @param pattern     a regex to find a prefixed part of the query string
     * @param inputRecord the record to get the values for the parameters from
     */
    private String applyParametersByPattern(String queryString, SingleParameterTypePattern pattern, Map<String, String> inputRecord) {
        ParameterizedSparqlString query = new ParameterizedSparqlString(queryString);
        Map<String, String> bindingPairs = extractBindingPairs(queryString, pattern);
        bindingPairs.forEach((key, value) -> setSparqlParameter(query, key, value, pattern, inputRecord));
        return query.toString();
    }

    private String applyParametersByPattern(String queryString, SingleParameterTypePattern pattern, Stream<CommonParameter> commonParameters) {
        ParameterizedSparqlString query = new ParameterizedSparqlString(queryString);
        Map<String, String> bindingPairs = extractBindingPairs(queryString, pattern);
        commonParameters.forEach(parameter -> setSparqlParameter(query, parameter, pattern, bindingPairs));
        return query.toString();
    }

    private Map<String, String> extractBindingPairs(String query, SingleParameterTypePattern pattern) {
        Map<String, String> bindingPairs = new HashMap<>();
        Matcher matcher = pattern.getPattern().matcher(query);
        while (matcher.find()) {
            bindingPairs.put(matcher.group(1), matcher.group(2));
        }
        return bindingPairs;
    }

    private void setSparqlParameter(ParameterizedSparqlString query, String parentParameterName, String boundParentParameterName, SingleParameterTypePattern pattern, Map<String, String> record) {
        if (!record.isEmpty()) {
            String parentParameterSuffix = parentParameterName.substring(pattern.getPrefix().length());
            String valueToSet = record.get(parentParameterSuffix);
            query.setParam(parentParameterName, parameterNodeFactory.createNode(boundParentParameterName, valueToSet, pattern.getType()));
        }
    }

    private void setSparqlParameter(ParameterizedSparqlString query, CommonParameter parameter, SingleParameterTypePattern pattern, Map<String, String> bindingPairs) {
        String parameterBinding = pattern.getPrefix() + parameter.getId();
        if (!bindingPairs.isEmpty() && bindingPairs.containsKey(parameterBinding)) {
            query.setParam(parameterBinding, parameterNodeFactory.createNode(bindingPairs.get(parameterBinding), parameter.getValue(), parameter.getType()));
        }
    }

    /**
     * Apply extra information coming from the target system.
     * We first extract the values keys, so from
     * <p>
     * VALUES (?additional_foreignKey ?additional_uuid) {
     * (<http://dds.semmtech.nl/asset/Huis> <urn:uuid:1>)
     * (<http://dds.semmtech.nl/asset/Kasteel> <urn:uuid:2>)
     * }
     * <p>
     * we extract additional_foreignKey and additional_uuid.
     * <p>
     * Next we execute the query for the additional values from the target system, which is returned as a map
     * whose keys would match suffixes of the values keys extracted above. We use these keys to extract the right
     * values from this map to construct objects with the right properties to add to the values list.
     *
     * @param configuration the add-on configuration that holds all information about the target system
     * @param queryString   the query configured in the add-on configuration, required for extracting the variables (in prepareSparqlValues).
     */
    private String applyAdditionalValues(AddOnEntity configuration, String queryString) {
        ParameterizedSparqlString query = new ParameterizedSparqlString(queryString);
        List<String> valuesKeys = extractValuesKeys(queryString);

        if (CollectionUtils.isNotEmpty(valuesKeys)) {
            List<Map<String, String>> additionalValues = null;
            if (configuration.getVisualization().getAdditionalInputsConfiguration() != null) {
                additionalValues = importService.readData(configuration);
            }
            if (CollectionUtils.isEmpty(additionalValues)) {
                query.setValues(ADDITIONAL_VALUES, Collections.singletonList(ResourceFactory.createResource("urn:nothing")));
            } else {
                query.setRowValues(ADDITIONAL_VALUES,
                        additionalValues.stream()
                                .map(additionalInput -> prepareSparqlValues(additionalInput, valuesKeys))
                                .collect(Collectors.toList()));
            }
        }

        return query.toString();
    }

    private List<String> extractValuesKeys(String query) {
        Matcher matcher = valuesPattern.matcher(query);
        if (matcher.find()) {
            String valuesKeysJoined = matcher.group(1);

            return Arrays.stream(valuesKeysJoined.split(" "))
                    .map(key -> key.replace(ADDITIONAL_PREFIX, ""))
                    .collect(Collectors.toList());
        }
        return Collections.emptyList();
    }

    private List<? extends RDFNode> prepareSparqlValues(Map<String, String> additionalInput, List<String> valuesKeys) {
        return valuesKeys.stream()
                .map(key -> ResourceFactory.createProperty(additionalInput.get(key.toLowerCase())))
                .collect(Collectors.toList());
    }

    /**
     * A regular expression to extract all bindings that match "BIND (?parent_<variable> as ?<boundTo>)
     * 2 matching groups are returned: one for <variable> and one for <boundTo>. The <variable> part is used to substitute
     * with an input variable, the <boundTo> part is used to determine the type of node to be created.
     */
    private VisualizationMetadata getVisualizationMetadata(AddOnEntity configuration) {

        var title = configuration.getDisplayName();
        var subtitle = StringUtils.EMPTY;

        if (configuration.getVisualization().getTitleQuery() != null) {
            var titleQueryResponse = executeTitleQuery(configuration);

            if (titleQueryResponse.isPresent() && titleQueryResponse.get().hasValues()) {
                title = titleQueryResponse.get().getValues().get(0).get(TITLE_COLUMN);
                subtitle = titleQueryResponse.get().getValues().get(0).get(SUBTITLE_COLUMN);
            }
        }

        return VisualizationMetadata.builder()
                .enablePagination(configuration.getVisualization().isEnablePagination())
                .title(title)
                .subtitle(subtitle)
                .build();
    }


    private Optional<QueryResult> executeTitleQuery(AddOnEntity configuration) {
        final var titleQuery = configuration.getVisualization().getTitleQuery();

        var sparqlResponse = sparqlClient.executeQuery(titleQuery.getQuery(), Collections.emptyList(), configuration.getSparqlEndpoint());
        if (sparqlResponse.isEmpty()) {
            return Optional.empty();
        }
        return Optional.of(QueryResult.fromSparqlResponseForTitle(sparqlResponse.get()));
    }

    /**
     * Transform the sparql response into a TreeNode representation that can be interpreted by the add-on client
     *
     * @param sparqlResponse the result of executing the sparql query on the source data system
     * @param configuration  the full add-on configuration that holds all information about the source and target systems
     * @return a TreeNode representation of the query result
     */
    private QueryResult convertSparqlResponseToTreeNode(SparqlResponse sparqlResponse, AddOnEntity configuration) {
        sparqlTypeCache.cache(sparqlResponse.getResults());
        return QueryResult.fromSparqlResponseForVisualization(sparqlResponse, configuration);
    }

    @Getter
    private static class SingleParameterTypePattern {

        private final Pattern pattern;
        private final String prefix;
        private final String type;

        SingleParameterTypePattern(String prefix, String type) {
            this.pattern = Pattern.compile("BIND\\s*\\(\\s*\\?(" + prefix + "[\\w\\-]*)\\s*as\\s*\\?([\\w\\-]*)\\s*\\)", Pattern.CASE_INSENSITIVE);
            this.prefix = prefix;
            this.type = type;
        }

    }
}
