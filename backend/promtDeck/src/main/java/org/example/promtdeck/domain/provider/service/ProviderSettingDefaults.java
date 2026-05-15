package org.example.promtdeck.domain.provider.service;

import org.example.promtdeck.domain.provider.dto.response.ProviderSettingOptionsResponse;
import org.example.promtdeck.domain.provider.type.AuthType;
import org.example.promtdeck.domain.provider.type.HttpMethodType;
import org.example.promtdeck.domain.provider.type.ProviderType;
import org.example.promtdeck.global.common.ErrorCode;
import org.example.promtdeck.global.exception.CustomException;
import org.springframework.util.StringUtils;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public final class ProviderSettingDefaults {

    private static final String OPENAI_ENDPOINT = "https://api.openai.com/v1/responses";
    private static final String CLAUDE_ENDPOINT = "https://api.anthropic.com/v1/messages";
    private static final String GEMINI_ENDPOINT_TEMPLATE = "https://generativelanguage.googleapis.com/v1beta/models/%s:generateContent";

    private ProviderSettingDefaults() {
    }

    public static ResolvedSetting resolve(
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
        return switch (providerType) {
            case OPENAI -> official(
                    OPENAI_ENDPOINT,
                    AuthType.BEARER,
                    "Authorization",
                    null,
                    null,
                    "output_text"
            );
            case CLAUDE -> official(
                    CLAUDE_ENDPOINT,
                    AuthType.HEADER,
                    "x-api-key",
                    null,
                    null,
                    "content[0].text"
            );
            case GEMINI -> official(
                    GEMINI_ENDPOINT_TEMPLATE.formatted(model),
                    AuthType.QUERY_PARAM,
                    null,
                    "key",
                    null,
                    "candidates[0].content.parts[0].text"
            );
            case CUSTOM -> custom(
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
        };
    }

    public static ProviderSettingOptionsResponse options() {
        Map<ProviderType, ProviderSettingOptionsResponse.ProviderOption> providers = new LinkedHashMap<>();
        providers.put(ProviderType.OPENAI, option(
                List.of("gpt-5.5", "gpt-5.4-mini", "gpt-5.4-nano"),
                "gpt-5.5",
                endpoint("OpenAI Responses API", OPENAI_ENDPOINT, AuthType.BEARER, "Authorization", null, "output_text"),
                List.of(AuthType.BEARER),
                AuthType.BEARER,
                "Authorization",
                null,
                List.of("output_text"),
                "output_text",
                false
        ));
        providers.put(ProviderType.CLAUDE, option(
                List.of("claude-3-5-sonnet-latest", "claude-3-5-haiku-latest"),
                "claude-3-5-sonnet-latest",
                endpoint("Claude Messages API", CLAUDE_ENDPOINT, AuthType.HEADER, "x-api-key", null, "content[0].text"),
                List.of(AuthType.HEADER),
                AuthType.HEADER,
                "x-api-key",
                null,
                List.of("content[0].text"),
                "content[0].text",
                false
        ));
        providers.put(ProviderType.GEMINI, option(
                List.of("gemini-1.5-pro", "gemini-1.5-flash"),
                "gemini-1.5-pro",
                endpoint("Gemini Generate Content API", GEMINI_ENDPOINT_TEMPLATE.formatted("gemini-1.5-pro"), AuthType.QUERY_PARAM, null, "key", "candidates[0].content.parts[0].text"),
                List.of(AuthType.QUERY_PARAM),
                AuthType.QUERY_PARAM,
                null,
                "key",
                List.of("candidates[0].content.parts[0].text"),
                "candidates[0].content.parts[0].text",
                false
        ));
        providers.put(ProviderType.CUSTOM, new ProviderSettingOptionsResponse.ProviderOption(
                List.of("custom-model"),
                "custom-model",
                List.of(),
                "",
                List.of(HttpMethodType.GET, HttpMethodType.POST, HttpMethodType.PUT, HttpMethodType.PATCH, HttpMethodType.DELETE),
                HttpMethodType.POST,
                List.of(AuthType.BEARER, AuthType.HEADER, AuthType.QUERY_PARAM, AuthType.NONE),
                AuthType.BEARER,
                "Authorization",
                null,
                List.of(""),
                "",
                List.of(),
                List.of(),
                true
        ));

        return new ProviderSettingOptionsResponse(List.of(ProviderType.OPENAI, ProviderType.GEMINI, ProviderType.CLAUDE, ProviderType.CUSTOM), providers);
    }

    private static ResolvedSetting official(
            String endpoint,
            AuthType authType,
            String authHeaderName,
            String authQueryParamName,
            String optionSchemaJson,
            String responsePath
    ) {
        return new ResolvedSetting(
                endpoint,
                HttpMethodType.POST,
                authType,
                authHeaderName,
                authQueryParamName,
                null,
                null,
                null,
                optionSchemaJson,
                responsePath
        );
    }

    private static ResolvedSetting custom(
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
        if (!StringUtils.hasText(endpoint)) {
            throw new CustomException(ErrorCode.INVALID_PROVIDER_OPTION);
        }

        AuthType resolvedAuthType = authType == null ? AuthType.BEARER : authType;
        String resolvedAuthHeaderName = null;
        String resolvedAuthQueryParamName = null;

        if (resolvedAuthType == AuthType.BEARER) {
            resolvedAuthHeaderName = StringUtils.hasText(authHeaderName) ? authHeaderName : "Authorization";
        } else if (resolvedAuthType == AuthType.HEADER) {
            if (!StringUtils.hasText(authHeaderName)) {
                throw new CustomException(ErrorCode.INVALID_PROVIDER_OPTION);
            }
            resolvedAuthHeaderName = authHeaderName;
        } else if (resolvedAuthType == AuthType.QUERY_PARAM) {
            if (!StringUtils.hasText(authQueryParamName)) {
                throw new CustomException(ErrorCode.INVALID_PROVIDER_OPTION);
            }
            resolvedAuthQueryParamName = authQueryParamName;
        }

        return new ResolvedSetting(
                endpoint,
                method == null ? HttpMethodType.POST : method,
                resolvedAuthType,
                resolvedAuthHeaderName,
                resolvedAuthQueryParamName,
                headersJson,
                queryParamsJson,
                bodyTemplateJson,
                optionSchemaJson,
                responsePath
        );
    }

    private static ProviderSettingOptionsResponse.ProviderOption option(
            List<String> models,
            String defaultModel,
            ProviderSettingOptionsResponse.EndpointOption endpoint,
            List<AuthType> authTypes,
            AuthType defaultAuthType,
            String defaultAuthHeaderName,
            String defaultAuthQueryParamName,
            List<String> responsePaths,
            String defaultResponsePath,
            boolean custom
    ) {
        return new ProviderSettingOptionsResponse.ProviderOption(
                models,
                defaultModel,
                List.of(endpoint),
                endpoint.value(),
                List.of(HttpMethodType.POST),
                HttpMethodType.POST,
                authTypes,
                defaultAuthType,
                defaultAuthHeaderName,
                defaultAuthQueryParamName,
                responsePaths,
                defaultResponsePath,
                List.of(),
                List.of(),
                custom
        );
    }

    private static ProviderSettingOptionsResponse.EndpointOption endpoint(
            String label,
            String value,
            AuthType authType,
            String authHeaderName,
            String authQueryParamName,
            String responsePath
    ) {
        return new ProviderSettingOptionsResponse.EndpointOption(
                label,
                value,
                HttpMethodType.POST,
                authType,
                authHeaderName,
                authQueryParamName,
                null,
                null,
                null,
                null,
                responsePath
        );
    }

    public record ResolvedSetting(
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
    }
}
