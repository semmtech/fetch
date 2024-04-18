package com.semmtech.laces.fetch.sparql;

import com.google.common.collect.Maps;
import org.apache.jena.query.ParameterizedSparqlString;
import org.apache.jena.rdf.model.RDFNode;
import org.apache.jena.rdf.model.ResourceFactory;
import org.junit.Test;

import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import static org.hamcrest.collection.IsCollectionWithSize.hasSize;
import static org.hamcrest.core.IsCollectionContaining.hasItems;
import static org.hamcrest.collection.IsIterableContainingInOrder.contains;
import static org.junit.Assert.assertThat;

public class SparqlQueryUtilsTest {

    @Test
    public void whenQuerySupplied_variablesExtracted() {
        String sparqlQuery = "PREFIX xsd: <http://www.w3.org/2001/XMLSchema#>\n" +
                "PREFIX rdfs: <http://www.w3.org/2000/01/rdf-schema#>\n" +
                "PREFIX skos: <http://www.w3.org/2004/02/skos/core#>\n" +
                "PREFIX owl: <http://www.w3.org/2002/07/owl#>\n" +
                "PREFIX rdf: <http://www.w3.org/1999/02/22-rdf-syntax-ns#>\n" +
                "PREFIX asset: <http://dds.semmtech.nl/asset/>\n" +
                "    \n" +
                "SELECT ?uri ?label ?hasChildren ?uuid ?isImported {\n" +
                "    BIND(<http://dds.semmtech.nl/asset/Gebouw> as ?parentUri) .\n" +
                "    BIND(?parent_uuid as ?parentUuid) .\n" +
                "    ?uri rdf:type owl:Class ;\n" +
                "        rdfs:subClassOf ?parentUri .\n" +
                "   \n" +
                "    OPTIONAL {\n" +
                "        ?uri skos:prefLabel ?label\n" +
                "    }\n" +
                "     \n" +
                "    BIND(EXISTS { ?child rdfs:subClassOf ?uri } as ?hasChildren) .\n" +
                " \n" +
                "    OPTIONAL {\n" +
                "      # VALUES is populated by the result of the XML webservice:\n" +
                "      VALUES (?additional_foreignKey) {\n" +
                "(<http://blabla.com>)}\n" +
                "      FILTER (?uri = ?additional_foreignKey) .\n" +
                "      BIND(true as ?inner_imported) .\n" +
                "    }\n" +
                "    BIND(COALESCE(?inner_imported, false) as ?isImported) . \n" +
                "    BIND(UUID() as ?uuid) .\n" +
                "}";
        SparqlQueryUtils sparqlQueryUtils = new SparqlQueryUtils();
        List<String> variables = sparqlQueryUtils.extractBindings(sparqlQuery);
        assertThat(variables, hasSize(5));
        assertThat(variables, hasItems("uri", "label", "hasChildren", "uuid", "isImported"));
    }

    @Test
    public void whenQueryWithPlaceholderSupplied_variablesExtracted() {
        String sparqlQuery = "PREFIX xsd: <http://www.w3.org/2001/XMLSchema#>\n" +
                "PREFIX rdfs: <http://www.w3.org/2000/01/rdf-schema#>\n" +
                "PREFIX skos: <http://www.w3.org/2004/02/skos/core#>\n" +
                "PREFIX owl: <http://www.w3.org/2002/07/owl#>\n" +
                "PREFIX rdf: <http://www.w3.org/1999/02/22-rdf-syntax-ns#>\n" +
                "PREFIX asset: <http://dds.semmtech.nl/asset/>\n" +
                "    \n" +
                "SELECT ?uri ?label ?hasChildren ?uuid ?isImported {\n" +
                "    BIND(<http://dds.semmtech.nl/asset/Gebouw> as ?parentUri) .\n" +
                "    BIND(?parent_uuid as ?parentUuid) .\n" +
                "    ?uri rdf:type owl:Class ;\n" +
                "        rdfs:subClassOf ?parentUri .\n" +
                "   \n" +
                "    OPTIONAL {\n" +
                "        ?uri skos:prefLabel ?label\n" +
                "    }\n" +
                "     \n" +
                "    BIND(EXISTS { ?child rdfs:subClassOf ?uri } as ?hasChildren) .\n" +
                " \n" +
                "    OPTIONAL {\n" +
                "      # VALUES is populated by the result of the XML webservice:\n" +
                "      VALUES (?additional_foreignKey) {\n" +
                " ?placeholder }\n" +
                "      FILTER (?uri = ?additional_foreignKey) .\n" +
                "      BIND(true as ?inner_imported) .\n" +
                "    }\n" +
                "    BIND(COALESCE(?inner_imported, false) as ?isImported) . \n" +
                "    BIND(UUID() as ?uuid) .\n" +
                "}";
        SparqlQueryUtils sparqlQueryUtils = new SparqlQueryUtils();
        List<String> variables = sparqlQueryUtils.extractBindings(sparqlQuery);
        assertThat(variables, hasSize(5));
        assertThat(variables, hasItems("uri", "label", "hasChildren", "uuid", "isImported"));
    }

