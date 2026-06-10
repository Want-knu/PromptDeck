package org.example.promtdeck.domain.provider.entity;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.example.promtdeck.domain.organization.entity.Organization;
import org.example.promtdeck.domain.user.entity.User;

import java.time.LocalDateTime;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "provider_execution_presets")
public class ProviderExecutionPreset {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Version
    @Column(nullable = false)
    private Long version;

    @Column(nullable = false, length = 80)
    private String displayName;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "provider_setting_id", nullable = false)
    private ProviderSetting providerSetting;

    @Column
    private Long providerKeyId;

    @Column(columnDefinition = "TEXT")
    private String prompt;

    @Column(columnDefinition = "TEXT")
    private String instructions;

    @Column(columnDefinition = "TEXT")
    private String optionsJson;

    @Column(columnDefinition = "TEXT")
    private String variablesJson;

    @Column(nullable = false)
    private LocalDateTime createdAt;

    @Column(nullable = false)
    private LocalDateTime updatedAt;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "organization_id")
    private Organization organization;

    private ProviderExecutionPreset(
            String displayName,
            ProviderSetting providerSetting,
            Long providerKeyId,
            String prompt,
            String instructions,
            String optionsJson,
            String variablesJson,
            User user,
            Organization organization
    ) {
        this.displayName = displayName;
        this.providerSetting = providerSetting;
        this.providerKeyId = providerKeyId;
        this.prompt = prompt;
        this.instructions = instructions;
        this.optionsJson = optionsJson;
        this.variablesJson = variablesJson;
        this.user = user;
        this.organization = organization;
        this.createdAt = LocalDateTime.now();
        this.updatedAt = this.createdAt;
    }

    public static ProviderExecutionPreset create(
            String displayName,
            ProviderSetting providerSetting,
            Long providerKeyId,
            String prompt,
            String instructions,
            String optionsJson,
            String variablesJson,
            User user
    ) {
        return new ProviderExecutionPreset(
                displayName,
                providerSetting,
                providerKeyId,
                prompt,
                instructions,
                optionsJson,
                variablesJson,
                user,
                providerSetting.getOrganization()
        );
    }

    public void update(
            String displayName,
            ProviderSetting providerSetting,
            Long providerKeyId,
            String prompt,
            String instructions,
            String optionsJson,
            String variablesJson
    ) {
        this.displayName = displayName;
        this.providerSetting = providerSetting;
        this.providerKeyId = providerKeyId;
        this.prompt = prompt;
        this.instructions = instructions;
        this.optionsJson = optionsJson;
        this.variablesJson = variablesJson;
        this.organization = providerSetting.getOrganization();
        this.updatedAt = LocalDateTime.now();
    }
}
