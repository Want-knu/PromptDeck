package org.example.promtdeck.domain.provider.service;

import lombok.RequiredArgsConstructor;
import org.example.promtdeck.domain.organization.entity.Organization;
import org.example.promtdeck.domain.organization.service.OrganizationService;
import org.example.promtdeck.domain.provider.dto.request.ProviderSettingCreateRequest;
import org.example.promtdeck.domain.provider.dto.request.ProviderSettingUpdateRequest;
import org.example.promtdeck.domain.provider.dto.response.ProviderSettingOptionsResponse;
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
    private final OrganizationService organizationService;

    @Transactional
    public ProviderSettingResponse create(Long userId, ProviderSettingCreateRequest request) {
        User user = getUser(userId);
        Organization organization = organizationService.getAccessibleOrganization(request.organizationId(), user);
        ProviderSettingDefaults.ResolvedSetting resolved = ProviderSettingDefaults.resolve(
                request.providerType(),
                request.model(),
                request.endpoint(),
                request.method(),
                request.authType(),
                request.authHeaderName(),
                request.authQueryParamName(),
                request.headersJson(),
                request.queryParamsJson(),
                request.bodyTemplateJson(),
                request.optionSchemaJson(),
                request.responsePath()
        );

        ProviderSetting setting = ProviderSetting.create(
                request.providerType(),
                request.displayName(),
                request.model(),
                resolved.endpoint(),
                resolved.method(),
                resolved.authType(),
                resolved.authHeaderName(),
                resolved.authQueryParamName(),
                resolved.headersJson(),
                resolved.queryParamsJson(),
                resolved.bodyTemplateJson(),
                resolved.optionSchemaJson(),
                resolved.responsePath(),
                user,
                organization
        );

        return ProviderSettingResponse.from(providerSettingRepository.save(setting));
    }

    @Transactional(readOnly = true)
    public List<ProviderSettingResponse> findAll(Long userId) {
        User user = getUser(userId);

        return providerSettingRepository.findAll()
                .stream()
                .filter(setting -> canAccess(setting, user))
                .map(ProviderSettingResponse::from)
                .toList();
    }

    @Transactional(readOnly = true)
    public ProviderSettingResponse findOne(Long userId, Long providerSettingId) {
        User user = getUser(userId);

        ProviderSetting setting = getAccessibleSetting(providerSettingId, user);

        return ProviderSettingResponse.from(setting);
    }

    @Transactional
    public ProviderSettingResponse update(Long userId, Long providerSettingId, ProviderSettingUpdateRequest request) {
        User user = getUser(userId);

        ProviderSetting setting = getAccessibleSetting(providerSettingId, user);

        validateVersion(setting.getVersion(), request.version());
        ProviderSettingDefaults.ResolvedSetting resolved = ProviderSettingDefaults.resolve(
                setting.getProviderType(),
                request.model(),
                request.endpoint(),
                request.method(),
                request.authType(),
                request.authHeaderName(),
                request.authQueryParamName(),
                request.headersJson(),
                request.queryParamsJson(),
                request.bodyTemplateJson(),
                request.optionSchemaJson(),
                request.responsePath()
        );

        setting.update(
                request.displayName(),
                request.model(),
                resolved.endpoint(),
                resolved.method(),
                resolved.authType(),
                resolved.authHeaderName(),
                resolved.authQueryParamName(),
                resolved.headersJson(),
                resolved.queryParamsJson(),
                resolved.bodyTemplateJson(),
                resolved.optionSchemaJson(),
                resolved.responsePath()
        );

        return ProviderSettingResponse.from(setting);
    }

    public ProviderSettingOptionsResponse findOptions() {
        return ProviderSettingDefaults.options();
    }

    @Transactional
    public void delete(Long userId, Long providerSettingId) {
        User user = getUser(userId);

        ProviderSetting setting = getAccessibleSetting(providerSettingId, user);

        providerSettingRepository.delete(setting);
    }

    public ProviderSetting getAccessibleSetting(Long providerSettingId, User user) {
        ProviderSetting setting = providerSettingRepository.findById(providerSettingId)
                .orElseThrow(() -> new CustomException(ErrorCode.PROVIDER_SETTING_NOT_FOUND));

        if (!canAccess(setting, user)) {
            throw new CustomException(ErrorCode.PROVIDER_SETTING_NOT_FOUND);
        }

        return setting;
    }

    private boolean canAccess(ProviderSetting setting, User user) {
        if (setting.getUser().getId().equals(user.getId())) {
            return true;
        }

        return organizationService.canAccess(setting.getOrganization(), user);
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
