package com.semmtech.laces.fetch.sparql;

import org.apache.commons.lang3.RegExUtils;
import org.apache.jena.query.Query;
import org.apache.jena.query.QueryFactory;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

@Component
public class SparqlQueryUtils {

    /**
     * This pattern detects queries with placeholders, which would not be processed correctly by QueryFactory.create
     */
    public static final Pattern VALUES_PATTERN = Pattern.compile("VALUES\\s*\\((?<variables>(?:\\?imported_\\w+\\s*|\\?additional_\\w+\\s*)+)\\)\\s*\\{\\s*(?<placeholder>\\?\\w+)\\s*\\}", Pattern.CASE_INSENSITIVE);

    public List<String> extractBindings(String queryString) {
        Query query = QueryFactory.create(preparePlaceHolders(queryString)) ;
        return query.getResultVars();
    }

    /**
     * Prepare queries with placeholders by replacing placeholders with null-values.
     * The key of extracting the bindings is in the select-clause, not further in the query.
     * @param queryString The original query string.
     * @return A valid, cleaned up query.
     */
    private String preparePlaceHolders(String queryString) {
        Pattern pattern = VALUES_PATTERN;
        Matcher matcher = pattern.matcher(queryString);
        if (matcher.find()) {
            String placeholderVariable = matcher.group("placeholder");
            int variablesToBindCount = matcher.group("variables").split(" ").length;

            String replacement = IntStream.range(0, variablesToBindCount)
                    .mapToObj(index -> "<urn:nothing>")
                    .collect(
                            Collectors.joining(" "));
            return queryString.replace(placeholderVariable, "(" + replacement + ")");
        }

        return queryString;
    }
}
