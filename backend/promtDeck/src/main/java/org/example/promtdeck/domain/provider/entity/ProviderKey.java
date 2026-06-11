package org.example.promtdeck.domain.provider.entity;

import jakarta.persistence.*;
import lombok.*;
import org.example.promtdeck.domain.provider.type.ProviderType;
import org.example.promtdeck.domain.user.entity.User;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Table(name = "provider_api_keys")
public class ProviderKey {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Version
    @Column(nullable = false)
    private Long version;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 30)
    private ProviderType providerType;

    @Column(nullable = false, length = 1000)
    private String encryptedApiKey;

    @Column(nullable = false, length = 100)
    private String maskedApiKey;

    @Column(nullable = false, length = 50)
    private String displayName;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    private ProviderKey(
            ProviderType providerType,
            String encryptedApiKey,
            String maskedApiKey,
            String displayName,
            User user
    ) {
        this.providerType = providerType;
        this.encryptedApiKey = encryptedApiKey;
        this.maskedApiKey = maskedApiKey;
        this.displayName = displayName;
        this.user = user;
    }

    public static ProviderKey create(
            ProviderType providerType,
            String encryptedApiKey,
            String maskedApiKey,
            String displayName,
            User user
    ) {
        return new ProviderKey(providerType, encryptedApiKey, maskedApiKey, displayName, user);
    }

    public void update(
            ProviderType providerType,
            String encryptedApiKey,
            String maskedApiKey,
            String displayName
    ) {
        this.providerType = providerType;
        this.encryptedApiKey = encryptedApiKey;
        this.maskedApiKey = maskedApiKey;
        this.displayName = displayName;
    }
}
