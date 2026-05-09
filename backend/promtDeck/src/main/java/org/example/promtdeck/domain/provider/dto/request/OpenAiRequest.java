package org.example.promtdeck.domain.provider.dto.request;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import org.example.promtdeck.domain.provider.entity.ProviderSetting;
import org.example.promtdeck.global.common.ErrorCode;
import org.example.promtdeck.global.exception.CustomException;
import org.springframework.util.StringUtils;

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
        Integer maxOutputTokens
) {

    private static final Set<String> SUPPORTED_MODELS = Set.of(
            "gpt-5.5",
            "gpt-5.4-mini",
            "gpt-5.4-nano"
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
                resolveTopP(variables.get("top_p")),
                resolveMaxOutputTokens(variables.get("max_output_tokens"))
        );
    }

    private static String resolveModel(String model) {
        if (!SUPPORTED_MODELS.contains(model)) {
            throw new CustomException(ErrorCode.INVALID_PROVIDER_OPTION);
        }

        return model;
    }

    private static Object resolveInput(Map<String, Object> variables) {
        Object input = variables.get("input");

        if (input instanceof String stringValue) {
            return stringValue;
        }

        if (input instanceof List<?> listValue) {
            return listValue;
        }

        if (input != null) {
            throw new CustomException(ErrorCode.INVALID_PROVIDER_OPTION);
        }

        return String.valueOf(variables.getOrDefault("prompt", ""));
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
