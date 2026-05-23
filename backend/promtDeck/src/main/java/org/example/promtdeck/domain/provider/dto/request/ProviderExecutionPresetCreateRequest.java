package org.example.promtdeck.domain.provider.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.util.Map;

public record ProviderExecutionPresetCreateRequest(
        @NotBlank
        String displayName,

        @NotNull
        Long providerSettingId,

        Long providerKeyId,

        String prompt,

        String instructions,

        Map<String, Object> options,

        Map<String, Object> variables
) {
}
