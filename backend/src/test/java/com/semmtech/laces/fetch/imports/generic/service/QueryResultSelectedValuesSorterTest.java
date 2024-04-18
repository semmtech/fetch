package com.semmtech.laces.fetch.imports.generic.service;

import com.semmtech.laces.fetch.visualization.model.QueryExecutionRequest;
import org.junit.Test;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

import static org.hamcrest.Matchers.equalTo;
import static org.junit.Assert.assertThat;

public class QueryResultSelectedValuesSorterTest {

    @Test
    public void testTreeNodeSort() {
        QueryExecutionRequest input = QueryExecutionRequest.builder().values(Arrays.asList(
                createValue("urn:uuid:2881078a-7ca5-4544-6138-525400767bfb", "urn:uuid:122dd84a-e46b-c804-7c13-525400767bfb", "1.1.3.1"),
                createValue("urn:uuid:afb51fb3-5cf4-7cd4-bcb7-525400767bfb", "urn:uuid:2881078a-7ca5-4544-6138-525400767bfb", "1.1.3.1.1"),
                createValue("urn:uuid:de20929b-dce2-8aa4-2b89-525400767bfb", "urn:uuid:2881078a-7ca5-4544-6138-525400767bfb", "1.1.3.1.2"),
                createValue("urn:uuid:b320ca54-ef13-2b44-ee15-525400767bfb", "urn:uuid:122dd84a-e46b-c804-7c13-525400767bfb", "1.1.3.2"),
                createValue("urn:uuid:3ec32e87-0844-eb44-d02c-525400767bfb", "urn:uuid:122dd84a-e46b-c804-7c13-525400767bfb", "1.1.3.6"),
                createValue("urn:uuid:70d1e3cf-34ac-4764-8408-525400767bfb", "urn:uuid:3ec32e87-0844-eb44-d02c-525400767bfb", "1.1.3.6.1"),
                createValue("urn:uuid:de18f8d1-f590-4ba4-1428-525400767bfb", "urn:uuid:122dd84a-e46b-c804-7c13-525400767bfb", "1.1.3.13"),
                createValue("urn:uuid:0ead9ce8-9331-d364-99c0-525400767bfb", "urn:uuid:de18f8d1-f590-4ba4-1428-525400767bfb", "1.1.3.13.1"),
                createValue("urn:uuid:5ef8fc38-e811-6e54-05ad-525400767bfb", "urn:uuid:de18f8d1-f590-4ba4-1428-525400767bfb", "1.1.3.13.2"),
                createValue("urn:uuid:a36a8941-2489-abc4-4677-525400767bfb", "urn:uuid:de18f8d1-f590-4ba4-1428-525400767bfb", "1.1.3.13.3"),
                createValue("urn:uuid:122dd84a-e46b-c804-7c13-525400767bfb", "urn:uuid:909a2da6-4624-9ce4-537e-525400767bfb", "1.1.3"),
                createValue("urn:uuid:909a2da6-4624-9ce4-537e-525400767bfb", "urn:uuid:0f608576-1033-3f74-fd45-525400767bfb", "1.1"),
                createValue("urn:uuid:0f608576-1033-3f74-fd45-525400767bfb", null, "1")
        )).build();

        new TreeNodeSelectedValuesSorter().sort(input);

        QueryExecutionRequest expected = QueryExecutionRequest.builder().values(Arrays.asList(
                createValue("urn:uuid:0f608576-1033-3f74-fd45-525400767bfb", null, "1"),
                createValue("urn:uuid:909a2da6-4624-9ce4-537e-525400767bfb", "urn:uuid:0f608576-1033-3f74-fd45-525400767bfb", "1.1"),
                createValue("urn:uuid:122dd84a-e46b-c804-7c13-525400767bfb", "urn:uuid:909a2da6-4624-9ce4-537e-525400767bfb", "1.1.3"),
                createValue("urn:uuid:2881078a-7ca5-4544-6138-525400767bfb", "urn:uuid:122dd84a-e46b-c804-7c13-525400767bfb", "1.1.3.1"),
                createValue("urn:uuid:afb51fb3-5cf4-7cd4-bcb7-525400767bfb", "urn:uuid:2881078a-7ca5-4544-6138-525400767bfb", "1.1.3.1.1"),
                createValue("urn:uuid:de20929b-dce2-8aa4-2b89-525400767bfb", "urn:uuid:2881078a-7ca5-4544-6138-525400767bfb", "1.1.3.1.2"),
                createValue("urn:uuid:b320ca54-ef13-2b44-ee15-525400767bfb", "urn:uuid:122dd84a-e46b-c804-7c13-525400767bfb", "1.1.3.2"),
                createValue("urn:uuid:3ec32e87-0844-eb44-d02c-525400767bfb", "urn:uuid:122dd84a-e46b-c804-7c13-525400767bfb", "1.1.3.6"),
                createValue("urn:uuid:70d1e3cf-34ac-4764-8408-525400767bfb", "urn:uuid:3ec32e87-0844-eb44-d02c-525400767bfb", "1.1.3.6.1"),
                createValue("urn:uuid:de18f8d1-f590-4ba4-1428-525400767bfb", "urn:uuid:122dd84a-e46b-c804-7c13-525400767bfb", "1.1.3.13"),
                createValue("urn:uuid:0ead9ce8-9331-d364-99c0-525400767bfb", "urn:uuid:de18f8d1-f590-4ba4-1428-525400767bfb", "1.1.3.13.1"),
                createValue("urn:uuid:5ef8fc38-e811-6e54-05ad-525400767bfb", "urn:uuid:de18f8d1-f590-4ba4-1428-525400767bfb", "1.1.3.13.2"),
                createValue("urn:uuid:a36a8941-2489-abc4-4677-525400767bfb", "urn:uuid:de18f8d1-f590-4ba4-1428-525400767bfb", "1.1.3.13.3")
        )).build();

        assertThat(input, equalTo(expected));

    }

    private Map<String, String> createValue(String uuid,String parentUuid,String treeNodeId) {
        Map<String, String> map = new HashMap<>();
        map.put("uuid", uuid);
        map.put("parentUuid", parentUuid);
        map.put("treeNodeId", treeNodeId);
        return map;
    }

 }
