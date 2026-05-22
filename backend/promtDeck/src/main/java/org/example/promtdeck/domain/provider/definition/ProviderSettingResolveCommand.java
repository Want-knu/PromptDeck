package org.example.promtdeck.domain.provider.definition;

import org.example.promtdeck.domain.provider.type.AuthType;
import org.example.promtdeck.domain.provider.type.HttpMethodType;
import org.example.promtdeck.domain.provider.type.ProviderType;

public record ProviderSettingResolveCommand(
        ProviderType providerType,
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
}
