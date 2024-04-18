package com.semmtech.laces.fetch.imports.relatics;

import com.semmtech.laces.fetch.imports.relatics.service.ImportDataXmlProvider;
import com.semmtech.laces.fetch.visualization.model.Column;
import com.semmtech.laces.fetch.visualization.model.QueryResult;
import org.junit.Test;

import javax.xml.stream.XMLStreamException;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

import static org.hamcrest.Matchers.allOf;
import static org.hamcrest.Matchers.containsString;
import static org.junit.Assert.assertThat;


public class ImportDataXmlProviderTest {

    @Test
    public void convertTreeNodeToXml() {
        List<Column> columns =
                List.of(
                        Column.builder().name("key1").build(),
                        Column.builder().name("key2").build(),
                        Column.builder().name("key3").build(),
                        Column.builder().name("key4").build());

        Map<String, String> record1 =
                Map.of( "key1", "value1.1",
                        "key2", "value1.2",
                        "key3", "value1.3",
                        "key4", "value1.4");

        Map<String, String> record2 =
                Map.of( "key1", "value2.1",
                        "key2", "value2.2",
                        "key3", "value2.3");


        QueryResult result =
                QueryResult.builder()
                        .columns(columns)
                        .values(Arrays.asList(record1, record2))
                        .build();

        ImportDataXmlProvider provider = new ImportDataXmlProvider();
        String xml = provider.toImportXml(result);

        assertThat(xml,
                allOf(
                        containsString("key1=\"value1.1\""),
                        containsString("key2=\"value1.2\""),
                        containsString("key3=\"value1.3\""),
                        containsString("key4=\"value1.4\""),
                        containsString("key1=\"value2.1\""),
                        containsString("key2=\"value2.2\""),
                        containsString("key3=\"value2.3\""),
                        containsString("key4=\"\"")
                ));

    }

}
