package org.example.promtdeck.domain.provider.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import org.example.promtdeck.domain.provider.type.ProviderType;

public record ProviderKeyUpdateRequest(
        @NotNull
        ProviderType providerType,

        @NotBlank
        String apiKey,

        @NotBlank
        String displayName
) {
}
