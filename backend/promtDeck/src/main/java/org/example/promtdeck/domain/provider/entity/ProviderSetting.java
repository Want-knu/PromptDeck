package org.example.promtdeck.domain.provider.entity;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.example.promtdeck.domain.provider.type.AuthType;
import org.example.promtdeck.domain.provider.type.HttpMethodType;
import org.example.promtdeck.domain.provider.type.ProviderType;
import org.example.promtdeck.domain.user.entity.User;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "provider_settings")
public class ProviderSetting {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Version
    @Column(nullable = false)
    private Long version;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 30)
    private ProviderType providerType;

    @Column(nullable = false, length = 50)
    private String displayName;

    @Column(nullable = false, length = 100)
    private String model;

    @Column(nullable = false, length = 500)
    private String endpoint;

    @Enumerated(EnumType.STRING)
    @Column(length = 10)
    private HttpMethodType method;

    @Enumerated(EnumType.STRING)
    @Column(length = 30)
    private AuthType authType;

    @Column(length = 100)
    private String authHeaderName;

    @Column(length = 100)
    private String authQueryParamName;

    @Column(columnDefinition = "TEXT")
    private String headersJson;

    @Column(columnDefinition = "TEXT")
    private String queryParamsJson;

    @Column(columnDefinition = "TEXT")
    private String bodyTemplateJson;

    @Column(length = 300)
    private String responsePath;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    private ProviderSetting(
            ProviderType providerType,
            String displayName,
            String model,
            String endpoint,
            HttpMethodType method,
            AuthType authType,
            String authHeaderName,
            String authQueryParamName,
            String headersJson,
            String queryParamsJson,
            String bodyTemplateJson,
            String responsePath,
            User user
    ) {
        this.providerType = providerType;
        this.displayName = displayName;
        this.model = model;
        this.endpoint = endpoint;
        this.method = method;
        this.authType = authType;
        this.authHeaderName = authHeaderName;
        this.authQueryParamName = authQueryParamName;
        this.headersJson = headersJson;
        this.queryParamsJson = queryParamsJson;
        this.bodyTemplateJson = bodyTemplateJson;
        this.responsePath = responsePath;
        this.user = user;
    }

    public static ProviderSetting create(
            ProviderType providerType,
            String displayName,
            String model,
            String endpoint,
            HttpMethodType method,
            AuthType authType,
            String authHeaderName,
            String authQueryParamName,
            String headersJson,
            String queryParamsJson,
            String bodyTemplateJson,
            String responsePath,
            User user
    ) {
        return new ProviderSetting(
                providerType,
                displayName,
                model,
                endpoint,
                method,
                authType,
                authHeaderName,
                authQueryParamName,
                headersJson,
                queryParamsJson,
                bodyTemplateJson,
                responsePath,
                user
        );
    }

    public void update(
            String displayName,
            String model,
            String endpoint,
            HttpMethodType method,
            AuthType authType,
            String authHeaderName,
            String authQueryParamName,
            String headersJson,
            String queryParamsJson,
            String bodyTemplateJson,
            String responsePath
    ) {
        this.displayName = displayName;
        this.model = model;
        this.endpoint = endpoint;
        this.method = method;
        this.authType = authType;
        this.authHeaderName = authHeaderName;
        this.authQueryParamName = authQueryParamName;
        this.headersJson = headersJson;
        this.queryParamsJson = queryParamsJson;
        this.bodyTemplateJson = bodyTemplateJson;
        this.responsePath = responsePath;
    }
}
