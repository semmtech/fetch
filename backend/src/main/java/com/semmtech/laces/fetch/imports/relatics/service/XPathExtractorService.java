package com.semmtech.laces.fetch.imports.relatics.service;

import com.semmtech.laces.fetch.imports.generic.service.ImportException;
import org.springframework.stereotype.Component;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import javax.xml.transform.Source;
import javax.xml.transform.dom.DOMSource;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpressionException;
import javax.xml.xpath.XPathFactory;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

@Component
public class XPathExtractorService {
    public List<Map<String, String>> extract(String xPathExpression, Source source) {

        XPath xPath = XPathFactory.newInstance().newXPath();
        NodeList nodeList;
        try {
            nodeList =
                    (NodeList) xPath
                            .compile(xPathExpression)
                            .evaluate(((DOMSource)source).getNode(), XPathConstants.NODESET);
        } catch(XPathExpressionException xpee) {
            throw new ImportException(xpee);
        }

        String name = xPathExpression.substring(xPathExpression.lastIndexOf("@")+1);
        return IntStream.range(0, nodeList.getLength())
                .mapToObj(nodeList::item)
                .map(node -> toMap(node, Arrays.asList(name, "relaticsId")))
                .collect(Collectors.toList());
    }

    private static Map<String, String> toMap(Node node, List<String> attributeNames) {
        Map<String, String> extractedValues = new HashMap<>();
        extractedValues.put(attributeNames.get(0).toLowerCase(), node.getNodeValue());
        return extractedValues;
    }

}
