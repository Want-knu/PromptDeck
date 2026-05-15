package org.example.promtdeck.domain.provider.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.example.promtdeck.domain.organization.service.OrganizationService;
import org.example.promtdeck.domain.provider.adapter.ProviderAdapter;
import org.example.promtdeck.domain.provider.adapter.ProviderAdapterResolver;
import org.example.promtdeck.domain.provider.client.ProviderClient;
import org.example.promtdeck.domain.provider.client.ProviderClientResolver;
import org.example.promtdeck.domain.provider.dto.request.ProviderExecuteRequest;
import org.example.promtdeck.domain.provider.dto.request.ProviderHttpRequest;
import org.example.promtdeck.domain.provider.dto.response.ProviderExecuteResponse;
import org.example.promtdeck.domain.provider.entity.ProviderExecutionHistory;
import org.example.promtdeck.domain.provider.entity.ProviderKey;
import org.example.promtdeck.domain.provider.entity.ProviderSetting;
import org.example.promtdeck.domain.provider.repository.ProviderExecutionHistoryRepository;
import org.example.promtdeck.domain.provider.repository.ProviderKeyRepository;
import org.example.promtdeck.domain.provider.type.HttpMethodType;
import org.example.promtdeck.domain.provider.type.ProviderType;
import org.example.promtdeck.domain.user.entity.User;
import org.example.promtdeck.domain.user.repository.UserRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.Map;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class RequestExecutionServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private ProviderExecutionHistoryRepository providerExecutionHistoryRepository;

    @Mock
    private ProviderKeyRepository providerKeyRepository;

    @Mock
    private ApiKeyCrypto apiKeyCrypto;

    @Mock
    private ProviderAdapterResolver adapterResolver;

    @Mock
    private ProviderClientResolver clientResolver;

    @Mock
    private ProviderSettingService providerSettingService;

    private OrganizationService organizationService;

    @Mock
    private ProviderAdapter providerAdapter;

    @Mock
    private ProviderClient providerClient;

    @Test
    void executeStoresHistoryWithParsedResponse() {
        RequestExecutionService requestExecutionService = new RequestExecutionService(
                userRepository,
                providerKeyRepository,
                providerExecutionHistoryRepository,
                apiKeyCrypto,
                adapterResolver,
                clientResolver,
                providerSettingService,
                organizationService,
                new ResponsePathExtractor(new ObjectMapper()),
                new ObjectMapper()
        );

        User user = User.createLocalUser("test@example.com", "password", "test");
        ReflectionTestUtils.setField(user, "id", 10L);

        ProviderKey key = ProviderKey.create(ProviderType.OPENAI, "encrypted", "sk-****", "OpenAI key", user);
        ReflectionTestUtils.setField(key, "id", 20L);

        ProviderSetting setting = ProviderSetting.create(
                ProviderType.OPENAI,
                "OpenAI",
                "gpt-5.5",
                "https://api.openai.com/v1/responses",
                HttpMethodType.POST,
                null,
                null,
                null,
                null,
                null,
                null,
                null,
                "output_text",
                user,
                null
        );
        ReflectionTestUtils.setField(setting, "id", 30L);

        ProviderHttpRequest httpRequest = new ProviderHttpRequest(
                HttpMethodType.POST,
                "https://api.openai.com/v1/responses",
                Map.of("Authorization", "Bearer secret"),
                Map.of("input", "Explain interfaces")
        );

        when(userRepository.findById(10L)).thenReturn(Optional.of(user));
        when(providerKeyRepository.findByIdAndUser(20L, user)).thenReturn(Optional.of(key));
        when(providerSettingService.getAccessibleSetting(30L, user)).thenReturn(setting);
        when(apiKeyCrypto.decrypt("encrypted")).thenReturn("secret");
        when(adapterResolver.getAdapter(ProviderType.OPENAI)).thenReturn(providerAdapter);
        when(providerAdapter.toHttpRequest(any(), any(), any())).thenReturn(httpRequest);
        when(clientResolver.getClient(ProviderType.OPENAI)).thenReturn(providerClient);
        when(providerClient.execute(setting, httpRequest)).thenReturn(new ProviderExecuteResponse(
                ProviderType.OPENAI,
                "gpt-5.5",
                200,
                true,
                "{\"output_text\":\"parsed answer\"}",
                null,
                null
        ));

        ProviderExecuteResponse response = requestExecutionService.execute(
                10L,
                new ProviderExecuteRequest(
                        20L,
                        30L,
                        null,
                        "Explain interfaces",
                        "Be concise",
                        Map.of("temperature", 0.7),
                        null
                )
        );

        assertThat(response.parsedResponse()).isEqualTo("parsed answer");

        ArgumentCaptor<ProviderExecutionHistory> historyCaptor = ArgumentCaptor.forClass(ProviderExecutionHistory.class);
        verify(providerExecutionHistoryRepository).save(historyCaptor.capture());

        ProviderExecutionHistory history = historyCaptor.getValue();
        assertThat(history.getProviderType()).isEqualTo(ProviderType.OPENAI);
        assertThat(history.getProviderSettingId()).isEqualTo(30L);
        assertThat(history.getProviderKeyId()).isEqualTo(20L);
        assertThat(history.getStatusCode()).isEqualTo(200);
        assertThat(history.getSuccess()).isTrue();
        assertThat(history.getParsedResponse()).isEqualTo("parsed answer");
        assertThat(history.getDurationMs()).isNotNegative();
        assertThat(history.getRequestJson()).contains("Explain interfaces");
    }
}
