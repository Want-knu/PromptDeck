package org.example.promtdeck.domain.provider.dto.request;

import org.example.promtdeck.domain.provider.type.HttpMethodType;

import java.util.Map;

public record ProviderHttpRequest(
        HttpMethodType method,
        String endpoint,
        Map<String, String> headers,
        Object body
) {
}
