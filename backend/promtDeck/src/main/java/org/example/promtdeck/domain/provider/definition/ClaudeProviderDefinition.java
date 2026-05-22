package org.example.promtdeck.domain.provider.definition;

import org.example.promtdeck.domain.provider.type.AuthType;
import org.example.promtdeck.domain.provider.type.ProviderType;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class ClaudeProviderDefinition extends AbstractOfficialProviderDefinition {

    private static final String ENDPOINT = "https://api.anthropic.com/v1/messages";
    private static final String RESPONSE_PATH = "content[0].text";
    private static final String VERSION_HEADERS_JSON = """
            {"anthropic-version":"2023-06-01"}
            """.trim();
    private static final String OPUS_47_OPTION_SCHEMA_JSON = """
            {
              "max_tokens": {"type":"integer","default":1024,"minimum":1},
              "thinking.display": {"type":"string","default":"omitted","enum":["summarized","omitted"]},
              "output_config.effort": {"type":"string","default":"medium","enum":["low","medium","high"]},
              "stop_sequences": {"type":"array","items":{"type":"string"}},
              "system": {"type":"string"}
            }
            """.trim();
    private static final String ADAPTIVE_OPTION_SCHEMA_JSON = """
            {
              "max_tokens": {"type":"integer","default":1024,"minimum":1},
              "thinking.type": {"type":"string","default":"adaptive","enum":["adaptive","disabled"]},
              "thinking.display": {"type":"string","default":"summarized","enum":["summarized","omitted"]},
              "output_config.effort": {"type":"string","default":"medium","enum":["low","medium","high"]},
              "temperature": {"type":"number","default":1.0,"minimum":0,"maximum":1},
              "stop_sequences": {"type":"array","items":{"type":"string"}},
              "system": {"type":"string"}
            }
            """.trim();
    private static final String HAIKU_OPTION_SCHEMA_JSON = """
            {
              "max_tokens": {"type":"integer","default":1024,"minimum":1},
              "thinking.type": {"type":"string","default":"enabled","enum":["enabled","disabled"]},
              "thinking.budget_tokens": {"type":"integer","default":10000,"minimum":1024},
              "temperature": {"type":"number","default":1.0,"minimum":0,"maximum":1},
              "stop_sequences": {"type":"array","items":{"type":"string"}},
              "system": {"type":"string"}
            }
            """.trim();

    public ClaudeProviderDefinition() {
        super(
                ProviderType.CLAUDE,
                "Claude Messages API",
                ENDPOINT,
                AuthType.HEADER,
                "x-api-key",
                null,
                VERSION_HEADERS_JSON,
                "Claude thinking options",
                List.of(
                        model("claude-opus-4-7", OPUS_47_OPTION_SCHEMA_JSON, RESPONSE_PATH),
                        model("claude-opus-4-6", ADAPTIVE_OPTION_SCHEMA_JSON, RESPONSE_PATH),
                        model("claude-sonnet-4-6", ADAPTIVE_OPTION_SCHEMA_JSON, RESPONSE_PATH),
                        model("claude-haiku-4-5-20251001", HAIKU_OPTION_SCHEMA_JSON, RESPONSE_PATH)
                )
        );
    }
}
