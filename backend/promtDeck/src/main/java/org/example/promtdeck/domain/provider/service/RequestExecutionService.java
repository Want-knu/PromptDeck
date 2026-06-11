package org.example.promtdeck.domain.provider.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.example.promtdeck.domain.organization.entity.Organization;
import org.example.promtdeck.domain.organization.service.OrganizationService;
import org.example.promtdeck.domain.provider.adapter.ProviderAdapter;
import org.example.promtdeck.domain.provider.adapter.ProviderAdapterResolver;
import org.example.promtdeck.domain.provider.client.ProviderClient;
import org.example.promtdeck.domain.provider.client.ProviderClientResolver;
import org.example.promtdeck.domain.provider.dto.request.ProviderExecuteRequest;
import org.example.promtdeck.domain.provider.dto.request.ProviderHttpRequest;
import org.example.promtdeck.domain.provider.dto.response.ProviderExecutionHistoryResponse;
import org.example.promtdeck.domain.provider.dto.response.ProviderExecuteResponse;
import org.example.promtdeck.domain.provider.dto.response.ProviderPreviewResponse;
import org.example.promtdeck.domain.provider.entity.ProviderKey;
import org.example.promtdeck.domain.provider.entity.ProviderSetting;
import org.example.promtdeck.domain.provider.entity.ProviderExecutionHistory;
import org.example.promtdeck.domain.provider.repository.ProviderExecutionHistoryRepository;
import org.example.promtdeck.domain.provider.repository.ProviderKeyRepository;
import org.example.promtdeck.domain.provider.type.AuthType;
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
public class RequestExecutionService {

    private final UserRepository userRepository;
    private final ProviderKeyRepository providerKeyRepository;
    private final ProviderExecutionHistoryRepository providerExecutionHistoryRepository;
    private final ApiKeyCrypto apiKeyCrypto;
    private final ProviderAdapterResolver adapterResolver;
    private final ProviderClientResolver clientResolver;
    private final ProviderSettingService providerSettingService;
    private final OrganizationService organizationService;
    private final ResponsePathExtractor responsePathExtractor;
    private final ObjectMapper objectMapper;

    @Transactional
    public ProviderExecuteResponse execute(Long userId, ProviderExecuteRequest request) {
        ExecutionContext context = buildExecutionContext(userId, request, false);
        ProviderClient client = clientResolver.getClient(context.setting().getProviderType());
        long startedAt = System.currentTimeMillis();

        ProviderExecuteResponse response = client.execute(context.setting(), context.httpRequest());
        long durationMs = System.currentTimeMillis() - startedAt;
        String parsedResponse = response.success()
                ? responsePathExtractor.extract(response.responseBody(), context.setting().getResponsePath())
                : null;
        ProviderExecuteResponse parsed = new ProviderExecuteResponse(
                response.providerType(),
                response.model(),
                response.statusCode(),
                response.success(),
                response.responseBody(),
                parsedResponse,
                response.errorMessage()
        );

        saveHistory(context, parsed, durationMs);

        return parsed;
    }

    public ProviderPreviewResponse preview(Long userId, ProviderExecuteRequest request) {
        ExecutionContext context = buildExecutionContext(userId, request, true);

        return ProviderPreviewResponse.from(context.httpRequest());
    }

    @Transactional(readOnly = true)
    public List<ProviderExecutionHistoryResponse> findHistory(Long userId, Long organizationId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new CustomException(ErrorCode.USER_NOT_FOUND));

        if (organizationId != null) {
            Organization organization = organizationService.getAccessibleOrganization(organizationId, user);

            return providerExecutionHistoryRepository.findAllByOrganizationOrderByCreatedAtDesc(organization)
                    .stream()
                    .map(ProviderExecutionHistoryResponse::from)
                    .toList();
        }

