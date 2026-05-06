package org.example.promtdeck.domain.provider.repository;

import org.example.promtdeck.domain.provider.entity.ProviderKey;
import org.example.promtdeck.domain.user.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface ProviderKeyRepository extends JpaRepository<ProviderKey, Long> {
    List<ProviderKey> findAllByUser(User user);

    Optional<ProviderKey> findByIdAndUser(Long id, User user);

}
