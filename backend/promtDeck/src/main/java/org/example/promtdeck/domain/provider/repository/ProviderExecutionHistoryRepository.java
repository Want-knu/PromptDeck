package org.example.promtdeck.domain.provider.repository;

import org.example.promtdeck.domain.organization.entity.Organization;
import org.example.promtdeck.domain.provider.entity.ProviderExecutionHistory;
import org.example.promtdeck.domain.user.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ProviderExecutionHistoryRepository extends JpaRepository<ProviderExecutionHistory, Long> {

    List<ProviderExecutionHistory> findAllByUserOrderByCreatedAtDesc(User user);

    List<ProviderExecutionHistory> findAllByOrganizationOrderByCreatedAtDesc(Organization organization);
}
