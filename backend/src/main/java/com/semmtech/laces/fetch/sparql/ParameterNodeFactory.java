package com.semmtech.laces.fetch.sparql;

import org.apache.commons.lang3.StringUtils;
import org.apache.jena.graph.Node;
import org.springframework.stereotype.Component;

@Component
public class ParameterNodeFactory {
    private final SparqlTypeCache sparqlTypeCache;

    public ParameterNodeFactory(SparqlTypeCache sparqlTypeCache) {
        this.sparqlTypeCache = sparqlTypeCache;
    }

    public Node createNode(String variableName, String value, String type) {
        if (value == null) {
            return null;
        }

        String nodeType = StringUtils.isEmpty(type) ? sparqlTypeCache.getTypeFor(variableName) : type;
        if (nodeType.equals("typed-literal"))
            return SparqlNodeType.typed_literal.createNode(value);
        else if (nodeType.equals("plain-literal"))
            return SparqlNodeType.plain_literal.createNode(value);

        return SparqlNodeType.valueOf(nodeType).createNode(value);
    }
}
