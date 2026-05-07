package org.example.promtdeck.domain.provider.dto.response;

import org.example.promtdeck.domain.provider.entity.ProviderKey;
import org.example.promtdeck.domain.provider.type.ProviderType;

public record ProviderKeyResponse(
        Long id,
        Long version,
        ProviderType providerType,
        String maskedApiKey,
        String displayName
) {
    public static ProviderKeyResponse from(ProviderKey providerKey) {
        return new ProviderKeyResponse(
                providerKey.getId(),
                providerKey.getVersion(),
                providerKey.getProviderType(),
                providerKey.getMaskedApiKey(),
                providerKey.getDisplayName()
        );
    }
}
