package org.example.promtdeck.domain.organization.repository;

import org.example.promtdeck.domain.organization.entity.Organization;
import org.example.promtdeck.domain.organization.entity.OrganizationMember;
import org.example.promtdeck.domain.user.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface OrganizationMemberRepository extends JpaRepository<OrganizationMember, Long> {

    boolean existsByOrganizationAndUser(Organization organization, User user);

    List<OrganizationMember> findAllByUser(User user);
}
