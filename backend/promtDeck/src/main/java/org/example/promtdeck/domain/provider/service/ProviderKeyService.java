package org.example.promtdeck.domain.provider.service;

import lombok.RequiredArgsConstructor;
import org.example.promtdeck.domain.provider.dto.request.ProviderKeyCreateRequest;
import org.example.promtdeck.domain.provider.dto.request.ProviderKeyUpdateRequest;
import org.example.promtdeck.domain.provider.dto.response.ProviderKeyResponse;
import org.example.promtdeck.domain.provider.entity.ProviderKey;
import org.example.promtdeck.domain.provider.repository.ProviderKeyRepository;
import org.example.promtdeck.domain.user.entity.User;
import org.example.promtdeck.domain.user.repository.UserRepository;
import org.example.promtdeck.global.common.ErrorCode;
import org.example.promtdeck.global.exception.CustomException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ProviderKeyService {

    private final UserRepository userRepository;
    private final ProviderKeyRepository providerKeyRepository;
    private final ApiKeyCrypto apiKeyCrypto;

    @Transactional
    public ProviderKeyResponse create(Long userId, ProviderKeyCreateRequest request) {
        User user = getUser(userId);
        String encryptedApiKey = apiKeyCrypto.encrypt(request.apiKey());
        String maskedApiKey = maskApiKey(request.apiKey());

        ProviderKey providerKey = ProviderKey.create(
                request.providerType(),
                encryptedApiKey,
                maskedApiKey,
                request.displayName(),
                user
        );

        return ProviderKeyResponse.from(providerKeyRepository.save(providerKey));
    }

    @Transactional(readOnly = true)
    public List<ProviderKeyResponse> findAll(Long userId) {
        User user = getUser(userId);

        return providerKeyRepository.findAllByUser(user)
                .stream()
                .map(ProviderKeyResponse::from)
                .toList();
    }

    @Transactional(readOnly = true)
    public ProviderKeyResponse findOne(Long userId, Long providerKeyId) {
        User user = getUser(userId);

        ProviderKey providerKey = providerKeyRepository.findByIdAndUser(providerKeyId, user)
                .orElseThrow(() -> new CustomException(ErrorCode.PROVIDER_KEY_NOT_FOUND));

        return ProviderKeyResponse.from(providerKey);
    }

    @Transactional
    public ProviderKeyResponse update(Long userId, Long providerKeyId, ProviderKeyUpdateRequest request) {
        User user = getUser(userId);

        ProviderKey providerKey = providerKeyRepository.findByIdAndUser(providerKeyId, user)
                .orElseThrow(() -> new CustomException(ErrorCode.PROVIDER_KEY_NOT_FOUND));

        validateVersion(providerKey.getVersion(), request.version());

        providerKey.update(
                request.providerType(),
                apiKeyCrypto.encrypt(request.apiKey()),
                maskApiKey(request.apiKey()),
                request.displayName()
        );

        return ProviderKeyResponse.from(providerKey);
    }

    @Transactional
    public void delete(Long userId, Long providerKeyId) {
        User user = getUser(userId);

        ProviderKey providerKey = providerKeyRepository.findByIdAndUser(providerKeyId, user)
                .orElseThrow(() -> new CustomException(ErrorCode.PROVIDER_KEY_NOT_FOUND));

        providerKeyRepository.delete(providerKey);
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

    private String maskApiKey(String apiKey) {
        if (apiKey.length() <= 8) {
            return "****";
        }

        return apiKey.substring(0, 4) + "..." + apiKey.substring(apiKey.length() - 4);
    }
}
