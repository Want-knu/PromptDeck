package org.example.promtdeck.domain.provider.client;

import org.example.promtdeck.domain.provider.type.ProviderType;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;

@Component
public class OpenAiProviderClient extends AbstractRestProviderClient {

    public OpenAiProviderClient(RestClient restClient) {
        super(restClient);
    }

    @Override
    public ProviderType supports() {
        return ProviderType.OPENAI;
    }
}
