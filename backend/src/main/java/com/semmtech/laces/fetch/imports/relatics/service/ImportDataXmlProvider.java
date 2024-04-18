package com.semmtech.laces.fetch.imports.relatics.service;

import com.semmtech.laces.fetch.imports.generic.service.ImportException;
import com.semmtech.laces.fetch.visualization.model.Column;
import com.semmtech.laces.fetch.visualization.model.QueryResult;

import javax.xml.stream.XMLOutputFactory;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamWriter;
import java.io.StringWriter;
import java.util.List;
import java.util.Map;
import org.jooq.lambda.Unchecked;
import org.springframework.stereotype.Component;

@Component
public class ImportDataXmlProvider {
    public String toImportXml(QueryResult queryResult) {
        StringWriter stringWriter = new StringWriter();
        try {
            XMLOutputFactory xmlOutputFactory = XMLOutputFactory.newInstance();
            XMLStreamWriter xmlStreamWriter =
                    xmlOutputFactory.createXMLStreamWriter(stringWriter);

            xmlStreamWriter.writeStartElement("Import");
            queryResult.getValues()
                    .forEach(Unchecked.consumer((Map<String, String> record) -> writeRow(queryResult.getColumns(), record, xmlStreamWriter)));
            xmlStreamWriter.writeEndElement();
        } catch (XMLStreamException xse) {
            throw new ImportException(xse);
        }

        return  stringWriter.toString();
    }

    private void writeRow(List<Column> columns, Map<String, String> record, XMLStreamWriter xmlStreamWriter) throws XMLStreamException {
        xmlStreamWriter.writeStartElement("Row");
        columns.stream()
                .map(Column::getName)
                .forEach(Unchecked.consumer(column -> xmlStreamWriter.writeAttribute(column, record.getOrDefault(column, ""))));

        xmlStreamWriter.writeEndElement();
    }
}
