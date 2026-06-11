package org.example.promtdeck.domain.provider.controller;

import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.example.promtdeck.domain.provider.dto.request.ProviderKeyCreateRequest;
import org.example.promtdeck.domain.provider.dto.request.ProviderKeyUpdateRequest;
import org.example.promtdeck.domain.provider.dto.response.ProviderKeyResponse;
import org.example.promtdeck.domain.provider.service.ProviderKeyService;
import org.example.promtdeck.global.common.ApiResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/provider-keys")
@RequiredArgsConstructor
@SecurityRequirement(name = "bearerAuth")
public class ProviderKeyController {

    private final ProviderKeyService providerKeyService;

    @PostMapping
    public ResponseEntity<ApiResponse<ProviderKeyResponse>> create(
            @AuthenticationPrincipal Long userId,
            @Valid @RequestBody ProviderKeyCreateRequest request
    ) {
        ProviderKeyResponse response = providerKeyService.create(userId, request);

        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success("Provider API Key가 저장되었습니다.", response));
    }

    @GetMapping
    public ResponseEntity<ApiResponse<List<ProviderKeyResponse>>> findAll(
            @AuthenticationPrincipal Long userId
    ) {
        List<ProviderKeyResponse> response = providerKeyService.findAll(userId);

        return ResponseEntity.ok(
                ApiResponse.success("Provider API Key 목록을 조회했습니다.", response)
        );
    }

    @GetMapping("/{providerKeyId}")
    public ResponseEntity<ApiResponse<ProviderKeyResponse>> findOne(
            @AuthenticationPrincipal Long userId,
            @PathVariable Long providerKeyId
    ) {
        ProviderKeyResponse response = providerKeyService.findOne(userId, providerKeyId);

        return ResponseEntity.ok(
                ApiResponse.success("Provider API Key를 조회했습니다.", response)
        );
    }

    @PutMapping("/{providerKeyId}")
    public ResponseEntity<ApiResponse<ProviderKeyResponse>> update(
            @AuthenticationPrincipal Long userId,
            @PathVariable Long providerKeyId,
            @Valid @RequestBody ProviderKeyUpdateRequest request
    ) {
        ProviderKeyResponse response = providerKeyService.update(userId, providerKeyId, request);

        return ResponseEntity.ok(
                ApiResponse.success("Provider API Key가 수정되었습니다.", response)
        );
    }

    @DeleteMapping("/{providerKeyId}")
    public ResponseEntity<ApiResponse<Void>> delete(
            @AuthenticationPrincipal Long userId,
            @PathVariable Long providerKeyId
    ) {
        providerKeyService.delete(userId, providerKeyId);

        return ResponseEntity.ok(
                ApiResponse.success("Provider API Key가 삭제되었습니다.")
        );
    }
}
