package org.example.promtdeck.domain.provider.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.example.promtdeck.domain.organization.service.OrganizationService;
import org.example.promtdeck.domain.provider.dto.request.ProviderExecutionPresetCreateRequest;
import org.example.promtdeck.domain.provider.dto.response.ProviderExecutionPresetResponse;
import org.example.promtdeck.domain.provider.entity.ProviderExecutionPreset;
import org.example.promtdeck.domain.provider.entity.ProviderKey;
import org.example.promtdeck.domain.provider.entity.ProviderSetting;
import org.example.promtdeck.domain.provider.repository.ProviderExecutionPresetRepository;
import org.example.promtdeck.domain.provider.repository.ProviderKeyRepository;
import org.example.promtdeck.domain.user.entity.User;
import org.example.promtdeck.domain.user.repository.UserRepository;
import org.example.promtdeck.global.common.ErrorCode;
import org.example.promtdeck.global.exception.CustomException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Comparator;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class ProviderExecutionPresetService {

    private final UserRepository userRepository;
    private final ProviderExecutionPresetRepository providerExecutionPresetRepository;
    private final ProviderKeyRepository providerKeyRepository;
    private final ProviderSettingService providerSettingService;
    private final OrganizationService organizationService;
    private final ObjectMapper objectMapper;

    @Transactional(readOnly = true)
    public List<ProviderExecutionPresetResponse> findAll(Long userId) {
        User user = getUser(userId);

        return providerExecutionPresetRepository.findAll()
                .stream()
                .filter(preset -> canAccess(preset, user))
                .sorted(Comparator.comparing(ProviderExecutionPreset::getUpdatedAt).reversed())
                .map(preset -> ProviderExecutionPresetResponse.from(preset, objectMapper))
                .toList();
    }

    @Transactional
    public ProviderExecutionPresetResponse create(Long userId, ProviderExecutionPresetCreateRequest request) {
        User user = getUser(userId);
        ProviderSetting setting = providerSettingService.getAccessibleSetting(request.providerSettingId(), user);
        Long providerKeyId = resolveProviderKeyId(request.providerKeyId(), user, setting);

        ProviderExecutionPreset preset = ProviderExecutionPreset.create(
                request.displayName(),
                setting,
                providerKeyId,
                request.prompt(),
                request.instructions(),
                writeJson(request.options()),
                writeJson(request.variables()),
                user
        );

        return ProviderExecutionPresetResponse.from(providerExecutionPresetRepository.save(preset), objectMapper);
    }

    @Transactional
    public ProviderExecutionPresetResponse update(
            Long userId,
            Long presetId,
            ProviderExecutionPresetCreateRequest request
    ) {
        User user = getUser(userId);
        ProviderExecutionPreset preset = getAccessiblePreset(presetId, user);
        ProviderSetting setting = providerSettingService.getAccessibleSetting(request.providerSettingId(), user);
        Long providerKeyId = resolveProviderKeyId(request.providerKeyId(), user, setting);

        preset.update(
                request.displayName(),
                setting,
                providerKeyId,
                request.prompt(),
                request.instructions(),
                writeJson(request.options()),
                writeJson(request.variables())
        );

        return ProviderExecutionPresetResponse.from(preset, objectMapper);
    }

    @Transactional
    public void delete(Long userId, Long presetId) {
        User user = getUser(userId);
        ProviderExecutionPreset preset = getAccessiblePreset(presetId, user);

        providerExecutionPresetRepository.delete(preset);
    }

    private ProviderExecutionPreset getAccessiblePreset(Long presetId, User user) {
        ProviderExecutionPreset preset = providerExecutionPresetRepository.findById(presetId)
                .orElseThrow(() -> new CustomException(ErrorCode.INVALID_REQUEST));

        if (!canAccess(preset, user)) {
            throw new CustomException(ErrorCode.INVALID_REQUEST);
        }

        return preset;
    }

    private boolean canAccess(ProviderExecutionPreset preset, User user) {
        if (preset.getUser().getId().equals(user.getId())) {
            return true;
        }

        return organizationService.canAccess(preset.getOrganization(), user);
    }

    private Long resolveProviderKeyId(Long providerKeyId, User user, ProviderSetting setting) {
        if (providerKeyId == null) {
            return null;
        }

        ProviderKey providerKey = providerKeyRepository.findByIdAndUser(providerKeyId, user)
                .orElseThrow(() -> new CustomException(ErrorCode.PROVIDER_KEY_NOT_FOUND));

        if (providerKey.getProviderType() != setting.getProviderType()) {
            throw new CustomException(ErrorCode.PROVIDER_TYPE_MISMATCH);
        }

        return providerKey.getId();
    }

    private String writeJson(Map<String, Object> value) {
        if (value == null || value.isEmpty()) {
            return null;
        }

        try {
            return objectMapper.writeValueAsString(value);
        } catch (Exception e) {
            throw new CustomException(ErrorCode.INVALID_PROVIDER_OPTION);
        }
    }

    private User getUser(Long userId) {
        return userRepository.findById(userId)
                .orElseThrow(() -> new CustomException(ErrorCode.USER_NOT_FOUND));
    }
}
