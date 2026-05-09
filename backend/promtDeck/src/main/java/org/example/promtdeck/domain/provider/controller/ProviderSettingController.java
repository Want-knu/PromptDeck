package org.example.promtdeck.domain.provider.controller;

import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.example.promtdeck.domain.provider.dto.request.ProviderSettingCreateRequest;
import org.example.promtdeck.domain.provider.dto.request.ProviderSettingUpdateRequest;
import org.example.promtdeck.domain.provider.dto.response.ProviderSettingResponse;
import org.example.promtdeck.domain.provider.service.ProviderSettingService;
import org.example.promtdeck.global.common.ApiResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/provider-settings")
@RequiredArgsConstructor
@SecurityRequirement(name = "bearerAuth")
public class ProviderSettingController {

    private final ProviderSettingService providerSettingService;

    @PostMapping
    public ResponseEntity<ApiResponse<ProviderSettingResponse>> create(
            @AuthenticationPrincipal Long userId,
            @Valid @RequestBody ProviderSettingCreateRequest request
    ) {
        ProviderSettingResponse response = providerSettingService.create(userId, request);

        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success("Provider 설정이 저장되었습니다.", response));
    }

    @GetMapping
    public ResponseEntity<ApiResponse<List<ProviderSettingResponse>>> findAll(
            @AuthenticationPrincipal Long userId
    ) {
        List<ProviderSettingResponse> response = providerSettingService.findAll(userId);

        return ResponseEntity.ok(
                ApiResponse.success("Provider 설정 목록을 조회했습니다.", response)
        );
    }

    @GetMapping("/{providerSettingId}")
    public ResponseEntity<ApiResponse<ProviderSettingResponse>> findOne(
            @AuthenticationPrincipal Long userId,
            @PathVariable Long providerSettingId
    ) {
        ProviderSettingResponse response = providerSettingService.findOne(userId, providerSettingId);

        return ResponseEntity.ok(
                ApiResponse.success("Provider 설정을 조회했습니다.", response)
        );
    }

    @PutMapping("/{providerSettingId}")
    public ResponseEntity<ApiResponse<ProviderSettingResponse>> update(
            @AuthenticationPrincipal Long userId,
            @PathVariable Long providerSettingId,
            @Valid @RequestBody ProviderSettingUpdateRequest request
    ) {
        ProviderSettingResponse response = providerSettingService.update(userId, providerSettingId, request);

        return ResponseEntity.ok(
                ApiResponse.success("Provider 설정이 수정되었습니다.", response)
        );
    }

    @DeleteMapping("/{providerSettingId}")
    public ResponseEntity<ApiResponse<Void>> delete(
            @AuthenticationPrincipal Long userId,
            @PathVariable Long providerSettingId
    ) {
        providerSettingService.delete(userId, providerSettingId);

        return ResponseEntity.ok(
                ApiResponse.success("Provider 설정이 삭제되었습니다.")
        );
    }
}
