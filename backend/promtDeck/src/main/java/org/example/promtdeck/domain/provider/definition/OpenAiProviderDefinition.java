package org.example.promtdeck.domain.provider.definition;

import org.example.promtdeck.domain.provider.type.AuthType;
import org.example.promtdeck.domain.provider.type.ProviderType;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class OpenAiProviderDefinition extends AbstractOfficialProviderDefinition {

    private static final String ENDPOINT = "https://api.openai.com/v1/responses";
    private static final String RESPONSE_PATH = "output_text";
    private static final String GPT5_OPTION_SCHEMA_JSON = """
            {
              "temperature": {"type":"number","default":1.0,"minimum":0,"maximum":2},
              "max_output_tokens": {"type":"integer","default":1024,"minimum":16},
              "presence_penalty": {"type":"number","default":0,"minimum":-2,"maximum":2},
              "frequency_penalty": {"type":"number","default":0,"minimum":-2,"maximum":2},
              "reasoning.effort": {"type":"string","default":"medium","enum":["none","minimal","low","medium","high","xhigh"]},
              "reasoning.summary": {"type":"string","default":"auto","enum":["auto","concise","detailed"]}
            }
            """.trim();
    private static final String GPT4_OPTION_SCHEMA_JSON = """
            {
              "temperature": {"type":"number","default":1.0,"minimum":0,"maximum":2},
              "top_p": {"type":"number","default":1.0,"minimum":0,"maximum":1},
              "max_output_tokens": {"type":"integer","default":1024,"minimum":16},
              "presence_penalty": {"type":"number","default":0,"minimum":-2,"maximum":2},
              "frequency_penalty": {"type":"number","default":0,"minimum":-2,"maximum":2},
              "stop": {"type":"array","items":{"type":"string"}}
            }
            """.trim();

    public OpenAiProviderDefinition() {
        super(
                ProviderType.OPENAI,
                "OpenAI Responses API",
                ENDPOINT,
                AuthType.BEARER,
                "Authorization",
                null,
                null,
                "GPT options",
                List.of(
                        model("gpt-5.5", GPT5_OPTION_SCHEMA_JSON, RESPONSE_PATH),
                        model("gpt-5.4", GPT5_OPTION_SCHEMA_JSON, RESPONSE_PATH),
                        model("gpt-5.4-mini", GPT5_OPTION_SCHEMA_JSON, RESPONSE_PATH),
                        model("gpt-4o", GPT4_OPTION_SCHEMA_JSON, RESPONSE_PATH),
                        model("gpt-4o-mini", GPT4_OPTION_SCHEMA_JSON, RESPONSE_PATH)
                )
        );
    }
}
