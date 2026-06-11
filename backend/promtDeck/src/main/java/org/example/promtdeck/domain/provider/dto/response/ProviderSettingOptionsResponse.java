package org.example.promtdeck.domain.provider.dto.response;

import org.example.promtdeck.domain.provider.type.AuthType;
import org.example.promtdeck.domain.provider.type.HttpMethodType;
import org.example.promtdeck.domain.provider.type.ProviderType;

import java.util.List;
import java.util.Map;

public record ProviderSettingOptionsResponse(
        List<ProviderType> providerTypes,
        Map<ProviderType, ProviderOption> providers
) {
    public record ProviderOption(
            List<String> models,
            String defaultModel,
            List<EndpointOption> endpoints,
            String defaultEndpoint,
            List<HttpMethodType> methods,
            HttpMethodType defaultMethod,
            List<AuthType> authTypes,
            AuthType defaultAuthType,
            String defaultAuthHeaderName,
            String defaultAuthQueryParamName,
            List<String> responsePaths,
            String defaultResponsePath,
            List<NamedJsonOption> bodyTemplates,
            List<NamedJsonOption> optionSchemas,
            Map<String, ModelOption> modelOptions,
            boolean custom
    ) {
    }

    public record EndpointOption(
            String label,
            String value,
            HttpMethodType method,
            AuthType authType,
            String authHeaderName,
            String authQueryParamName,
            String headersJson,
            String queryParamsJson,
            String bodyTemplateJson,
            String optionSchemaJson,
            String responsePath
    ) {
    }

    public record NamedJsonOption(
            String label,
            String value
    ) {
    }

    public record ModelOption(
            String optionSchemaJson,
            String responsePath
    ) {
    }
}
