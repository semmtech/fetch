package com.semmtech.laces.fetch.imports.generic.service;

import com.semmtech.laces.fetch.visualization.model.QueryExecutionRequest;
import org.junit.Test;

import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import static org.hamcrest.Matchers.allOf;
import static org.hamcrest.Matchers.hasEntry;
import static org.junit.Assert.assertThat;

public class QueryExecutionRequestParentsPreprocessorTest {

    @Test
    public void givenAllNodesWithParents_whenRemovingParents_noChangesMade() {
        Map<String, String> rootValue = new HashMap<>();
        rootValue.put("uri", "uri1");
        Map<String, String> firstChildValue = new HashMap<>();
        firstChildValue.put("uri", "uri2");
        firstChildValue.put("parentUri", "uri1");
        Map<String, String> secondChildValue = new HashMap<>();
        secondChildValue.put("uri", "uri3");
        secondChildValue.put("parentUri", "uri1");

        QueryExecutionRequest request =
                QueryExecutionRequest.builder()
                        .values(
                            Arrays.asList(
                                    rootValue,
                                    firstChildValue,
                                    secondChildValue
                            )
                        )
                        .build();

        QueryExecutionRequestParentsPreprocessor preprocessor = new QueryExecutionRequestParentsPreprocessor();
        preprocessor.removeParentsFromOrphans(request);

        assertThat(request.getValues().get(0), hasEntry("uri", "uri1"));
        assertThat(request.getValues().get(1),
                allOf(
                        hasEntry("uri", "uri2"),
                        hasEntry("parentUri", "uri1")
                ));
        assertThat(request.getValues().get(2),
                allOf(
                        hasEntry("uri", "uri3"),
                        hasEntry("parentUri", "uri1")
                ));
    }

    @Test
    public void givenANodeWithNotSelectedParent_whenRemovingParents_parentCleared() {
        Map<String, String> rootValue = new HashMap<>();
        rootValue.put("uri", "uri1");
        Map<String, String> firstChildValue = new HashMap<>();
        firstChildValue.put("uri", "uri2");
        firstChildValue.put("parentUri", "uri1");
        Map<String, String> secondChildValue = new HashMap<>();
        secondChildValue.put("uri", "uri3");
        secondChildValue.put("parentUri", "uri4");

        QueryExecutionRequest request =
                QueryExecutionRequest.builder()
                        .values(
                                Arrays.asList(
                                        rootValue,
                                        firstChildValue,
                                        secondChildValue
                                )
                        )
                        .build();

        QueryExecutionRequestParentsPreprocessor preprocessor = new QueryExecutionRequestParentsPreprocessor();
        preprocessor.removeParentsFromOrphans(request);

        assertThat(request.getValues().get(0), hasEntry("uri", "uri1"));
        assertThat(request.getValues().get(1),
                allOf(
                        hasEntry("uri", "uri2"),
                        hasEntry("parentUri", "uri1")
                ));
        assertThat(request.getValues().get(2),
                hasEntry("uri", "uri3")
        );
    }

    @Test
    public void givenOnlyRootNodeSelected_whenRemovingParents_nothingChanged() {
        Map<String, String> rootValue = new HashMap<>();
        rootValue.put("uri", "uri1");

        QueryExecutionRequest request =
                QueryExecutionRequest.builder()
                        .values(
                                Collections.singletonList(
                                        rootValue
                                )
                        )
                        .build();

        QueryExecutionRequestParentsPreprocessor preprocessor = new QueryExecutionRequestParentsPreprocessor();
        preprocessor.removeParentsFromOrphans(request);
        assertThat(request.getValues().get(0), hasEntry("uri", "uri1"));
    }

    @Test
    public void givenOnlyOrphan_whenRemovingParents_parentCleared() {
        Map<String, String> orphanValue = new HashMap<>();
        orphanValue.put("uri", "uri3");
        orphanValue.put("parentUri", "uri4");

        QueryExecutionRequest request =
                QueryExecutionRequest.builder()
                        .values(
                                Collections.singletonList(
                                        orphanValue
                                )
                        )
                        .build();

        QueryExecutionRequestParentsPreprocessor preprocessor = new QueryExecutionRequestParentsPreprocessor();
        preprocessor.removeParentsFromOrphans(request);

        assertThat(request.getValues().get(0),
                hasEntry("uri", "uri3")
        );
    }

}
