package org.example.promtdeck.domain.organization.controller;

import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.example.promtdeck.domain.organization.dto.request.OrganizationCreateRequest;
import org.example.promtdeck.domain.organization.dto.request.OrganizationMemberAddRequest;
import org.example.promtdeck.domain.organization.dto.response.OrganizationResponse;
import org.example.promtdeck.domain.organization.service.OrganizationService;
import org.example.promtdeck.global.common.ApiResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/organizations")
@RequiredArgsConstructor
@SecurityRequirement(name = "bearerAuth")
public class OrganizationController {

    private final OrganizationService organizationService;

    @PostMapping
    public ResponseEntity<ApiResponse<OrganizationResponse>> create(
            @AuthenticationPrincipal Long userId,
            @Valid @RequestBody OrganizationCreateRequest request
    ) {
        OrganizationResponse response = organizationService.create(userId, request);

        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success("조직이 생성되었습니다.", response));
    }

    @GetMapping
    public ResponseEntity<ApiResponse<List<OrganizationResponse>>> findAll(
            @AuthenticationPrincipal Long userId
    ) {
        List<OrganizationResponse> response = organizationService.findAll(userId);

        return ResponseEntity.ok(ApiResponse.success("조직 목록을 조회했습니다.", response));
    }

    @PostMapping("/{organizationId}/members")
    public ResponseEntity<ApiResponse<Void>> addMember(
            @AuthenticationPrincipal Long userId,
            @PathVariable Long organizationId,
            @Valid @RequestBody OrganizationMemberAddRequest request
    ) {
        organizationService.addMember(userId, organizationId, request);

        return ResponseEntity.ok(ApiResponse.success("조직 멤버가 추가되었습니다."));
    }
}
