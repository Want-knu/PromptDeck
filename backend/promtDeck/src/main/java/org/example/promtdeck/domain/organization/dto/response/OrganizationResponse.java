package org.example.promtdeck.domain.organization.dto.response;

import org.example.promtdeck.domain.organization.entity.Organization;

public record OrganizationResponse(
        Long id,
        String name,
        Long ownerId
) {

    public static OrganizationResponse from(Organization organization) {
        return new OrganizationResponse(
                organization.getId(),
                organization.getName(),
                organization.getOwner().getId()
        );
    }
}
