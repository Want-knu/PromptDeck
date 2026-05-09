package org.example.promtdeck.domain.provider.service;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.example.promtdeck.domain.provider.dto.request.ProviderHttpRequest;
import org.example.promtdeck.domain.provider.entity.ProviderSetting;
import org.example.promtdeck.domain.provider.type.AuthType;
import org.example.promtdeck.domain.provider.type.HttpMethodType;
import org.example.promtdeck.domain.provider.type.ProviderType;
import org.example.promtdeck.global.common.ErrorCode;
import org.example.promtdeck.global.exception.CustomException;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.util.UriComponentsBuilder;

import java.util.LinkedHashMap;
import java.util.Map;

@Component
@RequiredArgsConstructor
public class ProviderTemplateRenderer {

    private final ObjectMapper objectMapper;

    public ProviderHttpRequest buildRequest(
            ProviderSetting setting,
            String apiKey,
            Map<String, Object> variables,
            Map<String, String> defaultHeaders,
            Object defaultBody
    ) {
        Map<String, Object> preparedVariables = prepareVariables(setting, variables);
        Map<String, String> headers = new LinkedHashMap<>(defaultHeaders);
        headers.putAll(renderStringMap(setting.getHeadersJson(), preparedVariables));

        Map<String, String> queryParams = renderStringMap(setting.getQueryParamsJson(), preparedVariables);
        AuthConfig authConfig = resolveAuthConfig(setting);
        applyAuth(authConfig, apiKey, headers, queryParams);

        Object body = renderBody(setting.getBodyTemplateJson(), preparedVariables, defaultBody);
        String endpoint = appendQueryParams(setting.getEndpoint(), queryParams);

        return new ProviderHttpRequest(resolveMethod(setting), endpoint, headers, body);
    }

    private Map<String, Object> prepareVariables(ProviderSetting setting, Map<String, Object> variables) {
        Map<String, Object> prepared = new LinkedHashMap<>();

        if (variables != null) {
            prepared.putAll(variables);
        }

        prepared.putIfAbsent("model", setting.getModel());
        prepared.putIfAbsent("prompt", "");

        return prepared;
    }

    private Map<String, String> renderStringMap(String json, Map<String, Object> variables) {
        if (!StringUtils.hasText(json)) {
            return new LinkedHashMap<>();
        }

        try {
            String rendered = renderTemplate(json, variables);
            Map<String, Object> rawMap = objectMapper.readValue(rendered, new TypeReference<LinkedHashMap<String, Object>>() {
            });
            Map<String, String> result = new LinkedHashMap<>();
            rawMap.forEach((key, value) -> result.put(key, String.valueOf(value)));
            return result;
        } catch (Exception e) {
            throw new CustomException(ErrorCode.INVALID_PROVIDER_TEMPLATE);
        }
    }

    private Object renderBody(String bodyTemplateJson, Map<String, Object> variables, Object defaultBody) {
        if (!StringUtils.hasText(bodyTemplateJson)) {
            return defaultBody;
        }

        try {
            return objectMapper.readValue(renderTemplate(bodyTemplateJson, variables), Object.class);
        } catch (Exception e) {
            throw new CustomException(ErrorCode.INVALID_PROVIDER_TEMPLATE);
        }
    }

    private String renderTemplate(String template, Map<String, Object> variables) throws Exception {
        String rendered = template;

        for (Map.Entry<String, Object> entry : variables.entrySet()) {
            String placeholder = "{{" + entry.getKey() + "}}";
            rendered = rendered.replace(placeholder, renderValue(entry.getValue()));
        }

        return rendered;
    }

    private String renderValue(Object value) throws Exception {
        if (value == null) {
            return "null";
        }

        if (value instanceof String stringValue) {
            String quoted = objectMapper.writeValueAsString(stringValue);
            return quoted.substring(1, quoted.length() - 1);
        }

        return objectMapper.writeValueAsString(value);
    }

    private void applyAuth(
            AuthConfig authConfig,
            String apiKey,
            Map<String, String> headers,
            Map<String, String> queryParams
    ) {
        if (authConfig.authType() == AuthType.NONE) {
            return;
        }

        if (authConfig.authType() == AuthType.QUERY_PARAM) {
            queryParams.put(authConfig.name(), apiKey);
            return;
        }

        if (authConfig.authType() == AuthType.HEADER) {
            headers.put(authConfig.name(), apiKey);
            return;
        }

        headers.put(authConfig.name(), "Bearer " + apiKey);
    }

    private String appendQueryParams(String endpoint, Map<String, String> queryParams) {
        if (queryParams.isEmpty()) {
            return endpoint;
        }

        UriComponentsBuilder builder = UriComponentsBuilder.fromUriString(endpoint);
        queryParams.forEach(builder::queryParam);
        return builder.build().toUriString();
    }

    private HttpMethodType resolveMethod(ProviderSetting setting) {
        if (setting.getMethod() != null) {
            return setting.getMethod();
        }

        return HttpMethodType.POST;
    }

    private AuthConfig resolveAuthConfig(ProviderSetting setting) {
        AuthType authType = setting.getAuthType();
        String name;

        if (authType == null) {
            authType = defaultAuthType(setting.getProviderType());
        }

        if (authType == AuthType.QUERY_PARAM) {
            name = StringUtils.hasText(setting.getAuthQueryParamName())
                    ? setting.getAuthQueryParamName()
                    : defaultAuthName(setting.getProviderType(), authType);
        } else {
            name = StringUtils.hasText(setting.getAuthHeaderName())
                    ? setting.getAuthHeaderName()
                    : defaultAuthName(setting.getProviderType(), authType);
        }

        return new AuthConfig(authType, name);
    }

    private AuthType defaultAuthType(ProviderType providerType) {
        return switch (providerType) {
            case GEMINI -> AuthType.QUERY_PARAM;
            case CLAUDE -> AuthType.HEADER;
            case OPENAI, CUSTOM -> AuthType.BEARER;
        };
    }

    private String defaultAuthName(ProviderType providerType, AuthType authType) {
        if (authType == AuthType.QUERY_PARAM) {
            return "key";
        }

        if (providerType == ProviderType.CLAUDE && authType == AuthType.HEADER) {
            return "x-api-key";
        }

        return "Authorization";
    }

    private record AuthConfig(
            AuthType authType,
            String name
    ) {
    }
}
