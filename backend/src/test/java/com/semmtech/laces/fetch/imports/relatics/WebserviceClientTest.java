package com.semmtech.laces.fetch.imports.relatics;

import com.semmtech.laces.fetch.configuration.entities.*;
import com.semmtech.laces.fetch.imports.relatics.model.ImportResponse;
import com.semmtech.laces.fetch.imports.relatics.response.NoNamespaceUnmarshallingJaxb2Marshaller;
import com.semmtech.laces.fetch.imports.relatics.service.ImportDataXmlProvider;
import com.semmtech.laces.fetch.imports.relatics.service.WebserviceClient;
import com.semmtech.laces.fetch.imports.relatics.service.WebserviceClientConfig;
import com.semmtech.laces.fetch.visualization.model.QueryResult;
import org.apache.commons.codec.binary.Base64;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.oxm.jaxb.Jaxb2Marshaller;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.ws.test.client.MockWebServiceServer;
import org.springframework.xml.transform.StringSource;

import javax.xml.transform.Source;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.hamcrest.Matchers.hasEntry;
import static org.hamcrest.Matchers.hasSize;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertThat;
import static org.springframework.ws.test.client.RequestMatchers.anything;
import static org.springframework.ws.test.client.RequestMatchers.connectionTo;
import static org.springframework.ws.test.client.ResponseCreators.withPayload;


@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = WebserviceClientConfig.class)
public class WebserviceClientTest {

    @Autowired
    private WebserviceClient relaticsClient;

    private MockWebServiceServer mockServer;

    @Before
    public void init(){
        mockServer = MockWebServiceServer.createServer(relaticsClient);
    }

    @Test
    public void testStep1() {

        Source response = new StringSource("<ImportResponse  xmlns=\"http://www.relatics.com/\"><ImportResult><Import xmlns=\"\"><Message Time=\"10:33:50\" Result=\"Progress\">Successfully created ImportLog.</Message><Message Time=\"10:33:50\" Result=\"Comment\">Cleared 0 empty row(s) from the table.</Message><Message Time=\"10:33:50\" Result=\"Comment\">The size of the import is valid: importing 6 cells</Message><Message Time=\"10:33:50\" Result=\"Warning\">Source column 'userAccount' not found. Specification 'CreatedBy' ignored.</Message><Message Time=\"10:33:50\" Result=\"Warning\">Source column 'userAccount' not found. Specification 'CreatedBy' ignored.</Message><Message Time=\"10:33:50\" Result=\"Warning\">Source column 'userAccount' not found. Specification 'CreatedBy' ignored.</Message><Message Time=\"10:33:50\" Result=\"Progress\">Processing row : 00001</Message><Message Time=\"10:33:50\" Result=\"Success\">Parent (applied conceptual): Element 'Gemaal' found, but nothing updated</Message><Message Time=\"10:33:50\" Result=\"Success\">Child  (applied conceptual): Element 'Centrifugaal' found, but nothing updated</Message><Message Time=\"10:33:50\" Result=\"Success\">is a whole for: Relation 'is a whole for' found, but nothing updated</Message><Message Time=\"10:33:50\" Result=\"Progress\">Processing row : 00002</Message><Message Time=\"10:33:50\" Result=\"Success\">Parent (applied conceptual): Element 'Gemaal' found, but nothing updated</Message><Message Time=\"10:33:50\" Result=\"Success\">Child  (applied conceptual): Element 'Vijzel installatie' found, but nothing updated</Message><Message Time=\"10:33:50\" Result=\"Success\">is a whole for: Relation 'is a whole for' found, but nothing updated</Message><Message Time=\"10:33:50\" Result=\"Progress\">10:33:50 Import completed successfully.</Message><Message Time=\"10:33:50\" Result=\"Progress\">Total rows imported: 2</Message><Message Time=\"10:33:50\" Result=\"Progress\">Total time (ms): 78</Message><Message Time=\"10:33:50\" Result=\"Progress\">Total time per row (ms): 38.062</Message><Message Time=\"10:33:50\" Result=\"Progress\">The importlog is saved with the Import Run.</Message><Elements><Element Action=\"Update\" ID=\"0dc70fd9-b95a-e911-a2d7-00155d653c02\" ForeignKey=\"urn:uuid:object-0\" /><Element Action=\"Update\" ID=\"0dc70fd9-b95a-e911-a2d7-00155d653c02\" ForeignKey=\"urn:uuid:object-0\" /></Elements></Import></ImportResult></ImportResponse>");

        mockServer.expect(anything()).andRespond(withPayload(response));

        final var webservice = TargetDataSystemEntity.builder()
                .entryCode("b93k9JkJS9g")
                .operationName("ImportPhysicalObjectHierarchy")
                .build();
        ImportStepEntity importStep =
                ImportStepEntity.builder()
                        .name("test")
                        .importTarget("webservice")
                        .sparqlQuery(
                                SparqlQueryWithDefaultGraphs
                                        .builder()
                                        .query(
                                                SparqlQueryEntity
                                                        .builder()
                                                        .query("test")
                                                        .build())
                                        .build())
                        .build();

        WorkspaceEntity workspace = getRelaticsWorkspaceConfiguration();

        Map<String, String>  record1 = new HashMap<>();
        record1.put("parentForeignKey", "urn:uuid:object-0");
        record1.put("childForeignKey", "urn:uuid:object-1");

        Map<String, String>  record2 = new HashMap<>();
        record2.put("parentForeignKey", "urn:uuid:object-0");
        record2.put("childForeignKey", "urn:uuid:object-2");

        List<Map<String, String>> input = Arrays.asList(record1, record2);
        QueryResult queryResult = QueryResult.builder().values(input).build();
        ImportDataXmlProvider xmlProvider = new ImportDataXmlProvider();

        String data = Base64.encodeBase64String(xmlProvider.toImportXml(queryResult).getBytes(StandardCharsets.UTF_8));

        ImportResponse importResponse = relaticsClient.sendData(importStep, workspace, data, "https://semmtech.relaticsonline.com/DataExchange.asmx",webservice);

        assertThat(importResponse.getImportResult()
                .getImport()
                .getMessage(), hasSize(19));

        mockServer.verify();
    }

