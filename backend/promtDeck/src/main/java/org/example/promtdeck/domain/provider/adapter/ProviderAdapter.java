package org.example.promtdeck.domain.provider.adapter;

import org.example.promtdeck.domain.provider.dto.request.ProviderHttpRequest;
import org.example.promtdeck.domain.provider.entity.ProviderSetting;
import org.example.promtdeck.domain.provider.type.ProviderType;

import java.util.Map;

public interface ProviderAdapter {
    ProviderType supports();

    ProviderHttpRequest toHttpRequest(
            ProviderSetting setting,
            String apiKey,
            Map<String, Object> variables
    );
}
