package com.semmtech.laces.fetch.configuration.entities;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.google.common.escape.Escaper;
import com.google.common.net.UrlEscapers;
import com.semmtech.laces.fetch.configuration.dtos.common.AuthenticationMethodDto;
import com.semmtech.laces.fetch.configuration.dtos.common.SemmtechTokenAuthenticationDto;
import com.semmtech.laces.fetch.configuration.exceptions.LacesTokenAuthorizationException;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.data.annotation.TypeAlias;
import org.springframework.http.HttpHeaders;

import java.beans.ConstructorProperties;
import java.net.URI;
import java.net.URISyntaxException;
import java.security.GeneralSecurityException;
import java.security.KeyFactory;
import java.security.PrivateKey;
import java.security.Signature;
import java.security.spec.PKCS8EncodedKeySpec;
import java.util.Base64;

@Slf4j
@TypeAlias(value = "SemmtechTokenAuthentication")
@Getter
public class SemmtechTokenAuthenticationEntity extends AuthenticationMethodEntity {

    private String privateKey;
    private String applicationId;

    @JsonCreator
    @ConstructorProperties({"privateKey", "applicationId"})
    public SemmtechTokenAuthenticationEntity(String privateKey, String applicationId) {
        this.privateKey = privateKey;
        this.applicationId = applicationId;
    }

    @Override
    public HttpHeaders headers(String url) {
        HttpHeaders headers = new HttpHeaders();

        try {
            String relativeUrl = getRelativeURL(url);
            String signatureBase64 = calculateSignatureBase64(privateKey, relativeUrl);
            String authorizationValue =
                    String.format("semmtech-app-token appId=\"%s\", signature=\"%s\"",
                            applicationId,
                            signatureBase64);

            headers.set("Authorization", authorizationValue);

            return headers;
        } catch (URISyntaxException | GeneralSecurityException e) {
            log.error("Exception using laces token authentication/authorization.", e);
            throw new LacesTokenAuthorizationException(e.getMessage());
        }
    }

    @Override
    public AuthenticationMethodDto toDto() {
        return new SemmtechTokenAuthenticationDto(privateKey, applicationId);
    }

    private String getRelativeURL(String input) throws URISyntaxException {
        Escaper escaper = UrlEscapers.urlPathSegmentEscaper();
        URI url = new URI(input);
        String urlPath = url.getPath();
        String urlQuery = url.getQuery();

        return StringUtils.isEmpty(urlQuery) ? urlPath : urlPath + "?" + urlQuery;
    }

    private String calculateSignatureBase64(String privateKey, String relativeUrl) throws GeneralSecurityException {
        byte[] privateKeyBytes = Base64.getDecoder().decode(privateKey);
        KeyFactory keyFactory = KeyFactory.getInstance("RSA");
        PrivateKey key = keyFactory.generatePrivate(new PKCS8EncodedKeySpec(privateKeyBytes));
        Signature rsa = Signature.getInstance("SHA1withRSA");
        rsa.initSign(key);
        rsa.update(relativeUrl.getBytes());
        byte[] signature = rsa.sign();

        return Base64.getEncoder().encodeToString(signature);
    }
}
