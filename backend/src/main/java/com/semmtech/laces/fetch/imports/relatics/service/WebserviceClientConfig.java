package com.semmtech.laces.fetch.imports.relatics.service;

import com.semmtech.laces.fetch.imports.relatics.response.NoNamespaceUnmarshallingJaxb2Marshaller;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.oxm.jaxb.Jaxb2Marshaller;

@Configuration
public class WebserviceClientConfig {

    @Value("${fetch.relatics.timeout:600}")
    private long relaticsTimeout;

    public long getRelaticsTimeout() {
        return relaticsTimeout;
    }

    public void setRelaticsTimeout(long relaticsTimeout) {
        this.relaticsTimeout = relaticsTimeout;
    }

    @Bean
    public Jaxb2Marshaller marshaller() {
        Jaxb2Marshaller marshaller = new NoNamespaceUnmarshallingJaxb2Marshaller();
        marshaller.setContextPath("com.semmtech.laces.fetch.imports.relatics.model");
        return marshaller;
    }

    @Bean
    public XPathExtractorService xPathExtractorService() {
        return new XPathExtractorService();
    }

    @Bean
    public WebserviceClient webserviceClient(Jaxb2Marshaller marshaller, XPathExtractorService xPathExtractorService) {
        WebserviceClient client = new WebserviceClient(xPathExtractorService);
        client.setMarshaller(marshaller);
        client.setUnmarshaller(marshaller);
        client.setTimeout(getRelaticsTimeout());
        return client;
    }
}