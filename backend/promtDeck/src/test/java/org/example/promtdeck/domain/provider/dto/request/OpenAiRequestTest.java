package org.example.promtdeck.domain.provider.dto.request;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.example.promtdeck.domain.provider.entity.ProviderSetting;
import org.example.promtdeck.domain.provider.type.ProviderType;
import org.example.promtdeck.domain.user.entity.User;
import org.example.promtdeck.global.exception.CustomException;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class OpenAiRequestTest {

    private final ObjectMapper objectMapper = new ObjectMapper();

    @Test
    void createsOpenAiRequestFromCommonExecuteFields() throws Exception {
        ProviderSetting setting = setting("gpt-5.5");

        OpenAiRequest request = OpenAiRequest.from(setting, Map.of(
                "input", "Explain interfaces",
                "instructions", "Be concise",
                "temperature", 0.4,
                "top_p", 0.9,
                "max_output_tokens", 800
        ));

        assertThat(request.model()).isEqualTo("gpt-5.5");
        assertThat(request.input()).isEqualTo("Explain interfaces");
        assertThat(request.instructions()).isEqualTo("Be concise");
        assertThat(request.temperature()).isEqualTo(0.4);
        assertThat(request.topP()).isNull();
        assertThat(request.maxOutputTokens()).isEqualTo(800);

        String json = objectMapper.writeValueAsString(request);
        assertThat(json).doesNotContain("\"top_p\"");
        assertThat(json).contains("\"max_output_tokens\":800");
    }

    @Test
    void includesTopPForGpt4Models() throws Exception {
        ProviderSetting setting = setting("gpt-4o");

        OpenAiRequest request = OpenAiRequest.from(setting, Map.of(
                "input", "Explain interfaces",
                "top_p", 0.9
        ));

        assertThat(request.topP()).isEqualTo(0.9);

        String json = objectMapper.writeValueAsString(request);
        assertThat(json).contains("\"top_p\":0.9");
    }

    @Test
    void acceptsArrayInput() {
        ProviderSetting setting = setting("gpt-5.4-mini");
        List<Map<String, String>> input = List.of(Map.of("role", "user", "content", "hello"));

        OpenAiRequest request = OpenAiRequest.from(setting, Map.of("input", input));

        assertThat(request.input()).isEqualTo(input);
    }

    @Test
    void rejectsUnsupportedModel() {
        ProviderSetting setting = setting("gpt-4");

        assertThatThrownBy(() -> OpenAiRequest.from(setting, Map.of()))
                .isInstanceOf(CustomException.class);
    }

    @Test
    void rejectsInvalidOptionRanges() {
        ProviderSetting setting = setting("gpt-5.4-mini");
        ProviderSetting gpt4Setting = setting("gpt-4o");

        assertThatThrownBy(() -> OpenAiRequest.from(setting, Map.of("input", "hello", "temperature", 2.1)))
                .isInstanceOf(CustomException.class);
        assertThatThrownBy(() -> OpenAiRequest.from(gpt4Setting, Map.of("input", "hello", "top_p", 1.1)))
                .isInstanceOf(CustomException.class);
        assertThatThrownBy(() -> OpenAiRequest.from(setting, Map.of("input", "hello", "max_output_tokens", 0)))
                .isInstanceOf(CustomException.class);
    }

    private ProviderSetting setting(String model) {
        User user = User.createLocalUser("test@example.com", "password", "test");

        return ProviderSetting.create(
                ProviderType.OPENAI,
                "OpenAI",
                model,
                "https://api.openai.com/v1/responses",
                null,
                null,
                null,
                null,
                null,
                null,
                null,
                null,
                "output_text",
                user,
                null
        );
    }
}
