package org.example.promtdeck.domain.provider.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import org.example.promtdeck.domain.provider.type.AuthType;
import org.example.promtdeck.domain.provider.type.HttpMethodType;
import org.example.promtdeck.domain.provider.type.ProviderType;

public record ProviderSettingCreateRequest(
        Long organizationId,

        @NotNull
        ProviderType providerType,

        @NotBlank
        String displayName,

        @NotBlank
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
