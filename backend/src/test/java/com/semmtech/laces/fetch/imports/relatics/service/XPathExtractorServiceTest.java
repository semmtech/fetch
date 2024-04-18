package com.semmtech.laces.fetch.imports.relatics.service;

import com.semmtech.laces.fetch.imports.generic.service.ImportException;
import org.junit.Test;
import org.springframework.xml.transform.StringSource;
import org.w3c.dom.Document;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.dom.DOMSource;
import java.io.IOException;
import java.io.StringReader;
import java.util.List;
import java.util.Map;

import static org.hamcrest.Matchers.*;
import static org.junit.Assert.assertThat;

public class XPathExtractorServiceTest {
    @Test
    public void givenValidXML_AndXPathMatches_returnsData() throws ParserConfigurationException, IOException, SAXException {
        String xml = "<root><firstChild><childWithAttribute attribute=\"attrValue\" /></firstChild><secondChild><childWithAttribute attribute=\"secondAttribute\" /></secondChild></root>";

        DocumentBuilder db = DocumentBuilderFactory.newInstance().newDocumentBuilder();
        Document doc = db.parse( new InputSource( new StringReader( xml ) ) );
        DOMSource source = new DOMSource(doc);

        XPathExtractorService xPathExtractorService = new XPathExtractorService();
        List<Map<String, String>> result = xPathExtractorService.extract("//@attribute", source);

        assertThat(result, hasSize(2));
        assertThat(result.get(0), hasEntry("attribute", "attrValue"));
        assertThat(result.get(1), hasEntry("attribute", "secondAttribute"));
    }

    @Test
    public void givenValidXML_AndXPathDoesntMatch_returnsEmptyList() throws ParserConfigurationException, IOException, SAXException {
        String xml = "<root><firstChild><childWithAttribute attribute=\"attrValue\" /></firstChild><secondChild><childWithAttribute attribute=\"secondAttribute\" /></secondChild></root>";

        DocumentBuilder db = DocumentBuilderFactory.newInstance().newDocumentBuilder();
        Document doc = db.parse( new InputSource( new StringReader( xml ) ) );
        DOMSource source = new DOMSource(doc);

        XPathExtractorService xPathExtractorService = new XPathExtractorService();
        List<Map<String, String>> result = xPathExtractorService.extract("//@attributes", source);

        assertThat(result, hasSize(0));
    }


    @Test (expected = ImportException.class)
    public void givenValidXML_InvalidXPath_throwsImportException() throws ParserConfigurationException, IOException, SAXException {
        String xml = "<root><firstChild><childWithAttribute attribute=\"attrValue\" /></firstChild><secondChild><childWithAttribute attribute=\"secondAttribute\" /></secondChild></root>";

        DocumentBuilder db = DocumentBuilderFactory.newInstance().newDocumentBuilder();
        Document doc = db.parse( new InputSource( new StringReader( xml ) ) );
        DOMSource source = new DOMSource(doc);

        XPathExtractorService xPathExtractorService = new XPathExtractorService();
        List<Map<String, String>> result = xPathExtractorService.extract("//]@attributes", source);
    }
}