    @Test
    public void whenQueryWithMultiplePlaceholdersSupplied_variablesExtracted() {
        String sparqlQuery = "PREFIX xsd: <http://www.w3.org/2001/XMLSchema#>\n" +
                "PREFIX rdfs: <http://www.w3.org/2000/01/rdf-schema#>\n" +
                "PREFIX skos: <http://www.w3.org/2004/02/skos/core#>\n" +
                "PREFIX owl: <http://www.w3.org/2002/07/owl#>\n" +
                "PREFIX rdf: <http://www.w3.org/1999/02/22-rdf-syntax-ns#>\n" +
                "PREFIX asset: <http://dds.semmtech.nl/asset/>\n" +
                "    \n" +
                "SELECT ?uri ?label ?hasChildren ?uuid ?isImported {\n" +
                "    BIND(<http://dds.semmtech.nl/asset/Gebouw> as ?parentUri) .\n" +
                "    BIND(?parent_uuid as ?parentUuid) .\n" +
                "    ?uri rdf:type owl:Class ;\n" +
                "        rdfs:subClassOf ?parentUri .\n" +
                "   \n" +
                "    OPTIONAL {\n" +
                "        ?uri skos:prefLabel ?label\n" +
                "    }\n" +
                "     \n" +
                "    BIND(EXISTS { ?child rdfs:subClassOf ?uri } as ?hasChildren) .\n" +
                " \n" +
                "    OPTIONAL {\n" +
                "      # VALUES is populated by the result of the XML webservice:\n" +
                "      VALUES (?imported_foreignKey ?imported_foreignKey2) {\n" +
                " ?placeholder }\n" +
                "      FILTER (?uri = ?additional_foreignKey) .\n" +
                "      BIND(true as ?inner_imported) .\n" +
                "    }\n" +
                "    BIND(COALESCE(?inner_imported, false) as ?isImported) . \n" +
                "    BIND(UUID() as ?uuid) .\n" +
                "}";
        SparqlQueryUtils sparqlQueryUtils = new SparqlQueryUtils();
        List<String> variables = sparqlQueryUtils.extractBindings(sparqlQuery);
        assertThat(variables, hasSize(5));
        assertThat(variables, contains("uri", "label", "hasChildren", "uuid", "isImported"));
    }

    @Test
    public void test() {
        String queryString = "SELECT  ?sourceId ?targetId ?relationType \n" +
                "{\n" +
                "  BIND(?uuid as ?sourceId)\n" +
                "  BIND(?attributeExternalId as ?targetId)\n" +
                "  BIND(\"HAS_PROPERTY\" as ?relationType)\n" +
                "  VALUES (?imported_sourceId ?imported_attributeExternalId) {\n" +
                "    ?imported_placeholder\n" +
                "  }\n" +
                "}";
        ParameterizedSparqlString query = new ParameterizedSparqlString(queryString);

        Pattern pattern = Pattern.compile("VALUES\\s*\\((?<variables>(?:\\?imported_\\w+\\s*)+)\\)\\s*\\{\\s*\\?(?<placeholder>\\w+)\\s*\\}", Pattern.CASE_INSENSITIVE);
        Matcher matcher = pattern.matcher(queryString);

        Map<String, List<Map<String, String>>> propagatedValues =
                Map.of("uuid1",
                        List.of(
                                Map.of(
                                        "sourceId", "sourceId 1",
                                        "attributeExternalId", "attribute 1"),
                                Map.of("sourceId", "sourceId 2",
                                        "attributeExternalId", "attribute 2"
                                )
                        ),
                        "uuid2",
                        List.of(
                                Map.of(
                                        "sourceId", "sourceId 1.1",
                                        "attributeExternalId", "attribute 1.1"),
                                Map.of("sourceId", "sourceId 2.1",
                                        "attributeExternalId", "attribute 2.1"
                                )
                        ));

        List<String> uuids = List.of("uuid1","uuid2");
        if (matcher.find()) {
            List<String> variables = Arrays.asList(matcher.group("variables").split(" ")).stream().map(variable -> variable.replace("?imported_","")).collect(Collectors.toList());
            String placeholder = matcher.group("placeholder");

            uuids.stream()
                    .map(propagatedValues::get)
                    .map(allMaps -> filterAndProjectToRequiredKeys(allMaps, variables))
                    .map(filteredNodes -> transformAll(filteredNodes, variables))
                    .forEach(list -> query.setRowValues(placeholder, list));

            System.out.println(query.toString());
        }
    }

    private List<Map<String, String>> filterAndProjectToRequiredKeys(List<Map<String, String>> allMaps, List<String> variables) {
        return allMaps.stream()
                    .filter(map -> map.keySet().containsAll(variables))
                .map(map -> Maps.filterKeys(map, variables::contains))
                .distinct()
                .collect(Collectors.toList());
    }

    private Collection<List<? extends RDFNode>> transformAll(List<Map<String, String>> filteredNodes, List<String> variables) {
        return filteredNodes.stream().map(node -> transform(node, variables))
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
}
