package org.example.promtdeck.domain.provider.adapter;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.example.promtdeck.domain.provider.dto.request.ProviderHttpRequest;
import org.example.promtdeck.domain.provider.entity.ProviderSetting;
import org.example.promtdeck.domain.provider.service.ProviderTemplateRenderer;
import org.example.promtdeck.domain.provider.type.HttpMethodType;
import org.example.promtdeck.domain.provider.type.ProviderType;
import org.example.promtdeck.domain.user.entity.User;
import org.junit.jupiter.api.Test;

import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;

class ClaudeProviderAdapterTest {

    private final ClaudeProviderAdapter adapter = new ClaudeProviderAdapter(
            new ProviderTemplateRenderer(new ObjectMapper())
    );

    @Test
    void mapsInstructionsToSystem() {
        ProviderHttpRequest request = adapter.toHttpRequest(
                ProviderSetting.create(
                        ProviderType.CLAUDE,
                        "Claude",
                        "claude-3-5-sonnet-latest",
                        "https://api.anthropic.com/v1/messages",
                        HttpMethodType.POST,
                        null,
                        "x-api-key",
                        null,
                        null,
                        null,
                        null,
                        null,
                        "content[0].text",
                        User.createLocalUser("test@example.com", "password", "test"),
                        null
                ),
                "secret",
                Map.of(
                        "prompt", "Explain interfaces",
                        "instructions", "Answer in Korean."
                )
        );

        assertThat(request.body()).isInstanceOf(Map.class);
        @SuppressWarnings("unchecked")
        Map<String, Object> body = (Map<String, Object>) request.body();
        assertThat(body).containsEntry("system", "Answer in Korean.");
    }
}
