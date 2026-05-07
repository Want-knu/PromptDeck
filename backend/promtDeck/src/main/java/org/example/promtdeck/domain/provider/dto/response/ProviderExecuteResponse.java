package org.example.promtdeck.domain.provider.dto.response;

import org.example.promtdeck.domain.provider.type.ProviderType;

public record ProviderExecuteResponse(
        ProviderType providerType,
        String model,
        int statusCode,
        boolean success,
        String responseBody,
        String errorMessage
) {
}
