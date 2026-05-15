package org.example.promtdeck.domain.provider.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.example.promtdeck.domain.provider.dto.request.ProviderHttpRequest;
import org.example.promtdeck.domain.provider.entity.ProviderSetting;
import org.example.promtdeck.domain.provider.type.HttpMethodType;
import org.example.promtdeck.domain.provider.type.ProviderType;
import org.example.promtdeck.domain.user.entity.User;
import org.junit.jupiter.api.Test;

import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;

class ProviderTemplateRendererTest {

    private final ProviderTemplateRenderer renderer = new ProviderTemplateRenderer(new ObjectMapper());

    @Test
    void rendersCompanySpecificBodyTemplateAndQueryParams() {
        ProviderSetting setting = ProviderSetting.create(
                ProviderType.OPENAI,
                "A company OpenAI",
                "gpt-5.5",
                "https://api.example.com/responses",
                HttpMethodType.POST,
                null,
                null,
                null,
                "{\"X-Company\":\"{{company}}\"}",
                "{\"trace\":\"{{traceId}}\"}",
                "{\"model\":\"{{model}}\",\"input\":\"{{input}}\",\"temperature\":{{temperature}}}",
                null,
                "output_text",
                User.createLocalUser("test@example.com", "password", "test"),
                null
        );

        ProviderHttpRequest request = renderer.buildRequest(
                setting,
                "secret",
                Map.of(
                        "company", "A",
                        "traceId", "t-1",
                        "input", "Explain interfaces",
                        "temperature", 0.7
                ),
                Map.of("Content-Type", "application/json"),
                Map.of()
        );

        assertThat(request.endpoint()).isEqualTo("https://api.example.com/responses?trace=t-1");
        assertThat(request.headers()).containsEntry("Content-Type", "application/json");
        assertThat(request.headers()).containsEntry("Authorization", "Bearer secret");
        assertThat(request.headers()).containsEntry("X-Company", "A");
        assertThat(request.body()).isInstanceOf(Map.class);
        @SuppressWarnings("unchecked")
        Map<String, Object> body = (Map<String, Object>) request.body();
        assertThat(body)
                .containsEntry("model", "gpt-5.5")
                .containsEntry("input", "Explain interfaces")
                .containsEntry("temperature", 0.7);
    }
}