        return providerExecutionHistoryRepository.findAllByUserOrderByCreatedAtDesc(user)
                .stream()
                .map(ProviderExecutionHistoryResponse::from)
                .toList();
    }

    private ExecutionContext buildExecutionContext(Long userId, ProviderExecuteRequest request, boolean preview) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new CustomException(ErrorCode.USER_NOT_FOUND));

        ProviderSetting setting = providerSettingService.getAccessibleSetting(request.providerSettingId(), user);
        ProviderKey providerKey = resolveProviderKey(request, user, setting);

        if (providerKey != null && providerKey.getProviderType() != setting.getProviderType()) {
            throw new CustomException(ErrorCode.PROVIDER_TYPE_MISMATCH);
        }

        String apiKey = resolveApiKey(providerKey, preview);
        ProviderAdapter adapter = adapterResolver.getAdapter(setting.getProviderType());
        ProviderHttpRequest httpRequest = adapter.toHttpRequest(setting, apiKey, resolveVariables(setting, request));
        ProviderHttpRequest historyRequest = preview ? httpRequest : maskApiKey(httpRequest, apiKey);

        return new ExecutionContext(user, providerKey, setting, httpRequest, historyRequest);
    }

    private void saveHistory(ExecutionContext context, ProviderExecuteResponse response, long durationMs) {
        providerExecutionHistoryRepository.save(ProviderExecutionHistory.create(
                context.setting().getProviderType(),
                context.setting().getModel(),
                context.setting().getId(),
                context.providerKey() == null ? null : context.providerKey().getId(),
                response.statusCode(),
                response.success(),
                durationMs,
                writeJson(context.historyRequest()),
                response.responseBody(),
                response.parsedResponse(),
                response.errorMessage(),
                context.user(),
                context.setting().getOrganization()
        ));
    }

    private String writeJson(Object value) {
        try {
            return objectMapper.writeValueAsString(value);
        } catch (Exception e) {
            return null;
        }
    }

    private Map<String, Object> resolveVariables(ProviderSetting setting, ProviderExecuteRequest request) {
        Map<String, Object> variables = new LinkedHashMap<>();

        if (request.variables() != null) {
            variables.putAll(request.variables());
        }

        if (request.options() != null) {
            variables.putAll(request.options());
        }

        if (request.input() != null) {
            variables.put("input", request.input());
        }

        if (request.prompt() != null) {
            variables.put("prompt", request.prompt());
        }

        if (request.instructions() != null) {
            variables.put("instructions", request.instructions());
        }

        variables.putIfAbsent("prompt", "");
        variables.putIfAbsent("model", setting.getModel());

        return variables;
    }

    private ProviderKey resolveProviderKey(ProviderExecuteRequest request, User user, ProviderSetting setting) {
        if (request.providerKeyId() == null) {
            if (setting.getAuthType() == AuthType.NONE) {
                return null;
            }

            throw new CustomException(ErrorCode.PROVIDER_KEY_NOT_FOUND);
        }

        return providerKeyRepository.findByIdAndUser(request.providerKeyId(), user)
                .orElseThrow(() -> new CustomException(ErrorCode.PROVIDER_KEY_NOT_FOUND));
    }

    private String resolveApiKey(ProviderKey providerKey, boolean preview) {
        if (providerKey == null) {
            return "";
        }

        return preview ? providerKey.getMaskedApiKey() : apiKeyCrypto.decrypt(providerKey.getEncryptedApiKey());
    }

    private ProviderHttpRequest maskApiKey(ProviderHttpRequest request, String apiKey) {
        if (apiKey == null || apiKey.isBlank()) {
            return request;
        }

        Map<String, String> headers = new LinkedHashMap<>();
        request.headers().forEach((name, value) -> headers.put(name, maskSecret(value, apiKey)));

        return new ProviderHttpRequest(
                request.method(),
                maskSecret(request.endpoint(), apiKey),
                headers,
                request.body()
        );
    }

    private String maskSecret(String value, String secret) {
        if (value == null) {
            return null;
        }

        return value.replace(secret, "****");
    }

    private record ExecutionContext(
            User user,
            ProviderKey providerKey,
            ProviderSetting setting,
            ProviderHttpRequest httpRequest,
            ProviderHttpRequest historyRequest
    ) {
    }
}
