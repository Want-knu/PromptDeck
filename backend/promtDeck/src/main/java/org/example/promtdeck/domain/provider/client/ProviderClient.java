package org.example.promtdeck.domain.provider.client;

import org.example.promtdeck.domain.provider.dto.request.ProviderHttpRequest;
import org.example.promtdeck.domain.provider.dto.response.ProviderExecuteResponse;
import org.example.promtdeck.domain.provider.entity.ProviderSetting;
import org.example.promtdeck.domain.provider.type.ProviderType;

public interface ProviderClient {

    ProviderType supports();

    ProviderExecuteResponse execute(
            ProviderSetting setting,
            ProviderHttpRequest request
    );
}
