package com.semmtech.laces.fetch.imports.relatics.response;

import org.springframework.oxm.UnmarshallingFailureException;
import org.springframework.oxm.XmlMappingException;
import org.springframework.oxm.jaxb.Jaxb2Marshaller;
import org.springframework.oxm.mime.MimeContainer;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.XMLReader;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParserFactory;
import javax.xml.transform.*;
import javax.xml.transform.sax.SAXSource;
import javax.xml.transform.stream.StreamResult;
import java.io.StringReader;
import java.io.StringWriter;

public class NoNamespaceUnmarshallingJaxb2Marshaller extends Jaxb2Marshaller {

    @Override
    public Object unmarshal(Source source, MimeContainer mimeContainer) throws XmlMappingException {
        try {
            Transformer t = TransformerFactory.newInstance().newTransformer();
            StringWriter sw = new StringWriter();
            t.transform(source, new StreamResult(sw));
            String xmlString = sw.toString();

            InputSource inputSource = new InputSource(new StringReader(xmlString));
            XMLReader reader = SAXParserFactory.newInstance().newSAXParser().getXMLReader();
            NamespaceFilter filter = new NamespaceFilter("http://www.relatics.com/", true);
            filter.setParent(reader);
            Source newSource = new SAXSource(filter, inputSource);
            return super.unmarshal(newSource, mimeContainer);

        }  catch (SAXException e) {
            throw new UnmarshallingFailureException("Failed to create saxparser for namespace filter.", e);
        } catch (ParserConfigurationException e) {
            throw new UnmarshallingFailureException("Failed to configure saxparser for namespace filter.", e);
        } catch (TransformerException e) {
            throw new UnmarshallingFailureException("Failed to configure transformer for namespace filter.", e);
        }

    }


}
