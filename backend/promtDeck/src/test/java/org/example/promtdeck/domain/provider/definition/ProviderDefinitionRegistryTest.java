package org.example.promtdeck.domain.provider.definition;

import org.example.promtdeck.domain.provider.type.AuthType;
import org.example.promtdeck.domain.provider.type.HttpMethodType;
import org.example.promtdeck.domain.provider.type.ProviderType;
import org.example.promtdeck.global.exception.CustomException;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class ProviderDefinitionRegistryTest {

    private final ProviderDefinitionRegistry registry = new ProviderDefinitionRegistry(
            java.util.List.of(
                    new OpenAiProviderDefinition(),
                    new GeminiProviderDefinition(),
                    new ClaudeProviderDefinition(),
                    new CustomProviderDefinition()
            )
    );

    @Test
    void officialProviderIgnoresClientAuthAndEndpointValues() {
        ResolvedProviderSetting resolved = registry.resolve(command(
                ProviderType.OPENAI,
                "gpt-5.5",
                "https://wrong.example.com",
                HttpMethodType.GET,
                AuthType.QUERY_PARAM,
                null,
                "api_key",
                "{\"X-Test\":\"1\"}",
                "{\"debug\":\"true\"}",
                "{\"prompt\":\"{{prompt}}\"}",
                "{\"custom\":true}",
                "custom.path"
        ));

        assertThat(resolved.endpoint()).isEqualTo("https://api.openai.com/v1/responses");
        assertThat(resolved.method()).isEqualTo(HttpMethodType.POST);
        assertThat(resolved.authType()).isEqualTo(AuthType.BEARER);
        assertThat(resolved.authHeaderName()).isEqualTo("Authorization");
        assertThat(resolved.authQueryParamName()).isNull();
        assertThat(resolved.headersJson()).isNull();
        assertThat(resolved.queryParamsJson()).isNull();
        assertThat(resolved.bodyTemplateJson()).isNull();
        assertThat(resolved.responsePath()).isEqualTo("output_text");
    }

    @Test
    void geminiEndpointIsBuiltFromModel() {
        ResolvedProviderSetting resolved = registry.resolve(command(
                ProviderType.GEMINI,
                "gemini-2.5-flash",
                null,
                null,
                null,
                null,
                null,
                null,
                null,
                null,
                null,
                null
        ));

        assertThat(resolved.endpoint()).isEqualTo("https://generativelanguage.googleapis.com/v1beta/models/gemini-2.5-flash:generateContent");
        assertThat(resolved.authType()).isEqualTo(AuthType.QUERY_PARAM);
        assertThat(resolved.authQueryParamName()).isEqualTo("key");
    }

    @Test
    void customProviderKeepsClientConfig() {
        ResolvedProviderSetting resolved = registry.resolve(command(
                ProviderType.CUSTOM,
                "my-model",
                "https://api.example.com/run",
                HttpMethodType.PATCH,
                AuthType.HEADER,
                "X-API-Key",
                null,
                "{\"X-Test\":\"1\"}",
                "{\"trace\":\"1\"}",
                "{\"prompt\":\"{{prompt}}\"}",
                "{\"temperature\":{\"type\":\"number\"}}",
                "data.text"
        ));

        assertThat(resolved.endpoint()).isEqualTo("https://api.example.com/run");
        assertThat(resolved.method()).isEqualTo(HttpMethodType.PATCH);
        assertThat(resolved.authType()).isEqualTo(AuthType.HEADER);
        assertThat(resolved.authHeaderName()).isEqualTo("X-API-Key");
        assertThat(resolved.headersJson()).isEqualTo("{\"X-Test\":\"1\"}");
        assertThat(resolved.queryParamsJson()).isEqualTo("{\"trace\":\"1\"}");
        assertThat(resolved.bodyTemplateJson()).isEqualTo("{\"prompt\":\"{{prompt}}\"}");
        assertThat(resolved.optionSchemaJson()).isEqualTo("{\"temperature\":{\"type\":\"number\"}}");
        assertThat(resolved.responsePath()).isEqualTo("data.text");
    }

    @Test
    void customProviderRequiresEndpointAndAuthNamesWhenNeeded() {
        assertThatThrownBy(() -> registry.resolve(command(
                ProviderType.CUSTOM,
                "my-model",
                "",
                HttpMethodType.POST,
                AuthType.NONE,
                null,
                null,
                null,
                null,
                null,
                null,
                null
        ))).isInstanceOf(CustomException.class);

        assertThatThrownBy(() -> registry.resolve(command(
                ProviderType.CUSTOM,
                "my-model",
                "https://api.example.com/run",
                HttpMethodType.POST,
                AuthType.HEADER,
                "",
                null,
                null,
                null,
                null,
                null,
                null
        ))).isInstanceOf(CustomException.class);

        assertThatThrownBy(() -> registry.resolve(command(
                ProviderType.CUSTOM,
                "my-model",
                "https://api.example.com/run",
                HttpMethodType.POST,
                AuthType.QUERY_PARAM,
                null,
                "",
                null,
                null,
                null,
                null,
                null
        ))).isInstanceOf(CustomException.class);
    }

    @Test
    void customProviderRejectsLocalAndPrivateEndpoints() {
        assertThatThrownBy(() -> registry.resolve(command(
                ProviderType.CUSTOM,
                "my-model",
                "http://localhost:8080/run",
                HttpMethodType.POST,
                AuthType.NONE,
                null,
                null,
                null,
                null,
                null,
                null,
                null
        ))).isInstanceOf(CustomException.class);

        assertThatThrownBy(() -> registry.resolve(command(
                ProviderType.CUSTOM,
                "my-model",
                "http://10.0.0.5/run",
                HttpMethodType.POST,
                AuthType.NONE,
                null,
                null,
                null,
                null,
                null,
                null,
                null
        ))).isInstanceOf(CustomException.class);
    }

    private ProviderSettingResolveCommand command(
            ProviderType providerType,
            String model,
            String endpoint,
            HttpMethodType method,
            AuthType authType,
            String authHeaderName,
            String authQueryParamName,
            String headersJson,
            String queryParamsJson,
            String bodyTemplateJson,
            String optionSchemaJson,
            String responsePath
    ) {
        return new ProviderSettingResolveCommand(
                providerType,
                model,
                endpoint,
                method,
                authType,
                authHeaderName,
                authQueryParamName,
                headersJson,
                queryParamsJson,
                bodyTemplateJson,
                optionSchemaJson,
                responsePath
        );
    }
}