    @Test
    public void testStep2() {

        Source response = new StringSource("<ImportResponse  xmlns=\"http://www.relatics.com/\"><ImportResult><Import xmlns=\"\"><Message Time=\"10:33:50\" Result=\"Progress\">Successfully created ImportLog.</Message><Message Time=\"10:33:50\" Result=\"Comment\">Cleared 0 empty row(s) from the table.</Message><Message Time=\"10:33:50\" Result=\"Comment\">The size of the import is valid: importing 6 cells</Message><Message Time=\"10:33:50\" Result=\"Warning\">Source column 'userAccount' not found. Specification 'CreatedBy' ignored.</Message><Message Time=\"10:33:50\" Result=\"Warning\">Source column 'userAccount' not found. Specification 'CreatedBy' ignored.</Message><Message Time=\"10:33:50\" Result=\"Warning\">Source column 'userAccount' not found. Specification 'CreatedBy' ignored.</Message><Message Time=\"10:33:50\" Result=\"Progress\">Processing row : 00001</Message><Message Time=\"10:33:50\" Result=\"Success\">Parent (applied conceptual): Element 'Gemaal' found, but nothing updated</Message><Message Time=\"10:33:50\" Result=\"Success\">Child  (applied conceptual): Element 'Centrifugaal' found, but nothing updated</Message><Message Time=\"10:33:50\" Result=\"Success\">is a whole for: Relation 'is a whole for' found, but nothing updated</Message><Message Time=\"10:33:50\" Result=\"Progress\">Processing row : 00002</Message><Message Time=\"10:33:50\" Result=\"Success\">Parent (applied conceptual): Element 'Gemaal' found, but nothing updated</Message><Message Time=\"10:33:50\" Result=\"Success\">Child  (applied conceptual): Element 'Vijzel installatie' found, but nothing updated</Message><Message Time=\"10:33:50\" Result=\"Success\">is a whole for: Relation 'is a whole for' found, but nothing updated</Message><Message Time=\"10:33:50\" Result=\"Progress\">10:33:50 Import completed successfully.</Message><Message Time=\"10:33:50\" Result=\"Progress\">Total rows imported: 2</Message><Message Time=\"10:33:50\" Result=\"Progress\">Total time (ms): 78</Message><Message Time=\"10:33:50\" Result=\"Progress\">Total time per row (ms): 38.062</Message><Message Time=\"10:33:50\" Result=\"Progress\">The importlog is saved with the Import Run.</Message><Elements><Element Action=\"Update\" ID=\"0dc70fd9-b95a-e911-a2d7-00155d653c02\" ForeignKey=\"urn:uuid:object-0\" /><Element Action=\"Update\" ID=\"0dc70fd9-b95a-e911-a2d7-00155d653c02\" ForeignKey=\"urn:uuid:object-0\" /></Elements></Import></ImportResult></ImportResponse>");

        mockServer.expect(anything()).andRespond(withPayload(response));

        final var webservice = TargetDataSystemEntity.builder()
                .entryCode("b93k9JkJS9g")
                .operationName("ImportPhysicalObjectHierarchy")
                .build();
        ImportStepEntity importStep =
                ImportStepEntity.builder()
                        .name("step 2")
                        .importTarget("Webservice")
                        .sparqlQuery(
                                SparqlQueryWithDefaultGraphs
                                        .builder()
                                        .query(
                                                SparqlQueryEntity
                                                        .builder()
                                                        .query("test")
                                                        .build())
                                        .build())
                        .build();

        WorkspaceEntity workspace = getRelaticsWorkspaceConfiguration();

        Map<String, String>  record1 = new HashMap<>();
        record1.put("appliedForeignKey", "urn:uuid:object-0");
        record1.put("genericForeignKey", "http://dds.semmtech.nl/asset/Gemaal");
        record1.put("name", "Gemaal");
        record1.put("description", "Beschrijving gemaal");
        record1.put("remark", "Opmerking gemaal");

        Map<String, String>  record2 = new HashMap<>();
        record2.put("appliedForeignKey", "urn:uuid:object-1");
        record2.put("genericForeignKey", "http://dds.semmtech.nl/asset/Centrifugaal");
        record2.put("name", "Centrifugaal");
        record2.put("description", "Beschrijving centrifugaal");
        record2.put("remark", "Opmerking centrifugaal");

        Map<String, String>  record3 = new HashMap<>();
        record3.put("appliedForeignKey", "urn:uuid:object-2");
        record3.put("genericForeignKey", "http://dds.semmtech.nl/asset/VijzelInstallatie");
        record3.put("name", "Vijzel Installatie");
        record3.put("description", "Beschrijving vijzel installatie");
        record3.put("remark", "Opmerking vijzel installatie");

        List<Map<String, String>> input = Arrays.asList(record1, record2, record3);
        QueryResult queryResult = QueryResult.builder().values(input).build();
        ImportDataXmlProvider xmlProvider = new ImportDataXmlProvider();

        String data = Base64.encodeBase64String(xmlProvider.toImportXml(queryResult).getBytes(StandardCharsets.UTF_8));

        ImportResponse importResponse = relaticsClient.sendData(importStep, workspace, data, "https://semmtech.relaticsonline.com/DataExchange.asmx", webservice);

        importResponse.getImportResult()
                .getImport()
                .getMessage()
                .forEach(System.out::println);

        mockServer.verify();
    }

