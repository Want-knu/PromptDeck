package org.example.promtdeck.domain.organization.dto.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

public record OrganizationMemberAddRequest(
        @NotBlank
        @Email
        String email
) {
}
