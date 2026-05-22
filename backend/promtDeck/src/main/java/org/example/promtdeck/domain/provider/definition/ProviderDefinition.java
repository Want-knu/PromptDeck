package org.example.promtdeck.domain.provider.definition;

import org.example.promtdeck.domain.provider.dto.response.ProviderSettingOptionsResponse;
import org.example.promtdeck.domain.provider.type.ProviderType;

public interface ProviderDefinition {

    ProviderType type();

    ProviderSettingOptionsResponse.ProviderOption options();

    ResolvedProviderSetting resolve(ProviderSettingResolveCommand command);
}
