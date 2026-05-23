package org.example.promtdeck.domain.provider.dto.response;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.example.promtdeck.domain.provider.entity.ProviderExecutionPreset;
import org.example.promtdeck.domain.provider.type.ProviderType;

import java.time.LocalDateTime;
import java.util.Map;

public record ProviderExecutionPresetResponse(
        Long id,
        Long version,
        String displayName,
        Long providerSettingId,
        Long providerKeyId,
        Long organizationId,
        ProviderType providerType,
        String model,
        String prompt,
        String instructions,
        Map<String, Object> options,
        Map<String, Object> variables,
        LocalDateTime createdAt,
        LocalDateTime updatedAt
) {
    public static ProviderExecutionPresetResponse from(ProviderExecutionPreset preset, ObjectMapper objectMapper) {
        return new ProviderExecutionPresetResponse(
                preset.getId(),
                preset.getVersion(),
                preset.getDisplayName(),
                preset.getProviderSetting().getId(),
                preset.getProviderKeyId(),
                preset.getOrganization() == null ? null : preset.getOrganization().getId(),
                preset.getProviderSetting().getProviderType(),
                preset.getProviderSetting().getModel(),
                preset.getPrompt(),
                preset.getInstructions(),
                readMap(preset.getOptionsJson(), objectMapper),
                readMap(preset.getVariablesJson(), objectMapper),
                preset.getCreatedAt(),
                preset.getUpdatedAt()
        );
    }

    private static Map<String, Object> readMap(String json, ObjectMapper objectMapper) {
        if (json == null || json.isBlank()) {
            return Map.of();
        }

        try {
            return objectMapper.readValue(json, new TypeReference<>() {
            });
        } catch (Exception e) {
            return Map.of();
        }
    }
}
