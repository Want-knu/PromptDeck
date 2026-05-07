package org.example.promtdeck.domain.provider.service;

import lombok.RequiredArgsConstructor;
import org.example.promtdeck.domain.provider.adapter.ProviderAdapter;
import org.example.promtdeck.domain.provider.adapter.ProviderAdapterResolver;
import org.example.promtdeck.domain.provider.client.ProviderClient;
import org.example.promtdeck.domain.provider.client.ProviderClientResolver;
import org.example.promtdeck.domain.provider.dto.request.ProviderExecuteRequest;
import org.example.promtdeck.domain.provider.dto.request.ProviderHttpRequest;
import org.example.promtdeck.domain.provider.dto.response.ProviderExecuteResponse;
import org.example.promtdeck.domain.provider.dto.response.ProviderPreviewResponse;
import org.example.promtdeck.domain.provider.entity.ProviderKey;
import org.example.promtdeck.domain.provider.entity.ProviderSetting;
import org.example.promtdeck.domain.provider.repository.ProviderKeyRepository;
import org.example.promtdeck.domain.provider.repository.ProviderSettingRepository;
import org.example.promtdeck.domain.user.entity.User;
import org.example.promtdeck.domain.user.repository.UserRepository;
import org.example.promtdeck.global.common.ErrorCode;
import org.example.promtdeck.global.exception.CustomException;
import org.springframework.stereotype.Service;

import java.util.LinkedHashMap;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class RequestExecutionService {

    private final UserRepository userRepository;
    private final ProviderKeyRepository providerKeyRepository;
    private final ProviderSettingRepository providerSettingRepository;
    private final ApiKeyCrypto apiKeyCrypto;
    private final ProviderAdapterResolver adapterResolver;
    private final ProviderClientResolver clientResolver;

    public ProviderExecuteResponse execute(Long userId, ProviderExecuteRequest request) {
        ExecutionContext context = buildExecutionContext(userId, request, false);
        ProviderClient client = clientResolver.getClient(context.setting().getProviderType());

        return client.execute(context.setting(), context.httpRequest());
    }

    public ProviderPreviewResponse preview(Long userId, ProviderExecuteRequest request) {
        ExecutionContext context = buildExecutionContext(userId, request, true);

        return ProviderPreviewResponse.from(context.httpRequest());
    }

    private ExecutionContext buildExecutionContext(Long userId, ProviderExecuteRequest request, boolean preview) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new CustomException(ErrorCode.USER_NOT_FOUND));

        ProviderKey providerKey = providerKeyRepository.findByIdAndUser(request.providerKeyId(), user)
                .orElseThrow(() -> new CustomException(ErrorCode.PROVIDER_KEY_NOT_FOUND));

        ProviderSetting setting = providerSettingRepository.findByIdAndUser(request.providerSettingId(), user)
                .orElseThrow(() -> new CustomException(ErrorCode.PROVIDER_SETTING_NOT_FOUND));

        if (providerKey.getProviderType() != setting.getProviderType()) {
            throw new CustomException(ErrorCode.PROVIDER_TYPE_MISMATCH);
        }

        String apiKey = preview ? providerKey.getMaskedApiKey() : apiKeyCrypto.decrypt(providerKey.getEncryptedApiKey());
        ProviderAdapter adapter = adapterResolver.getAdapter(setting.getProviderType());
        ProviderHttpRequest httpRequest = adapter.toHttpRequest(setting, apiKey, resolveVariables(setting, request));

        return new ExecutionContext(setting, httpRequest);
    }

    private Map<String, Object> resolveVariables(ProviderSetting setting, ProviderExecuteRequest request) {
        Map<String, Object> variables = new LinkedHashMap<>();

        if (request.variables() != null) {
            variables.putAll(request.variables());
        }

        if (request.prompt() != null) {
            variables.put("prompt", request.prompt());
        }

        variables.putIfAbsent("prompt", "");
        variables.putIfAbsent("model", setting.getModel());

        return variables;
    }

    private record ExecutionContext(
            ProviderSetting setting,
            ProviderHttpRequest httpRequest
    ) {
    }
}
