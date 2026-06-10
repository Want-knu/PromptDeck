package org.example.promtdeck.domain.provider.controller;

import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.example.promtdeck.domain.provider.dto.request.ProviderExecutionPresetCreateRequest;
import org.example.promtdeck.domain.provider.dto.response.ProviderExecutionPresetResponse;
import org.example.promtdeck.domain.provider.service.ProviderExecutionPresetService;
import org.example.promtdeck.global.common.ApiResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/provider-execution-presets")
@RequiredArgsConstructor
@SecurityRequirement(name = "bearerAuth")
public class ProviderExecutionPresetController {

    private final ProviderExecutionPresetService providerExecutionPresetService;

    @GetMapping
    public ResponseEntity<ApiResponse<List<ProviderExecutionPresetResponse>>> findAll(
            @AuthenticationPrincipal Long userId
    ) {
        List<ProviderExecutionPresetResponse> response = providerExecutionPresetService.findAll(userId);

        return ResponseEntity.ok(
                ApiResponse.success("실행 프리셋 목록을 조회했습니다.", response)
        );
    }

    @PostMapping
    public ResponseEntity<ApiResponse<ProviderExecutionPresetResponse>> create(
            @AuthenticationPrincipal Long userId,
            @Valid @RequestBody ProviderExecutionPresetCreateRequest request
    ) {
        ProviderExecutionPresetResponse response = providerExecutionPresetService.create(userId, request);

        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success("실행 프리셋이 저장되었습니다.", response));
    }

    @PutMapping("/{presetId}")
    public ResponseEntity<ApiResponse<ProviderExecutionPresetResponse>> update(
            @AuthenticationPrincipal Long userId,
            @PathVariable Long presetId,
            @Valid @RequestBody ProviderExecutionPresetCreateRequest request
    ) {
        ProviderExecutionPresetResponse response = providerExecutionPresetService.update(userId, presetId, request);

        return ResponseEntity.ok(
                ApiResponse.success("실행 프리셋이 수정되었습니다.", response)
        );
    }

    @DeleteMapping("/{presetId}")
    public ResponseEntity<ApiResponse<Void>> delete(
            @AuthenticationPrincipal Long userId,
            @PathVariable Long presetId
    ) {
        providerExecutionPresetService.delete(userId, presetId);

        return ResponseEntity.ok(ApiResponse.success("실행 프리셋이 삭제되었습니다."));
    }
}
