package org.example.promtdeck.domain.provider.dto.response;

import org.example.promtdeck.domain.provider.entity.ProviderSetting;
import org.example.promtdeck.domain.provider.type.AuthType;
import org.example.promtdeck.domain.provider.type.HttpMethodType;
import org.example.promtdeck.domain.provider.type.ProviderType;

public record ProviderSettingResponse(
        Long id,
        Long version,
        Long organizationId,
        ProviderType providerType,
        String displayName,
        String model,
        String endpoint,
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
    public static ProviderSettingResponse from(ProviderSetting setting) {
        return new ProviderSettingResponse(
                setting.getId(),
                setting.getVersion(),
                setting.getOrganization() == null ? null : setting.getOrganization().getId(),
                setting.getProviderType(),
                setting.getDisplayName(),
                setting.getModel(),
                setting.getEndpoint(),
                setting.getMethod(),
                setting.getAuthType(),
                setting.getAuthHeaderName(),
                setting.getAuthQueryParamName(),
                setting.getHeadersJson(),
                setting.getQueryParamsJson(),
                setting.getBodyTemplateJson(),
                setting.getOptionSchemaJson(),
                setting.getResponsePath()
        );
    }
}
