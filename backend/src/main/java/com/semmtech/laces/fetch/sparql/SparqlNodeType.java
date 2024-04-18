package com.semmtech.laces.fetch.sparql;

import org.apache.jena.graph.Node;
import org.apache.jena.graph.NodeFactory;

import java.util.function.Function;

public enum SparqlNodeType {
    literal (NodeFactory::createLiteral),
    typed_literal (NodeFactory::createLiteral),
    plain_literal (NodeFactory::createLiteral),
    uri (NodeFactory::createURI),
    header (NodeFactory::createBlankNode);

    private Function<String, Node> createNodeFunction;

    SparqlNodeType(Function<String, Node> createNodeFunction) {
        this.createNodeFunction = createNodeFunction;
    }

    public Node createNode(String value) {
        return createNodeFunction.apply(value);
    }
}
