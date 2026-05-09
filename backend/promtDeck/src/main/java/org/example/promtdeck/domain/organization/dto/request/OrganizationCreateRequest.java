package org.example.promtdeck.domain.organization.dto.request;

import jakarta.validation.constraints.NotBlank;

public record OrganizationCreateRequest(
        @NotBlank
        String name
) {
}
