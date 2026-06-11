package org.example.promtdeck.domain.provider.repository;

import org.example.promtdeck.domain.provider.entity.ProviderSetting;
import org.example.promtdeck.domain.user.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface ProviderSettingRepository extends JpaRepository<ProviderSetting, Long> {

    List<ProviderSetting> findAllByUser(User user);

    Optional<ProviderSetting> findByIdAndUser(Long id, User user);
}
