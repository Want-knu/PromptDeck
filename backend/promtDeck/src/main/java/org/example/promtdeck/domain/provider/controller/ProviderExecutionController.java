package org.example.promtdeck.domain.provider.controller;

import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.example.promtdeck.domain.provider.dto.request.ProviderExecuteRequest;
import org.example.promtdeck.domain.provider.dto.response.ProviderExecutionHistoryResponse;
import org.example.promtdeck.domain.provider.dto.response.ProviderExecuteResponse;
import org.example.promtdeck.domain.provider.dto.response.ProviderPreviewResponse;
import org.example.promtdeck.domain.provider.service.RequestExecutionService;
import org.example.promtdeck.global.common.ApiResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/provider-executions")
@RequiredArgsConstructor
@SecurityRequirement(name = "bearerAuth")
public class ProviderExecutionController {

    private final RequestExecutionService requestExecutionService;

    @GetMapping("/history")
    public ResponseEntity<ApiResponse<java.util.List<ProviderExecutionHistoryResponse>>> findHistory(
            @AuthenticationPrincipal Long userId,
            @RequestParam(required = false) Long organizationId
    ) {
        java.util.List<ProviderExecutionHistoryResponse> response = requestExecutionService.findHistory(userId, organizationId);

        return ResponseEntity.ok(
                ApiResponse.success("Provider 실행 이력을 조회했습니다.", response)
        );
    }

    @PostMapping
    public ResponseEntity<ApiResponse<ProviderExecuteResponse>> execute(
            @AuthenticationPrincipal Long userId,
            @Valid @RequestBody ProviderExecuteRequest request
    ) {
        ProviderExecuteResponse response = requestExecutionService.execute(userId, request);

        return ResponseEntity.ok(
                ApiResponse.success("Provider 요청 실행이 완료되었습니다.", response)
        );
    }

    @PostMapping("/preview")
    public ResponseEntity<ApiResponse<ProviderPreviewResponse>> preview(
            @AuthenticationPrincipal Long userId,
            @Valid @RequestBody ProviderExecuteRequest request
    ) {
        ProviderPreviewResponse response = requestExecutionService.preview(userId, request);

        return ResponseEntity.ok(
                ApiResponse.success("Provider 요청 미리보기가 생성되었습니다.", response)
        );
    }
}
