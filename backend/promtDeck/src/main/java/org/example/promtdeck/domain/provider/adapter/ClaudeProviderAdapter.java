package org.example.promtdeck.domain.provider.adapter;

import lombok.RequiredArgsConstructor;
import org.example.promtdeck.domain.provider.dto.request.ProviderHttpRequest;
import org.example.promtdeck.domain.provider.entity.ProviderSetting;
import org.example.promtdeck.domain.provider.service.ProviderTemplateRenderer;
import org.example.promtdeck.domain.provider.type.ProviderType;
import org.springframework.stereotype.Component;

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

        Map<String, Object> body = Map.of(
                "model", setting.getModel(),
                "max_tokens", 1024,
                "messages", List.of(
                        Map.of(
                                "role", "user",
                                "content", String.valueOf(variables.getOrDefault("prompt", ""))
                        )
                )
        );

        return templateRenderer.buildRequest(setting, apiKey, variables, headers, body);
    }
}
