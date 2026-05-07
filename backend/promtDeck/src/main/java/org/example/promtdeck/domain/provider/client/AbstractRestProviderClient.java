package org.example.promtdeck.domain.provider.client;

import lombok.RequiredArgsConstructor;
import org.example.promtdeck.domain.provider.dto.request.ProviderHttpRequest;
import org.example.promtdeck.domain.provider.dto.response.ProviderExecuteResponse;
import org.example.promtdeck.domain.provider.entity.ProviderSetting;
import org.example.promtdeck.domain.provider.type.HttpMethodType;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.RestClientResponseException;

@RequiredArgsConstructor
public abstract class AbstractRestProviderClient implements ProviderClient {

    private final RestClient restClient;

    @Override
    public ProviderExecuteResponse execute(ProviderSetting setting, ProviderHttpRequest request) {
        try {
            RestClient.RequestBodySpec spec = restClient.method(HttpMethod.valueOf(request.method().name()))
                    .uri(request.endpoint())
                    .headers(headers -> request.headers().forEach(headers::set));

            ResponseEntity<String> response;

            if (supportsBody(request.method()) && request.body() != null) {
                response = spec.body(request.body())
                        .retrieve()
                        .toEntity(String.class);
            } else {
                response = spec.retrieve()
                        .toEntity(String.class);
            }

            return new ProviderExecuteResponse(
                    supports(),
                    setting.getModel(),
                    response.getStatusCode().value(),
                    response.getStatusCode().is2xxSuccessful(),
                    response.getBody(),
                    null
            );
        } catch (RestClientResponseException e) {
            return new ProviderExecuteResponse(
                    supports(),
                    setting.getModel(),
                    e.getStatusCode().value(),
                    false,
                    e.getResponseBodyAsString(),
                    e.getMessage()
            );
        } catch (Exception e) {
            return new ProviderExecuteResponse(
                    supports(),
                    setting.getModel(),
                    500,
                    false,
                    null,
                    e.getMessage()
            );
        }
    }

    private boolean supportsBody(HttpMethodType method) {
        return method == HttpMethodType.POST
                || method == HttpMethodType.PUT
                || method == HttpMethodType.PATCH;
    }
}
