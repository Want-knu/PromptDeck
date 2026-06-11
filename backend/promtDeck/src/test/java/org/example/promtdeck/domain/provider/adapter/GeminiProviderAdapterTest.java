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

class GeminiProviderAdapterTest {

    private final GeminiProviderAdapter adapter = new GeminiProviderAdapter(
            new ProviderTemplateRenderer(new ObjectMapper())
    );

    @Test
    void nestsThinkingConfigUnderGenerationConfigForRestApi() {
        ProviderHttpRequest request = adapter.toHttpRequest(
                geminiSetting(),
                "secret",
                Map.of(
                        "prompt", "Explain interfaces",
                        "generationConfig.maxOutputTokens", 1024,
                        "thinkingConfig.thinkingBudget", -1
                )
        );

        assertThat(request.body()).isInstanceOf(Map.class);
        @SuppressWarnings("unchecked")
        Map<String, Object> body = (Map<String, Object>) request.body();
        assertThat(body).doesNotContainKey("thinkingConfig");
        assertThat(body).containsKey("generationConfig");

        @SuppressWarnings("unchecked")
        Map<String, Object> generationConfig = (Map<String, Object>) body.get("generationConfig");
        assertThat(generationConfig).containsEntry("maxOutputTokens", 1024);
        assertThat(generationConfig.get("thinkingConfig")).isEqualTo(Map.of("thinkingBudget", -1));
    }

    @Test
    void mapsInstructionsToSystemInstruction() {
        ProviderHttpRequest request = adapter.toHttpRequest(
                geminiSetting(),
                "secret",
                Map.of(
                        "prompt", "Explain interfaces",
                        "instructions", "Answer in Korean."
                )
        );

        assertThat(request.body()).isInstanceOf(Map.class);
        @SuppressWarnings("unchecked")
        Map<String, Object> body = (Map<String, Object>) request.body();
        assertThat(body).containsKey("systemInstruction");

        @SuppressWarnings("unchecked")
        Map<String, Object> systemInstruction = (Map<String, Object>) body.get("systemInstruction");
        assertThat(systemInstruction).isEqualTo(Map.of(
                "parts", java.util.List.of(Map.of("text", "Answer in Korean."))
        ));
    }

    private ProviderSetting geminiSetting() {
        return ProviderSetting.create(
                ProviderType.GEMINI,
                "Gemini",
                "gemini-2.5-flash",
                "https://generativelanguage.googleapis.com/v1beta/models/{{model}}:generateContent",
                HttpMethodType.POST,
                null,
                "key",
                null,
                null,
                null,
                null,
                null,
                "candidates[0].content.parts[0].text",
                User.createLocalUser("test@example.com", "password", "test"),
                null
        );
    }
}
