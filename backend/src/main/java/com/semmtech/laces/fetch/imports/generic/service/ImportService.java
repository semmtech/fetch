package com.semmtech.laces.fetch.imports.generic.service;

import com.google.common.collect.Maps;
import com.semmtech.laces.fetch.configuration.entities.AddOnEntity;
import com.semmtech.laces.fetch.configuration.entities.EnvironmentType;
import com.semmtech.laces.fetch.configuration.entities.ImportStepEntity;
import com.semmtech.laces.fetch.configuration.service.RelaticsEnvironmentService;
import com.semmtech.laces.fetch.imports.generic.model.GenericImportResponse;
import com.semmtech.laces.fetch.sparql.ParameterNodeFactory;
import com.semmtech.laces.fetch.sparql.SparqlClient;
import com.semmtech.laces.fetch.sparql.SparqlQueryUtils;
import com.semmtech.laces.fetch.visualization.model.CommonParameter;
import com.semmtech.laces.fetch.visualization.model.QueryExecutionRequest;
import com.semmtech.laces.fetch.visualization.model.QueryResult;
import javax.naming.NoPermissionException;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.jena.query.ParameterizedSparqlString;
import org.apache.jena.rdf.model.RDFNode;
import org.apache.jena.rdf.model.ResourceFactory;
import org.jooq.lambda.tuple.Tuple2;
import org.springframework.stereotype.Service;
import org.springframework.ws.client.WebServiceIOException;

import java.net.SocketTimeoutException;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.Stream;

@Service
public class ImportService {
    private final Log logger = LogFactory.getLog(this.getClass());

    private SparqlClient sparqlClient;
    private ParameterNodeFactory parameterNodeFactory;
    private Map<EnvironmentType, TargetClient> targetClients = new HashMap<>();
    private TreeNodeSelectedValuesSorter treeNodeSelectedValuesSorter;
    private QueryExecutionRequestParentsPreprocessor treeNodeParentsCleaner;
    private SparqlQueryUtils sparqlQueryUtils;
    private RelaticsEnvironmentService environmentService;

    private static final Pattern UUID_PARAMETER_BINDING_NAME_PATTERN = Pattern.compile("BIND\\s*\\(\\?uuid\\s*as\\s*\\?([\\w\\-]*)\\)", Pattern.CASE_INSENSITIVE);
    private static final Pattern NEW_UUID_BINDING_NAME_PATTERN = Pattern.compile("BIND\\s*\\(UUID\\(\\)\\s*as\\s*\\?([\\w\\-]*)\\)", Pattern.CASE_INSENSITIVE);
    private static final Pattern QUERIED_IDENTIFIER_BINDING_NAME_PATTERN = Pattern.compile("BIND\\s*\\(\\?[\\w]+(?:uri|uuid)\\s+as\\s+\\?([\\w\\-]*)\\)", Pattern.CASE_INSENSITIVE);
    /**
     * This pattern matches the following construct in Sparql queries:
     * <p>
     * values (?imported_variable1 ?imported_variable2) {
     * ?some_placeholder
     * }
     * <p>
     * It returns 2 named groups: the values variables as "variables", so ?imported_variable1 ?imported_variable2
     * and the "placeholder" group to identify the variable
     */
    private static final Pattern IMPORTED_LINKED_VALUES_PATTERN = Pattern.compile("VALUES\\s*\\((?<variables>(?:\\?imported_\\w+\\s*)+)\\)\\s*\\{\\s*\\?(?<placeholder>\\w+)\\s*\\}", Pattern.CASE_INSENSITIVE);

    public ImportService(SparqlClient sparqlClient, ParameterNodeFactory parameterNodeFactory,
                         TreeNodeSelectedValuesSorter treeNodeSelectedValuesSorter,
                         QueryExecutionRequestParentsPreprocessor treeNodeParentsCleaner,
                         SparqlQueryUtils sparqlQueryUtils,
                         RelaticsEnvironmentService environmentService) {
        this.sparqlClient = sparqlClient;
        this.parameterNodeFactory = parameterNodeFactory;
        this.treeNodeSelectedValuesSorter = treeNodeSelectedValuesSorter;
        this.treeNodeParentsCleaner = treeNodeParentsCleaner;
        this.sparqlQueryUtils = sparqlQueryUtils;
        this.environmentService = environmentService;
    }

