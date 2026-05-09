package org.example.promtdeck.domain.provider.dto.response;

import org.example.promtdeck.domain.provider.entity.ProviderExecutionHistory;
import org.example.promtdeck.domain.provider.type.ProviderType;

import java.time.LocalDateTime;

public record ProviderExecutionHistoryResponse(
        Long id,
        Long organizationId,
        ProviderType providerType,
        String model,
        Long providerSettingId,
        Long providerKeyId,
        Integer statusCode,
        Boolean success,
        Long durationMs,
        String requestJson,
        String responseBody,
        String parsedResponse,
        String errorMessage,
        LocalDateTime createdAt
) {

    public static ProviderExecutionHistoryResponse from(ProviderExecutionHistory history) {
        return new ProviderExecutionHistoryResponse(
                history.getId(),
                history.getOrganization() == null ? null : history.getOrganization().getId(),
                history.getProviderType(),
                history.getModel(),
                history.getProviderSettingId(),
                history.getProviderKeyId(),
                history.getStatusCode(),
                history.getSuccess(),
                history.getDurationMs(),
                history.getRequestJson(),
                history.getResponseBody(),
                history.getParsedResponse(),
                history.getErrorMessage(),
                history.getCreatedAt()
        );
    }
}
