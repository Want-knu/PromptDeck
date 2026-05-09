package org.example.promtdeck.domain.provider.dto.request;

import jakarta.validation.constraints.NotNull;

import java.util.Map;

public record ProviderExecuteRequest(
        @NotNull
        Long providerKeyId,

        @NotNull
        Long providerSettingId,

        String prompt,

        Object input,

        String instructions,

        Map<String, Object> options,

        Map<String, Object> variables
) {
}