    public void prepareSelectionForImport(QueryExecutionRequest request) {
        // The selected values are sorted by their treeNodeId to make sure they are imported in top-down order.
        // If the order is off, the target system is not able to create the correct hierarchy.
        treeNodeSelectedValuesSorter.sort(request);


        // Remove all parent info from selected items for which the parent has not been selected for import too.
        // Done in a separate step outside the sendData method to avoid having to repeat it for every import step.
        treeNodeParentsCleaner.removeParentsFromOrphans(request);
    }

    /**
     * Execute one import step for all selected items.
     *
     * @param request     a list of items represented as generic maps, as key value pairs. The generic nature of the application doesn't allow to define a proper object model for these items.
     * @param step        the import step to execute, it contains info about the target system and how to import into it.
     * @param addOnEntity the full configuration for the add-on for access to sparql and relatics endpoints
     * @return a generic response with step name, success message, warnings and errors if present.
     */
    public GenericImportResponse sendData(QueryExecutionRequest request, ImportStepEntity step, AddOnEntity addOnEntity, Map<String, List<Map<String, String>>> linkedUuidsAndUrisByUuid) {

        var rawSparqlQuery = step.getSparqlQuery().getQuery().getQuery();
        QueryResult combinedResult = prepareSparqlQueries(request, rawSparqlQuery, linkedUuidsAndUrisByUuid)
                .filter(Optional::isPresent)
                .map(Optional::get)
                .map(query -> sparqlClient.executeQuery(query, step.getSparqlQuery().getDefaultGraphs(), addOnEntity.getSparqlEndpoint()))
                .filter(Optional::isPresent)
                .map(Optional::get)
                .map(QueryResult::fromSparqlResponseForImport)
                .reduce(getEmptyQueryResult(), QueryResult::merge);

        propagateIdentifiers(linkedUuidsAndUrisByUuid, rawSparqlQuery, combinedResult);
        if (combinedResult.hasValues()) {
            try {
                return targetClients
                        .get(EnvironmentType.valueOf(addOnEntity.getTargetType()))
                        .doImport(step, addOnEntity, combinedResult, request.getHeaderParameters());
            } catch (WebServiceIOException wioe) {
                logger.error("WebServiceIOException was thrown (cause: " + wioe.getCause() + "): " + wioe.getMessage());
                if (wioe.getCause() instanceof SocketTimeoutException) {
                    return webserviceErrorResponse(step, "Target did not respond in time! Either decrease import size or contact target's administrator.");
                }
                return webserviceErrorResponse(step, "Unexpected error occurred while contacting target: " + wioe.getMessage());
            } catch (NoPermissionException npe) {
                logger.error("NoPermissionException was thrown (cause: " + npe.getCause() + "): " + npe.getMessage());
                return webserviceErrorResponse(step, "Insufficient permissions to execute the webservice. Please check the entry code");
            } catch (Exception ex) {
                logger.error("An exception occurred. (cause: " + ex.getCause() + "): " + ex.getMessage());
                return webserviceErrorResponse(step, "Unexpected error occurred. Please contact your administrator");
            }
        } else {
            return emptyStepResponse(step);
        }
    }

    private GenericImportResponse emptyStepResponse(ImportStepEntity step) {
        return GenericImportResponse.builder()
                .success(true)
                .importStep(step.getName())
                .successMessage("No instances to create.")
                .errors(List.of())
                .warnings(List.of())
                .build();
    }

    private GenericImportResponse webserviceErrorResponse(ImportStepEntity step, String message) {
        return GenericImportResponse.builder()
                .success(false)
                .importStep(step.getName())
                .errors(Collections.singletonList(message))
                .warnings(List.of())
                .build();
    }

