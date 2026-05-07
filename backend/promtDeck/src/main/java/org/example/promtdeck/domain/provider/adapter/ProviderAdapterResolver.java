package org.example.promtdeck.domain.provider.adapter;

import org.example.promtdeck.domain.provider.type.ProviderType;
import org.example.promtdeck.global.common.ErrorCode;
import org.example.promtdeck.global.exception.CustomException;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Component
public class ProviderAdapterResolver {

    private final Map<ProviderType, ProviderAdapter> adapters;

    public ProviderAdapterResolver(List<ProviderAdapter> adapters) {
        this.adapters = adapters.stream()
                .collect(Collectors.toMap(ProviderAdapter::supports, adapter -> adapter));
    }

    public ProviderAdapter getAdapter(ProviderType providerType) {
        ProviderAdapter adapter = adapters.get(providerType);

        if (adapter == null) {
            throw new CustomException(ErrorCode.UNSUPPORTED_PROVIDER);
        }

        return adapter;
    }
}
