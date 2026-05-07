package org.example.promtdeck.domain.provider.dto.response;

import org.example.promtdeck.domain.provider.dto.request.ProviderHttpRequest;
import org.example.promtdeck.domain.provider.type.HttpMethodType;

import java.util.Map;

public record ProviderPreviewResponse(
        HttpMethodType method,
        String endpoint,
        Map<String, String> headers,
        Object body
) {
    public static ProviderPreviewResponse from(ProviderHttpRequest request) {
        return new ProviderPreviewResponse(
                request.method(),
                request.endpoint(),
                request.headers(),
                request.body()
        );
    }
}