    /**
     * Make sure all variables that can be used as identifiers are made available to steps further down the line.
     * This includes all variables with a name containing uri or uuid, or all variables to which a new uuid or a
     * matched uri is bound.
     *
     * @param linkedUuidsAndUrisByUuid contains all variables with identifiers from previous steps.
     * @param rawSparqlQuery           the original sparql query from the configuration, before parameter substitution.
     * @param combinedResult           the result of the sparql query configured for this step after execution.
     */
    private void propagateIdentifiers(Map<String, List<Map<String, String>>> linkedUuidsAndUrisByUuid, String rawSparqlQuery, QueryResult combinedResult) {
        String uuidBindingName = extractUuidBindingName(rawSparqlQuery);
        Set<String> urisAndUuids = extractReturnedUrisAndUuids(rawSparqlQuery, uuidBindingName);
        if (CollectionUtils.isNotEmpty(urisAndUuids)) {
            combinedResult.getValues()
                    .stream()
                    .map(value -> new Tuple2<>(value.get(uuidBindingName), filterUuidAndUriFields(value, urisAndUuids)))
                    .forEach(tuple -> updateLinkedUuidsAndUrisByUuid(linkedUuidsAndUrisByUuid, tuple.v1, tuple.v2));
        }
    }

    public List<Map<String, String>> readData(AddOnEntity addOnEntity) {
        return targetClients
                .get(EnvironmentType.valueOf(addOnEntity.getTargetType()))
                .getAdditionalInputData(addOnEntity);
    }

    private QueryResult getEmptyQueryResult() {
        return QueryResult.builder()
                .values(new ArrayList<>())
                .build();
    }

    private Stream<Optional<String>> prepareSparqlQueries(QueryExecutionRequest request, String queryString, Map<String, List<Map<String, String>>> propagatedVariables) {
        return request.getValues()
                .stream()
                .map(record -> new Tuple2<Map<String, String>, String>(record, applyRecordToQuery(record, queryString, "")))
                .map(tuple -> new Tuple2<Map<String, String>, String>(tuple.v1, applyRecordToQuery(request.getSparqlParameters(), tuple.v2, "parameter_")))
                .map(tuple -> applyRecordToQuery(
                        propagatedVariables.getOrDefault(tuple.v1.get("uuid"), new ArrayList<Map<String, String>>()), tuple.v2));
    }

    private String applyRecordToQuery(Map<String, String> record, String queryString, String prefix) {
        ParameterizedSparqlString query = new ParameterizedSparqlString(queryString);
        record.forEach((key, value) -> query.setParam(prefix + key, parameterNodeFactory.createNode(key, value, null)));
        return query.toString();
    }

    private String applyRecordToQuery(Stream<CommonParameter> commonParameters, String queryString, String prefix) {
        ParameterizedSparqlString query = new ParameterizedSparqlString(queryString);
        commonParameters.forEach(parameter -> query.setParam(prefix + parameter.getId(), parameterNodeFactory.createNode(parameter.getId(), parameter.getValue(), parameter.getType())));
        return query.toString();
    }

    private Optional<String> applyRecordToQuery(List<Map<String, String>> propagatedVariables, String queryString) {
        ParameterizedSparqlString query = new ParameterizedSparqlString(queryString);

        Matcher matcher = IMPORTED_LINKED_VALUES_PATTERN.matcher(queryString);
        if (matcher.find()) {

            List<String> variables = Arrays.asList(matcher.group("variables").split(" ")).stream().map(variable -> variable.replace("?imported_", "")).collect(Collectors.toList());
            String placeholder = matcher.group("placeholder");

            Collection<List<? extends RDFNode>> filteredAndProjected = filterAndProjectToRequiredKeys(propagatedVariables, variables);
            if (CollectionUtils.isEmpty(filteredAndProjected)) {
                return Optional.empty();
            }
            query.setRowValues(placeholder, filteredAndProjected);
        }

        return Optional.ofNullable(query.toString());
    }

    private Collection<List<? extends RDFNode>> filterAndProjectToRequiredKeys(List<Map<String, String>> allMaps, List<String> variables) {
        return allMaps.stream()
                .filter(map -> map.keySet().containsAll(variables))
                .map(map -> Maps.filterKeys(map, variables::contains))
                .distinct()
                .map(node -> transform(node, variables))
                .collect(Collectors.toList());
    }