    private WorkspaceEntity getRelaticsWorkspaceConfiguration() {
        return WorkspaceEntity.builder()
                .workspaceId("c019c2bf-de34-e911-a2d3-00155d653c02")
                .build();
    }

    @Test
    public void unmarshallResponse() {

        Jaxb2Marshaller marshaller = new NoNamespaceUnmarshallingJaxb2Marshaller();
        marshaller.setContextPath("com.semmtech.laces.fetch.imports.relatics.model");

        Source response = new StringSource("<ImportResponse  xmlns=\"http://www.relatics.com/\"><ImportResult><Import /></ImportResult></ImportResponse>");

        ImportResponse importResponse = (ImportResponse) marshaller.unmarshal(response);
        assertNotNull(importResponse);
    }

    @Test
    public void loadItemsFromRelatics() {
        Source response = new StringSource(
                "<Report ReportName=\"LACES Decompostion - Existing physical objects\" GeneratedOn=\"2019-04-18\" EnvironmentID=\"e56f5475-b28c-41dd-84ea-1e189aa44da7\" EnvironmentName=\"Semmtech\" EnvironmentURL=\"https://semmtech.relaticsonline.com/\" WorkspaceID=\"c019c2bf-de34-e911-a2d3-00155d653c02\" WorkspaceName=\"Laces Fetch ontwikkelomgeving Heijmans Neanex\" TargetDevice=\"Pc\">\n" +
                "  <ReportPart>\n" +
                "    <generic ForeignKey=\"http://dds.semmtech.nl/asset/Bouwwerk\"/>\n" +
                "    <generic ForeignKey=\"http://dds.semmtech.nl/asset/Centrifugaal\"/>\n" +
                "    <generic ForeignKey=\"http://dds.semmtech.nl/asset/Gemaal\"/>\n" +
                "    <generic ForeignKey=\"http://dds.semmtech.nl/asset/Vijzelinstallatie\"/>\n" +
                "  </ReportPart>\n" +
                "</Report>");

        mockServer.expect(connectionTo("https://semmtech.relaticsonline.com/DataExchange.asmx")).andRespond(withPayload(response));

        final var targetDataSystem = TargetDataSystemEntity
                .builder()
                .xPathExpression("//ReportPart/generic/@ForeignKey")
                .entryCode("entryCode")
                .operationName("GetResult")
                .build();

        WorkspaceEntity workspaceEntity = getRelaticsWorkspaceConfiguration();

        List<Map<String, String>> result = relaticsClient.readData(workspaceEntity,"https://semmtech.relaticsonline.com/DataExchange.asmx", targetDataSystem);

        assertThat(result.get(0), hasEntry("foreignkey", "http://dds.semmtech.nl/asset/Bouwwerk"));
        assertThat(result.get(1), hasEntry("foreignkey", "http://dds.semmtech.nl/asset/Centrifugaal"));
        assertThat(result.get(2), hasEntry("foreignkey", "http://dds.semmtech.nl/asset/Gemaal"));
        assertThat(result.get(3), hasEntry("foreignkey", "http://dds.semmtech.nl/asset/Vijzelinstallatie"));
    }


}
