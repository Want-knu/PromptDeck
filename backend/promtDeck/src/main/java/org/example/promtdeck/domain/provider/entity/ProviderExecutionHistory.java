package org.example.promtdeck.domain.provider.entity;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.example.promtdeck.domain.organization.entity.Organization;
import org.example.promtdeck.domain.provider.type.ProviderType;
import org.example.promtdeck.domain.user.entity.User;

import java.time.LocalDateTime;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "provider_execution_histories")
public class ProviderExecutionHistory {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 30)
    private ProviderType providerType;

    @Column(nullable = false, length = 100)
    private String model;

    @Column(nullable = false)
    private Long providerSettingId;

    @Column(nullable = false)
    private Long providerKeyId;

    @Column(nullable = false)
    private Integer statusCode;

    @Column(nullable = false)
    private Boolean success;

    @Column(nullable = false)
    private Long durationMs;

    @Column(columnDefinition = "TEXT")
    private String requestJson;

    @Column(columnDefinition = "TEXT")
    private String responseBody;

    @Column(columnDefinition = "TEXT")
    private String parsedResponse;

    @Column(columnDefinition = "TEXT")
    private String errorMessage;

    @Column(nullable = false)
    private LocalDateTime createdAt;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "organization_id")
    private Organization organization;

    private ProviderExecutionHistory(
            ProviderType providerType,
            String model,
            Long providerSettingId,
            Long providerKeyId,
            Integer statusCode,
            Boolean success,
            Long durationMs,
            String requestJson,
            String responseBody,
            String parsedResponse,
            String errorMessage,
            User user,
            Organization organization
    ) {
        this.providerType = providerType;
        this.model = model;
        this.providerSettingId = providerSettingId;
        this.providerKeyId = providerKeyId;
        this.statusCode = statusCode;
        this.success = success;
        this.durationMs = durationMs;
        this.requestJson = requestJson;
        this.responseBody = responseBody;
        this.parsedResponse = parsedResponse;
        this.errorMessage = errorMessage;
        this.user = user;
        this.organization = organization;
        this.createdAt = LocalDateTime.now();
    }

    public static ProviderExecutionHistory create(
            ProviderType providerType,
            String model,
            Long providerSettingId,
            Long providerKeyId,
            Integer statusCode,
            Boolean success,
            Long durationMs,
            String requestJson,
            String responseBody,
            String parsedResponse,
            String errorMessage,
            User user,
            Organization organization
    ) {
        return new ProviderExecutionHistory(
                providerType,
                model,
                providerSettingId,
                providerKeyId,
                statusCode,
                success,
                durationMs,
                requestJson,
                responseBody,
                parsedResponse,
                errorMessage,
                user,
                organization
        );
    }
}
