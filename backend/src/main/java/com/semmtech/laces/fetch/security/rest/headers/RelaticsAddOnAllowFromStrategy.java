package com.semmtech.laces.fetch.security.rest.headers;

import com.semmtech.laces.fetch.configuration.entities.EnvironmentEntity;
import com.semmtech.laces.fetch.configuration.entities.WorkspaceEntity;
import com.semmtech.laces.fetch.configuration.service.AddOnConfigurationService;
import com.semmtech.laces.fetch.configuration.service.RelaticsService;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.http.HttpHeaders;
import org.springframework.security.web.header.writers.frameoptions.AllowFromStrategy;
import org.springframework.stereotype.Component;

import javax.servlet.http.HttpServletRequest;
import java.util.Optional;
import java.util.function.Function;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


/**
 * This AllowFromStrategy makes sure the page cannot be included in an iframe on a
 * page that doesn't come from the configured relatics environment.
 */
@Slf4j
@Component
@AllArgsConstructor
public class RelaticsAddOnAllowFromStrategy implements AllowFromStrategy {
    public static final String DENY = "DENY";

    private final AddOnConfigurationService addOnConfigurationService;
    private final RelaticsService relaticsService;

    @Override
    public String getAllowFromValue(HttpServletRequest httpServletRequest) {
        Optional<String> refererHeaderValue =
                Optional.ofNullable(httpServletRequest.getHeader(HttpHeaders.REFERER));
        if (refererHeaderValue.map(headerValue -> StringUtils.contains(headerValue, "localhost")).orElse(false)) {
            return null;
        } else if (httpServletRequest.getRequestURI().contains("add-on/index.html")) {
            String configurationId = httpServletRequest.getParameter("configurationId");

            return addOnConfigurationService.get(configurationId)
                            .flatMap(relaticsService::getWorkspaceForConfiguration)
                            .map(workspace -> buildFrameOptionsHeader(workspace, refererHeaderValue))
                            .orElse(DENY);
        } else if (httpServletRequest.getRequestURI().contains(".js")) {
            return getRefererDomain(refererHeaderValue);
        }
        return DENY;
    }

    private String buildFrameOptionsHeader(
            WorkspaceEntity workspace,
            Optional<String> refererHeaderValue) {

        String refererBaseUrl = getRefererDomain(refererHeaderValue);
        EnvironmentEntity environment = relaticsService.getEnvironmentForWorkspace(workspace);
        String header = DENY;
        if (StringUtils.startsWith(environment.getServiceUrl(), refererBaseUrl) &&
                StringUtils.equals(environment.getEnvironmentId(), getUUID(refererHeaderValue, UUIDParameter.EID)) &&
                StringUtils.equals(workspace.getWorkspaceId(), getUUID(refererHeaderValue, UUIDParameter.WID))) {
            header = refererBaseUrl;
        }

        return header;
    }

    private String getRefererDomain(Optional<String> refererHeaderValue) {
        return getUrlPart(
                refererHeaderValue,
                "referer",
                refererUrl -> refererUrl.substring(0, refererUrl.indexOf("/", "https://".length())));
    }

    private String getUUID(Optional<String> refererHeaderValue, UUIDParameter parameter) {
        return getUrlPart(
                refererHeaderValue,
                parameter.name,
                refererUrl -> extractUUIDUrlParameter(refererUrl, parameter));
    }

    private String getUrlPart(Optional<String> refererHeaderValue, String urlPartName, Function<String, String> valueExtractor) {
        return refererHeaderValue
                .map(valueExtractor)
                .orElse("No valid " + urlPartName);
    }


    /**
     * This method extracts a UUID parameter value from the query string of the referer url.
     * @param parameter an enum containing the parameter name to search for and a regex to find it
     * @param fullUrl the url to look for parameters in
     *
     * @return the extracted UUID
     */
    private String extractUUIDUrlParameter(String fullUrl, UUIDParameter parameter) {
        String result = DENY;
        Matcher matcher = parameter.pattern.matcher(fullUrl);
        if (matcher.matches()) {
            result = matcher.group(parameter.name);
        }
        return result;
    }

    /**
     * The regular expression searches for either a ? or a & : .*(%26|\?) ,
     * followed by the parameter name and an equals sign: + parameterName + "=,
     * and a named group with the UUID of 36 characters: (?<" + parameterName + ">.{36}) ,
     * followed by
     *      either an ampersand and one or more parameters: (%26.*)*
     *      or the end of the url
     *
     */
    private enum UUIDParameter {
        EID("EID"),
        WID("WID");

        UUIDParameter(String name) {
            this.name = name;
            this.pattern = Pattern.compile(".*(&|%26|\\?)"+name+"=(?<"+name+">.{36})(&|%26)?.*");
        }

        private String name;
        private Pattern pattern;
    }
}
