package org.example.promtdeck.domain.provider.definition;

import org.example.promtdeck.domain.provider.dto.response.ProviderSettingOptionsResponse;
import org.example.promtdeck.domain.provider.type.AuthType;
import org.example.promtdeck.domain.provider.type.HttpMethodType;
import org.example.promtdeck.domain.provider.type.ProviderType;
import org.example.promtdeck.global.common.ErrorCode;
import org.example.promtdeck.global.exception.CustomException;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import java.net.URI;
import java.util.List;
import java.util.Map;

@Component
public class CustomProviderDefinition implements ProviderDefinition {

    @Override
    public ProviderType type() {
        return ProviderType.CUSTOM;
    }

    @Override
    public ProviderSettingOptionsResponse.ProviderOption options() {
        return new ProviderSettingOptionsResponse.ProviderOption(
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
                Map.of(),
                true
        );
    }

    @Override
    public ResolvedProviderSetting resolve(ProviderSettingResolveCommand command) {
        if (!StringUtils.hasText(command.endpoint())) {
            throw new CustomException(ErrorCode.INVALID_PROVIDER_OPTION);
        }
        validateEndpoint(command.endpoint());

        AuthType resolvedAuthType = command.authType() == null ? AuthType.BEARER : command.authType();
        String resolvedAuthHeaderName = null;
        String resolvedAuthQueryParamName = null;

        if (resolvedAuthType == AuthType.BEARER) {
            resolvedAuthHeaderName = StringUtils.hasText(command.authHeaderName()) ? command.authHeaderName() : "Authorization";
        } else if (resolvedAuthType == AuthType.HEADER) {
            if (!StringUtils.hasText(command.authHeaderName())) {
                throw new CustomException(ErrorCode.INVALID_PROVIDER_OPTION);
            }
            resolvedAuthHeaderName = command.authHeaderName();
        } else if (resolvedAuthType == AuthType.QUERY_PARAM) {
            if (!StringUtils.hasText(command.authQueryParamName())) {
                throw new CustomException(ErrorCode.INVALID_PROVIDER_OPTION);
            }
            resolvedAuthQueryParamName = command.authQueryParamName();
        }

        return new ResolvedProviderSetting(
                command.endpoint(),
                command.method() == null ? HttpMethodType.POST : command.method(),
                resolvedAuthType,
                resolvedAuthHeaderName,
                resolvedAuthQueryParamName,
                command.headersJson(),
                command.queryParamsJson(),
                command.bodyTemplateJson(),
                command.optionSchemaJson(),
                command.responsePath()
        );
    }

    private void validateEndpoint(String endpoint) {
        try {
            URI uri = URI.create(endpoint);
            String scheme = uri.getScheme();
            String host = uri.getHost();

            if (!("https".equalsIgnoreCase(scheme) || "http".equalsIgnoreCase(scheme))
                    || !StringUtils.hasText(host)
                    || isBlockedHost(host)) {
                throw new CustomException(ErrorCode.INVALID_PROVIDER_OPTION);
            }
        } catch (IllegalArgumentException e) {
            throw new CustomException(ErrorCode.INVALID_PROVIDER_OPTION);
        }
    }

    private boolean isBlockedHost(String host) {
        String normalized = host.toLowerCase();
        return "localhost".equals(normalized)
                || normalized.endsWith(".localhost")
                || normalized.startsWith("127.")
                || normalized.startsWith("10.")
                || normalized.startsWith("192.168.")
                || normalized.matches("172\\.(1[6-9]|2\\d|3[0-1])\\..*")
                || normalized.startsWith("169.254.")
                || "0.0.0.0".equals(normalized)
                || "::1".equals(normalized)
                || normalized.startsWith("[::1]");
    }
}
