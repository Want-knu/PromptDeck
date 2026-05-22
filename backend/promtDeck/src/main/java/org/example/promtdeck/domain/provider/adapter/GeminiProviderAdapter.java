package org.example.promtdeck.domain.provider.adapter;

import lombok.RequiredArgsConstructor;
import org.example.promtdeck.domain.provider.dto.request.ProviderHttpRequest;
import org.example.promtdeck.domain.provider.entity.ProviderSetting;
import org.example.promtdeck.domain.provider.service.ProviderTemplateRenderer;
import org.example.promtdeck.domain.provider.type.ProviderType;
import org.springframework.stereotype.Component;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@Component
@RequiredArgsConstructor
public class GeminiProviderAdapter implements ProviderAdapter {

    private final ProviderTemplateRenderer templateRenderer;

    @Override
    public ProviderType supports() {
        return ProviderType.GEMINI;
    }

    @Override
    public ProviderHttpRequest toHttpRequest(ProviderSetting setting, String apiKey, Map<String, Object> variables) {
        Map<String, String> headers = Map.of("Content-Type", "application/json");
        Map<String, Object> body = new LinkedHashMap<>();
        body.put("contents", List.of(
                Map.of("parts", List.of(Map.of("text", String.valueOf(variables.getOrDefault("prompt", "")))))
        ));
        if (hasText(variables.get("instructions"))) {
            body.put("systemInstruction", Map.of(
                    "parts", List.of(Map.of("text", String.valueOf(variables.get("instructions"))))
            ));
        }

        Map<String, Object> generationConfig = new LinkedHashMap<>();
        putIfPresent(generationConfig, variables, "temperature");
        putDotValue(generationConfig, variables, "generationConfig.topP", "topP");
        putDotValue(generationConfig, variables, "generationConfig.topK", "topK");
        putDotValue(generationConfig, variables, "generationConfig.maxOutputTokens", "maxOutputTokens");
        putDotValue(generationConfig, variables, "generationConfig.responseMimeType", "responseMimeType");

        if (!generationConfig.isEmpty()) {
            body.put("generationConfig", generationConfig);
        }

        Map<String, Object> thinkingConfig = new LinkedHashMap<>();
        putDotValue(thinkingConfig, variables, "thinkingConfig.thinkingLevel", "thinkingLevel");
        putDotValue(thinkingConfig, variables, "thinkingConfig.thinkingBudget", "thinkingBudget");
        putDotValue(thinkingConfig, variables, "thinkingConfig.includeThoughts", "includeThoughts");

        if (!thinkingConfig.isEmpty()) {
            generationConfig.put("thinkingConfig", thinkingConfig);
            body.put("generationConfig", generationConfig);
        }

        ProviderHttpRequest request = templateRenderer.buildRequest(setting, apiKey, variables, headers, body);
        return new ProviderHttpRequest(
                request.method(),
                normalizeEndpoint(request.endpoint()),
                request.headers(),
                request.body()
        );
    }

    private void putIfPresent(Map<String, Object> body, Map<String, Object> variables, String key) {
        if (variables.containsKey(key) && variables.get(key) != null) {
            body.put(key, variables.get(key));
        }
    }

    private void putDotValue(Map<String, Object> body, Map<String, Object> variables, String sourceKey, String targetKey) {
        if (variables.containsKey(sourceKey) && variables.get(sourceKey) != null) {
            body.put(targetKey, variables.get(sourceKey));
        }
    }

    private String normalizeEndpoint(String endpoint) {
        return endpoint.replace("/models/gemini-3.1-pro:", "/models/gemini-3.1-pro-preview:");
    }

    private boolean hasText(Object value) {
        return value != null && !String.valueOf(value).isBlank();
    }
}
