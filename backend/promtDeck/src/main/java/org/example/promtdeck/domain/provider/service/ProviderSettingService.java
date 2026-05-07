package org.example.promtdeck.domain.provider.service;

import lombok.RequiredArgsConstructor;
import org.example.promtdeck.domain.provider.dto.request.ProviderSettingCreateRequest;
import org.example.promtdeck.domain.provider.dto.request.ProviderSettingUpdateRequest;
import org.example.promtdeck.domain.provider.dto.response.ProviderSettingResponse;
import org.example.promtdeck.domain.provider.entity.ProviderSetting;
import org.example.promtdeck.domain.provider.repository.ProviderSettingRepository;
import org.example.promtdeck.domain.user.entity.User;
import org.example.promtdeck.domain.user.repository.UserRepository;
import org.example.promtdeck.global.common.ErrorCode;
import org.example.promtdeck.global.exception.CustomException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ProviderSettingService {

    private final UserRepository userRepository;
    private final ProviderSettingRepository providerSettingRepository;

    @Transactional
    public ProviderSettingResponse create(Long userId, ProviderSettingCreateRequest request) {
        User user = getUser(userId);

        ProviderSetting setting = ProviderSetting.create(
                request.providerType(),
                request.displayName(),
                request.model(),
                request.endpoint(),
                request.method(),
                request.authType(),
                request.authHeaderName(),
                request.authQueryParamName(),
                request.headersJson(),
                request.queryParamsJson(),
                request.bodyTemplateJson(),
                request.responsePath(),
                user
        );

        return ProviderSettingResponse.from(providerSettingRepository.save(setting));
    }

    @Transactional(readOnly = true)
    public List<ProviderSettingResponse> findAll(Long userId) {
        User user = getUser(userId);

        return providerSettingRepository.findAllByUser(user)
                .stream()
                .map(ProviderSettingResponse::from)
                .toList();
    }

    @Transactional(readOnly = true)
    public ProviderSettingResponse findOne(Long userId, Long providerSettingId) {
        User user = getUser(userId);

        ProviderSetting setting = providerSettingRepository.findByIdAndUser(providerSettingId, user)
                .orElseThrow(() -> new CustomException(ErrorCode.PROVIDER_SETTING_NOT_FOUND));

        return ProviderSettingResponse.from(setting);
    }

    @Transactional
    public ProviderSettingResponse update(Long userId, Long providerSettingId, ProviderSettingUpdateRequest request) {
        User user = getUser(userId);

        ProviderSetting setting = providerSettingRepository.findByIdAndUser(providerSettingId, user)
                .orElseThrow(() -> new CustomException(ErrorCode.PROVIDER_SETTING_NOT_FOUND));

        validateVersion(setting.getVersion(), request.version());

        setting.update(
                request.displayName(),
                request.model(),
                request.endpoint(),
                request.method(),
                request.authType(),
                request.authHeaderName(),
                request.authQueryParamName(),
                request.headersJson(),
                request.queryParamsJson(),
                request.bodyTemplateJson(),
                request.responsePath()
        );

        return ProviderSettingResponse.from(setting);
    }

    @Transactional
    public void delete(Long userId, Long providerSettingId) {
        User user = getUser(userId);

        ProviderSetting setting = providerSettingRepository.findByIdAndUser(providerSettingId, user)
                .orElseThrow(() -> new CustomException(ErrorCode.PROVIDER_SETTING_NOT_FOUND));

        providerSettingRepository.delete(setting);
    }

    private User getUser(Long userId) {
        return userRepository.findById(userId)
                .orElseThrow(() -> new CustomException(ErrorCode.USER_NOT_FOUND));
    }

    private void validateVersion(Long currentVersion, Long requestVersion) {
        if (!currentVersion.equals(requestVersion)) {
            throw new CustomException(ErrorCode.CONFLICT_RESOURCE);
        }
    }
}
