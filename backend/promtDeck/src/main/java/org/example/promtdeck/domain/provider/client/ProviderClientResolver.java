package org.example.promtdeck.domain.provider.client;

import org.example.promtdeck.domain.provider.type.ProviderType;
import org.example.promtdeck.global.common.ErrorCode;
import org.example.promtdeck.global.exception.CustomException;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Component
public class ProviderClientResolver {

    private final Map<ProviderType, ProviderClient> clients;

    public ProviderClientResolver(List<ProviderClient> clients) {
        this.clients = clients.stream()
                .collect(Collectors.toMap(ProviderClient::supports, client -> client));
    }

    public ProviderClient getClient(ProviderType providerType) {
        ProviderClient client = clients.get(providerType);

        if (client == null) {
            throw new CustomException(ErrorCode.UNSUPPORTED_PROVIDER);
        }

        return client;
    }
}
