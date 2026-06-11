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
public class ClaudeProviderAdapter implements ProviderAdapter {

    private final ProviderTemplateRenderer templateRenderer;

    @Override
    public ProviderType supports() {
        return ProviderType.CLAUDE;
    }

    @Override
    public ProviderHttpRequest toHttpRequest(ProviderSetting setting, String apiKey, Map<String, Object> variables) {
        Map<String, String> headers = Map.of(
                "anthropic-version", "2023-06-01",
                "Content-Type", "application/json"
        );

        Map<String, Object> body = new LinkedHashMap<>();
        body.put("model", setting.getModel());
        body.put("max_tokens", variables.getOrDefault("max_tokens", 1024));
        body.put("messages", List.of(
                Map.of(
                        "role", "user",
                        "content", String.valueOf(variables.getOrDefault("prompt", ""))
                )
        ));
        putIfPresent(body, variables, "system");
        putDotValue(body, variables, "instructions", "system");
        putIfPresent(body, variables, "temperature");
        putIfPresent(body, variables, "stop_sequences");

        Map<String, Object> thinking = new LinkedHashMap<>();
        putDotValue(thinking, variables, "thinking.type", "type");
        putDotValue(thinking, variables, "thinking.display", "display");
        putDotValue(thinking, variables, "thinking.budget_tokens", "budget_tokens");

        if (!thinking.isEmpty()) {
            body.put("thinking", thinking);
        }

        Map<String, Object> outputConfig = new LinkedHashMap<>();
        putDotValue(outputConfig, variables, "output_config.effort", "effort");

        if (!outputConfig.isEmpty()) {
            body.put("output_config", outputConfig);
        }

        return templateRenderer.buildRequest(setting, apiKey, variables, headers, body);
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
}
