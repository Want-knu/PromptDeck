package org.example.promtdeck.domain.provider.adapter;

import lombok.RequiredArgsConstructor;
import org.example.promtdeck.domain.provider.dto.request.OpenAiRequest;
import org.example.promtdeck.domain.provider.dto.request.ProviderHttpRequest;
import org.example.promtdeck.domain.provider.entity.ProviderSetting;
import org.example.promtdeck.domain.provider.service.ProviderTemplateRenderer;
import org.example.promtdeck.domain.provider.type.ProviderType;
import org.springframework.stereotype.Component;

import java.util.LinkedHashMap;
import java.util.Map;

@Component
@RequiredArgsConstructor
public class OpenAiProviderAdapter implements ProviderAdapter {

    private final ProviderTemplateRenderer templateRenderer;

    @Override
    public ProviderType supports() {
        return ProviderType.OPENAI;
    }

    @Override
    public ProviderHttpRequest toHttpRequest(ProviderSetting setting, String apiKey, Map<String, Object> variables) {
        Map<String, String> headers = Map.of(
                "Content-Type", "application/json"
        );

        OpenAiRequest body = OpenAiRequest.from(setting, variables);
        Map<String, Object> templateVariables = resolveTemplateVariables(variables, body);

        return templateRenderer.buildRequest(setting, apiKey, templateVariables, headers, body);
    }

    private Map<String, Object> resolveTemplateVariables(Map<String, Object> variables, OpenAiRequest body) {
        Map<String, Object> templateVariables = new LinkedHashMap<>(variables);
        templateVariables.putIfAbsent("input", body.input());

        if (body.instructions() != null) {
            templateVariables.putIfAbsent("instructions", body.instructions());
        }

        templateVariables.putIfAbsent("temperature", body.temperature());
        templateVariables.putIfAbsent("top_p", body.topP());

        if (body.maxOutputTokens() != null) {
            templateVariables.putIfAbsent("max_output_tokens", body.maxOutputTokens());
        }

        if (body.presencePenalty() != null) {
            templateVariables.putIfAbsent("presence_penalty", body.presencePenalty());
        }

        if (body.frequencyPenalty() != null) {
            templateVariables.putIfAbsent("frequency_penalty", body.frequencyPenalty());
        }

        if (body.reasoning() != null) {
            templateVariables.putIfAbsent("reasoning", body.reasoning());
        }

        if (body.stop() != null) {
            templateVariables.putIfAbsent("stop", body.stop());
        }

        return templateVariables;
    }
}
