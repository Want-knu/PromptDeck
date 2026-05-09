package org.example.promtdeck.domain.organization.repository;

import org.example.promtdeck.domain.organization.entity.Organization;
import org.springframework.data.jpa.repository.JpaRepository;

public interface OrganizationRepository extends JpaRepository<Organization, Long> {
}
