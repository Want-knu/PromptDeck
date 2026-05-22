package org.example.promtdeck.domain.provider.dto.request;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import org.example.promtdeck.domain.provider.entity.ProviderSetting;
import org.example.promtdeck.global.common.ErrorCode;
import org.example.promtdeck.global.exception.CustomException;
import org.springframework.util.StringUtils;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

@JsonInclude(JsonInclude.Include.NON_NULL)
public record OpenAiRequest(
        String model,
        Object input,
        String instructions,
        Double temperature,
        @JsonProperty("top_p")
        Double topP,
        @JsonProperty("max_output_tokens")
        Integer maxOutputTokens,
        @JsonProperty("presence_penalty")
        Double presencePenalty,
        @JsonProperty("frequency_penalty")
        Double frequencyPenalty,
        Map<String, Object> reasoning,
        Object stop
) {

    private static final Set<String> SUPPORTED_MODELS = Set.of(
            "gpt-5.5",
            "gpt-5.4",
            "gpt-5.4-mini",
            "gpt-4o",
            "gpt-4o-mini"
    );
    private static final double DEFAULT_TEMPERATURE = 0.7;
    private static final double DEFAULT_TOP_P = 1.0;

    public static OpenAiRequest from(ProviderSetting setting, Map<String, Object> variables) {
        String model = resolveModel(setting.getModel());

        return new OpenAiRequest(
                model,
                resolveInput(variables),
                resolveString(variables.get("instructions")),
                resolveTemperature(variables.get("temperature")),
                supportsTopP(model) ? resolveTopP(variables.get("top_p")) : null,
                resolveMaxOutputTokens(variables.get("max_output_tokens")),
                resolvePenalty(variables.get("presence_penalty")),
                resolvePenalty(variables.get("frequency_penalty")),
                resolveReasoning(variables),
                variables.get("stop")
        );
    }

    private static String resolveModel(String model) {
        if (!SUPPORTED_MODELS.contains(model)) {
            throw new CustomException(ErrorCode.INVALID_PROVIDER_OPTION);
        }

        return model;
    }

    private static boolean supportsTopP(String model) {
        return model.startsWith("gpt-4");
    }

    private static Object resolveInput(Map<String, Object> variables) {
        Object input = variables.get("input");

        if (input instanceof String stringValue) {
            if (!StringUtils.hasText(stringValue)) {
                throw new CustomException(ErrorCode.INVALID_PROVIDER_OPTION);
            }
            return stringValue;
        }

        if (input instanceof List<?> listValue) {
            return listValue;
        }

        if (input != null) {
            throw new CustomException(ErrorCode.INVALID_PROVIDER_OPTION);
        }

        String prompt = String.valueOf(variables.getOrDefault("prompt", ""));
        if (!StringUtils.hasText(prompt)) {
            throw new CustomException(ErrorCode.INVALID_PROVIDER_OPTION);
        }

        return prompt;
    }

    private static String resolveString(Object value) {
        if (value == null) {
            return null;
        }

        if (value instanceof String stringValue) {
            return stringValue;
        }

        throw new CustomException(ErrorCode.INVALID_PROVIDER_OPTION);
    }

    private static Double resolveTemperature(Object value) {
        double temperature = resolveDouble(value, DEFAULT_TEMPERATURE);

        if (temperature < 0 || temperature > 2) {
            throw new CustomException(ErrorCode.INVALID_PROVIDER_OPTION);
        }

        return temperature;
    }

    private static Double resolveTopP(Object value) {
        double topP = resolveDouble(value, DEFAULT_TOP_P);

        if (topP < 0 || topP > 1) {
            throw new CustomException(ErrorCode.INVALID_PROVIDER_OPTION);
        }

        return topP;
    }

    private static Integer resolveMaxOutputTokens(Object value) {
        if (value == null) {
            return null;
        }

        int maxOutputTokens = resolveInteger(value);

        if (maxOutputTokens <= 0) {
            throw new CustomException(ErrorCode.INVALID_PROVIDER_OPTION);
        }

        return maxOutputTokens;
    }

    private static Double resolvePenalty(Object value) {
        if (value == null) {
            return null;
        }

        double penalty = resolveDouble(value, 0);

        if (penalty < -2 || penalty > 2) {
            throw new CustomException(ErrorCode.INVALID_PROVIDER_OPTION);
        }

        return penalty;
    }

    private static Map<String, Object> resolveReasoning(Map<String, Object> variables) {
        Map<String, Object> reasoning = new LinkedHashMap<>();
        String effort = resolveString(variables.get("reasoning.effort"));
        String summary = resolveString(variables.get("reasoning.summary"));

        if (effort != null) {
            reasoning.put("effort", effort);
        }

        if (summary != null) {
            reasoning.put("summary", summary);
        }

        return reasoning.isEmpty() ? null : reasoning;
    }

    private static double resolveDouble(Object value, double defaultValue) {
        if (value == null) {
            return defaultValue;
        }

        if (value instanceof Number number) {
            return number.doubleValue();
        }

        if (value instanceof String stringValue && StringUtils.hasText(stringValue)) {
            try {
                return Double.parseDouble(stringValue);
            } catch (NumberFormatException e) {
                throw new CustomException(ErrorCode.INVALID_PROVIDER_OPTION);
            }
        }

        throw new CustomException(ErrorCode.INVALID_PROVIDER_OPTION);
    }

    private static int resolveInteger(Object value) {
        if (value instanceof Number number) {
            return number.intValue();
        }

        if (value instanceof String stringValue && StringUtils.hasText(stringValue)) {
            try {
                return Integer.parseInt(stringValue);
            } catch (NumberFormatException e) {
                throw new CustomException(ErrorCode.INVALID_PROVIDER_OPTION);
            }
        }

        throw new CustomException(ErrorCode.INVALID_PROVIDER_OPTION);
    }
}
