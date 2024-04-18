package com.semmtech.laces.fetch.imports.relatics.response;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;

import org.xml.sax.helpers.XMLFilterImpl;

public class NamespaceFilter extends XMLFilterImpl {

    private String usedNamespaceUri;
    private boolean addNamespace;

    //State variable
    private boolean addedNamespace = false;

    public NamespaceFilter(String namespaceUri,
                           boolean addNamespace) {
        super();

        if (addNamespace)
            this.usedNamespaceUri = namespaceUri;
        else
            this.usedNamespaceUri = "";
        this.addNamespace = addNamespace;
    }



    @Override
    public void startDocument() throws SAXException {
        super.startDocument();
        if (addNamespace) {
            startControlledPrefixMapping();
        }
    }



    @Override
    public void startElement(String uri, String localName, String qName,
                             Attributes atts) throws SAXException {

        super.startElement(this.usedNamespaceUri, localName, qName, atts);
    }

    @Override
    public void endElement(String uri, String localName, String qName)
            throws SAXException {

        super.endElement(this.usedNamespaceUri, localName, qName);
    }

    @Override
    public void startPrefixMapping(String prefix, String url)
            throws SAXException {


        if (addNamespace) {
            this.startControlledPrefixMapping();
        } else {
            //Remove the namespace, i.e. don´t call startPrefixMapping for parent!
        }

    }

    private void startControlledPrefixMapping() throws SAXException {

        if (this.addNamespace && !this.addedNamespace) {
            //We should add namespace since it is set and has not yet been done.
            super.startPrefixMapping("", this.usedNamespaceUri);

            //Make sure we dont do it twice
            this.addedNamespace = true;
        }
    }

}