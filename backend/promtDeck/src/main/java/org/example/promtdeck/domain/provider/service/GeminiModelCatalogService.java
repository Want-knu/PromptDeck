package org.example.promtdeck.domain.provider.service;

import lombok.RequiredArgsConstructor;
import org.example.promtdeck.domain.provider.entity.ProviderKey;
import org.example.promtdeck.domain.provider.repository.ProviderKeyRepository;
import org.example.promtdeck.domain.provider.type.ProviderType;
import org.example.promtdeck.domain.user.entity.User;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;

import java.time.Duration;
import java.time.Instant;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Service
@RequiredArgsConstructor
public class GeminiModelCatalogService {

    private static final Duration CACHE_TTL = Duration.ofHours(24);
    private static final String MODELS_ENDPOINT = "https://generativelanguage.googleapis.com/v1beta/models?key={apiKey}";

    private final ProviderKeyRepository providerKeyRepository;
    private final ApiKeyCrypto apiKeyCrypto;
    private final RestClient restClient;
    private final Map<String, CacheEntry> cache = new ConcurrentHashMap<>();

    public List<String> findGenerateContentModels(User user) {
        return providerKeyRepository.findAllByUser(user)
                .stream()
                .filter(key -> key.getProviderType() == ProviderType.GEMINI)
                .min(Comparator.comparing(ProviderKey::getId))
                .map(this::findGenerateContentModels)
                .orElse(List.of());
    }

    private List<String> findGenerateContentModels(ProviderKey key) {
        String cacheKey = key.getId() + ":" + key.getVersion();
        CacheEntry cached = cache.get(cacheKey);

        if (cached != null && !cached.isExpired()) {
            return cached.models();
        }

        try {
            String apiKey = apiKeyCrypto.decrypt(key.getEncryptedApiKey());
            GeminiListModelsResponse response = restClient.get()
                    .uri(MODELS_ENDPOINT, apiKey)
                    .retrieve()
                    .body(GeminiListModelsResponse.class);
            List<String> models = extractGenerateContentModels(response);

            if (!models.isEmpty()) {
                cache.put(cacheKey, new CacheEntry(models, Instant.now().plus(CACHE_TTL)));
            }

            return models;
        } catch (Exception e) {
            return cached == null ? List.of() : cached.models();
        }
    }

    private List<String> extractGenerateContentModels(GeminiListModelsResponse response) {
        if (response == null || response.models() == null) {
            return List.of();
        }

        return response.models()
                .stream()
                .filter(model -> model.supportedGenerationMethods() != null)
                .filter(model -> model.supportedGenerationMethods().contains("generateContent"))
                .map(model -> model.baseModelId() != null ? model.baseModelId() : model.name())
                .filter(model -> model != null && !model.isBlank())
                .map(model -> model.startsWith("models/") ? model.substring("models/".length()) : model)
                .filter(model -> model.startsWith("gemini-"))
                .filter(model -> !model.contains("tts"))
                .filter(model -> !model.contains("live"))
                .filter(model -> !model.contains("image"))
                .distinct()
                .sorted()
                .toList();
    }

    private record CacheEntry(
            List<String> models,
            Instant expiresAt
    ) {
        private boolean isExpired() {
            return Instant.now().isAfter(expiresAt);
        }
    }

    private record GeminiListModelsResponse(
            List<GeminiModel> models
    ) {
    }

    private record GeminiModel(
            String name,
            String baseModelId,
            List<String> supportedGenerationMethods
    ) {
    }
}
