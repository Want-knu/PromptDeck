package org.example.promtdeck.domain.provider.definition;

import org.example.promtdeck.domain.provider.type.AuthType;
import org.example.promtdeck.domain.provider.type.ProviderType;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class GeminiProviderDefinition extends AbstractOfficialProviderDefinition {

    private static final String ENDPOINT_TEMPLATE = "https://generativelanguage.googleapis.com/v1beta/models/%s:generateContent";
    private static final String RESPONSE_PATH = "candidates[0].content.parts[0].text";
    private static final String GEMINI_3_OPTION_SCHEMA_JSON = """
            {
              "temperature": {"type":"number","default":1.0,"minimum":0,"maximum":2},
              "generationConfig.topP": {"type":"number","default":1.0,"minimum":0,"maximum":1},
              "generationConfig.topK": {"type":"integer"},
              "generationConfig.maxOutputTokens": {"type":"integer","default":1024},
              "thinkingConfig.thinkingLevel": {"type":"string","enum":["MINIMAL","LOW","MEDIUM","HIGH"]},
              "thinkingConfig.includeThoughts": {"type":"boolean"},
              "generationConfig.responseMimeType": {"type":"string","default":"text/plain","enum":["text/plain","application/json"]}
            }
            """.trim();
    private static final String GEMINI_25_OPTION_SCHEMA_JSON = """
            {
              "temperature": {"type":"number","default":1.0,"minimum":0,"maximum":2},
              "generationConfig.topP": {"type":"number","default":1.0,"minimum":0,"maximum":1},
              "generationConfig.topK": {"type":"integer"},
              "generationConfig.maxOutputTokens": {"type":"integer","default":1024},
              "thinkingConfig.thinkingBudget": {"type":"integer"},
              "generationConfig.responseMimeType": {"type":"string","default":"text/plain","enum":["text/plain","application/json"]}
            }
            """.trim();

    public static String optionSchemaForModel(String model) {
        if (model != null && model.startsWith("gemini-3")) {
            return GEMINI_3_OPTION_SCHEMA_JSON;
        }

        return GEMINI_25_OPTION_SCHEMA_JSON;
    }

    public static String responsePath() {
        return RESPONSE_PATH;
    }

    public GeminiProviderDefinition() {
        super(
                ProviderType.GEMINI,
                "Gemini Generate Content API",
                ENDPOINT_TEMPLATE,
                AuthType.QUERY_PARAM,
                null,
                "key",
                null,
                "Gemini generation options",
                List.of(
                        model("gemini-3.5-flash", GEMINI_3_OPTION_SCHEMA_JSON, RESPONSE_PATH),
                        model("gemini-3.1-pro-preview", GEMINI_3_OPTION_SCHEMA_JSON, RESPONSE_PATH),
                        model("gemini-3-flash-preview", GEMINI_3_OPTION_SCHEMA_JSON, RESPONSE_PATH),
                        model("gemini-2.5-pro", GEMINI_25_OPTION_SCHEMA_JSON, RESPONSE_PATH),
                        model("gemini-2.5-flash", GEMINI_25_OPTION_SCHEMA_JSON, RESPONSE_PATH),
                        model("gemini-2.5-flash-lite", GEMINI_25_OPTION_SCHEMA_JSON, RESPONSE_PATH)
                )
        );
    }
}
