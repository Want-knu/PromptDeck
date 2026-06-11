package org.example.promtdeck.domain.provider.service;

import lombok.RequiredArgsConstructor;
import org.example.promtdeck.domain.organization.entity.Organization;
import org.example.promtdeck.domain.organization.service.OrganizationService;
import org.example.promtdeck.domain.provider.definition.GeminiProviderDefinition;
import org.example.promtdeck.domain.provider.definition.ProviderDefinitionRegistry;
import org.example.promtdeck.domain.provider.definition.ProviderSettingResolveCommand;
import org.example.promtdeck.domain.provider.definition.ResolvedProviderSetting;
import org.example.promtdeck.domain.provider.dto.request.ProviderSettingCreateRequest;
import org.example.promtdeck.domain.provider.dto.request.ProviderSettingUpdateRequest;
import org.example.promtdeck.domain.provider.dto.response.ProviderSettingOptionsResponse;
import org.example.promtdeck.domain.provider.dto.response.ProviderSettingResponse;
import org.example.promtdeck.domain.provider.entity.ProviderSetting;
import org.example.promtdeck.domain.provider.repository.ProviderSettingRepository;
import org.example.promtdeck.domain.provider.type.ProviderType;
import org.example.promtdeck.domain.user.entity.User;
import org.example.promtdeck.domain.user.repository.UserRepository;
import org.example.promtdeck.global.common.ErrorCode;
import org.example.promtdeck.global.exception.CustomException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class ProviderSettingService {

    private final UserRepository userRepository;
    private final ProviderSettingRepository providerSettingRepository;
    private final OrganizationService organizationService;
    private final ProviderDefinitionRegistry providerDefinitionRegistry;
    private final GeminiModelCatalogService geminiModelCatalogService;

    @Transactional
    public ProviderSettingResponse create(Long userId, ProviderSettingCreateRequest request) {
        User user = getUser(userId);
        Organization organization = organizationService.getAccessibleOrganization(request.organizationId(), user);
        ResolvedProviderSetting resolved = providerDefinitionRegistry.resolve(new ProviderSettingResolveCommand(
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
        ));

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
        ResolvedProviderSetting resolved = providerDefinitionRegistry.resolve(new ProviderSettingResolveCommand(
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
        ));

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

    public ProviderSettingOptionsResponse findOptions(Long userId) {
        ProviderSettingOptionsResponse options = providerDefinitionRegistry.options();
        User user = getUser(userId);
        List<String> geminiModels = geminiModelCatalogService.findGenerateContentModels(user);

        if (geminiModels.isEmpty()) {
            return options;
        }

        return replaceGeminiModels(options, geminiModels);
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

    private ProviderSettingOptionsResponse replaceGeminiModels(
            ProviderSettingOptionsResponse options,
            List<String> geminiModels
    ) {
        Map<ProviderType, ProviderSettingOptionsResponse.ProviderOption> providers = new LinkedHashMap<>(options.providers());
        ProviderSettingOptionsResponse.ProviderOption current = providers.get(ProviderType.GEMINI);

        if (current == null) {
            return options;
        }

        String defaultModel = geminiModels.contains(current.defaultModel())
                ? current.defaultModel()
                : geminiModels.get(0);
        Map<String, ProviderSettingOptionsResponse.ModelOption> modelOptions = new LinkedHashMap<>();
        geminiModels.forEach(model -> modelOptions.put(
                model,
                new ProviderSettingOptionsResponse.ModelOption(
                        GeminiProviderDefinition.optionSchemaForModel(model),
                        GeminiProviderDefinition.responsePath()
                )
        ));

        ProviderSettingOptionsResponse.ProviderOption gemini = new ProviderSettingOptionsResponse.ProviderOption(
                geminiModels,
                defaultModel,
                current.endpoints(),
                current.defaultEndpoint().replace(current.defaultModel(), defaultModel),
                current.methods(),
                current.defaultMethod(),
                current.authTypes(),
                current.defaultAuthType(),
                current.defaultAuthHeaderName(),
                current.defaultAuthQueryParamName(),
                current.responsePaths(),
                current.defaultResponsePath(),
                current.bodyTemplates(),
                List.of(new ProviderSettingOptionsResponse.NamedJsonOption(
                        "Gemini generation options",
                        GeminiProviderDefinition.optionSchemaForModel(defaultModel)
                )),
                modelOptions,
                current.custom()
        );
        providers.put(ProviderType.GEMINI, gemini);

        return new ProviderSettingOptionsResponse(options.providerTypes(), providers);
    }
}
