package com.semmtech.laces.fetch.imports.relatics.service;


import com.semmtech.laces.fetch.configuration.entities.ImportStepEntity;
import com.semmtech.laces.fetch.configuration.entities.TargetDataSystemEntity;
import com.semmtech.laces.fetch.configuration.entities.WorkspaceEntity;
import com.semmtech.laces.fetch.imports.relatics.model.Authenticatable;
import com.semmtech.laces.fetch.imports.relatics.model.Authentication;
import com.semmtech.laces.fetch.imports.relatics.model.GetResult;
import com.semmtech.laces.fetch.imports.relatics.model.Identifiable;
import com.semmtech.laces.fetch.imports.relatics.model.Identification;
import com.semmtech.laces.fetch.imports.relatics.model.Import;
import com.semmtech.laces.fetch.imports.relatics.model.ImportResponse;
import org.apache.commons.codec.binary.Base64;
import org.springframework.ws.client.core.WebServiceMessageCallback;
import org.springframework.ws.client.core.WebServiceMessageExtractor;
import org.springframework.ws.client.core.support.WebServiceGatewaySupport;
import org.springframework.ws.soap.SoapMessage;
import org.springframework.ws.soap.client.core.SoapActionCallback;
import org.springframework.ws.support.MarshallingUtils;
import org.springframework.ws.transport.WebServiceMessageSender;
import org.springframework.ws.transport.http.HttpUrlConnectionMessageSender;

import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Map;

public class WebserviceClient extends WebServiceGatewaySupport {

    private final XPathExtractorService xPathExtractorService;

    private long timeout = 600;

    public WebserviceClient(XPathExtractorService xPathExtractorService) {
        this.xPathExtractorService = xPathExtractorService;
    }

    public long getTimeout() {
        return timeout;
    }

    public void setTimeout(long timeout) {
        this.timeout = timeout;
    }

    public ImportResponse sendData(ImportStepEntity importStepEntity, WorkspaceEntity workspaceEntity, String data, String serviceUrl, TargetDataSystemEntity webservice) {
        Import importRequest = new Import();
        importRequest.setOperation(webservice.getOperationName());

        prepareAuthentication(webservice.getEntryCode(), importRequest);
        prepareIdentification(workspaceEntity, importRequest);
        importRequest.setFilename("laces-fetch-import-" + LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMdd-HHmmss")) + ".xml");
        importRequest.setData(Base64.encodeBase64String(data.getBytes(StandardCharsets.UTF_8)));

        // QUICK FIX: Increase the timeout
        for (WebServiceMessageSender sender : getWebServiceTemplate().getMessageSenders()) {
            try
            {
                Duration duration = Duration.ofSeconds(timeout);
                HttpUrlConnectionMessageSender httpSender = (HttpUrlConnectionMessageSender) sender;
                httpSender.setReadTimeout(duration);
                httpSender.setConnectionTimeout(duration);
                logger.info("Setting timeout to " + timeout + " seconds");
            }
            catch (Exception ex)
            {
                logger.warn("Cannot set WS timeout: " + ex.getMessage());
            }
        }
        logger.info("Sending data to Relatics import for step \"" + importStepEntity.getName() + "\"");
        LocalTime start = LocalTime.now();
        ImportResponse response = (ImportResponse) getWebServiceTemplate()
                .marshalSendAndReceive(serviceUrl, importRequest,
                        new SoapActionCallback("http://www.relatics.com/Import"));

        Duration duration = Duration.between(start, LocalTime.now());
        logger.info("Received response from import step \"" + importStepEntity.getName() + "\" using sendData after " + duration.getSeconds() + " seconds!");

        return response;
    }

    /**
     * Call the endpoint configured to fetch objects from Relatics,
     * by using the operation and xpath expression from the TargetDataSystemConfiguration
     *
     * @param serviceUrl
     * @param additionalInputsConfiguration
     * @return a list of mapped keys and-values to represent the returned objects.
     */
    public List<Map<String, String>> readData(WorkspaceEntity workspaceEntity, String serviceUrl, TargetDataSystemEntity additionalInputsConfiguration) {
        GetResult getResultRequest = new GetResult();
        getResultRequest.setOperation(additionalInputsConfiguration.getOperationName());

        prepareAuthentication(additionalInputsConfiguration.getEntryCode(), getResultRequest);
        prepareIdentification(workspaceEntity, getResultRequest);

        return getWebServiceTemplate().sendAndReceive(
                serviceUrl,
                createGetResultWebServiceMessageCallBack(getResultRequest, "http://www.relatics.com/GetResult"),
                createMessageExtractor(additionalInputsConfiguration)
        );

    }

    private WebServiceMessageCallback createGetResultWebServiceMessageCallBack(GetResult getResultRequest, String soapAction) {
        return webServiceMessage -> {
            MarshallingUtils.marshal(getWebServiceTemplate().getMarshaller(), getResultRequest, webServiceMessage);
            ((SoapMessage) webServiceMessage).setSoapAction(soapAction);
        };
    }

    private WebServiceMessageExtractor<List<Map<String, String>>> createMessageExtractor(TargetDataSystemEntity additionalInputsConfiguration) {
        return message -> xPathExtractorService.extract(additionalInputsConfiguration.getXPathExpression(), message.getPayloadSource());
    }

    private void prepareAuthentication(String entryCode, Authenticatable importRequest) {
        importRequest.setAuthentication(Authentication.newAuthentication(entryCode));
    }

    private void prepareIdentification(WorkspaceEntity workspaceEntity, Identifiable importRequest) {
        importRequest.setIdentification(Identification.newIdentification(workspaceEntity.getWorkspaceId()));
    }

}