    private List<? extends RDFNode> transform(Map<String, String> map, List<String> variables) {
        return map.entrySet().stream()
                .filter(e -> variables.contains(e.getKey()))
                .sorted((first, last) -> Integer.compare(variables.indexOf(first), variables.indexOf(last)))
                .map(Map.Entry::getValue)
                .map(ResourceFactory::createProperty)
                .collect(Collectors.toList());
    }

    public void registerTargetClient(TargetClient targetClient) {
        targetClient
                .supportedTypes()
                .forEach(type -> targetClients.put(type, targetClient));
    }

    private String extractUuidBindingName(String query) {
        Matcher matcher = UUID_PARAMETER_BINDING_NAME_PATTERN.matcher(query);
        if (matcher.find()) {
            return matcher.group(1);
        }
        return "";
    }

    /**
     * Extract all variable names from returned values that either
     * - have a name with any form of uri or uuid in it
     * - are the right side of a binding from any variable with uri or uuid in it
     * - are the right side of a binding for a newly created UUID - using UUID()
     *
     * @param query
     * @param uuidBindingName
     * @return
     */
    private Set<String> extractReturnedUrisAndUuids(String query, String uuidBindingName) {
        Matcher matcher = IMPORTED_LINKED_VALUES_PATTERN.matcher(query);
        if (matcher.find()) {
            List<String> variables = Arrays.asList(matcher.group("variables").split(" ")).stream().map(variable -> variable.replace("?imported_", "")).collect(Collectors.toList());
            String placeholder = matcher.group("placeholder");
            String emptyValue = IntStream.rangeClosed(1, variables.size()).mapToObj(o -> "<urn:nothing>").collect(Collectors.joining(" "));
            query = query.replace("?" + placeholder, "(" + emptyValue + ")");
        }


        var bindings = sparqlQueryUtils.extractBindings(query);
        var variableNames = bindings
                .stream()
                .filter(variable -> StringUtils.containsAny("uri", "Uri", "URI", "uuid", "UUID", "Uuid"))
                .filter(variable -> !StringUtils.equals(variable, uuidBindingName))
                .collect(Collectors.toSet());

        updateFromPattern(query, bindings, variableNames, NEW_UUID_BINDING_NAME_PATTERN);
        updateFromPattern(query, bindings, variableNames, QUERIED_IDENTIFIER_BINDING_NAME_PATTERN);
        return variableNames;
    }

    private void updateFromPattern(String query, List<String> bindings, Set<String> variableNames, Pattern queriedUriBindingNamePattern) {
        Matcher matcher = queriedUriBindingNamePattern.matcher(query);
        while (matcher.find()) {
            String match = matcher.group(1);
            if (bindings.contains(match)) {
                variableNames.add(match);
            }
        }
    }

    /**
     * Return only the fields that are URI's or UUIDs from the previous result.
     *
     * @param origin
     * @param uuidsAndUris
     * @return
     */
    private Map<String, String> filterUuidAndUriFields(Map<String, String> origin, Set<String> uuidsAndUris) {
        return origin.entrySet()
                .stream()
                .filter(variable -> uuidsAndUris.contains(variable.getKey()))
                .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));
    }

    /**
     * Update the linked uuids and uris for one specific UUID. Replace all values for existing variables with
     * values from the current query or add new variables with their values. If the UUID hasn't been processed before,
     * put a new entry.
     *
     * @param linkedUuidsAndUrisByUuid all UUIDs and URIs from previous steps.
     * @param uuidsAndUrisFromResult   the UUIDs and URIs returned by the Sparql query of the current step.
     */
    private void updateLinkedUuidsAndUrisByUuid(
            Map<String, List<Map<String, String>>> linkedUuidsAndUrisByUuid,
            String uuid,
            Map<String, String> uuidsAndUrisFromResult) {
        List<Map<String, String>> uuidsAndUrisForCurrentUuid = new ArrayList<>();
        uuidsAndUrisForCurrentUuid.add(uuidsAndUrisFromResult);
        uuidsAndUrisForCurrentUuid = linkedUuidsAndUrisByUuid.putIfAbsent(uuid, uuidsAndUrisForCurrentUuid);
        if (uuidsAndUrisForCurrentUuid != null) {
            uuidsAndUrisForCurrentUuid.add(uuidsAndUrisFromResult);
        }
    }


}
