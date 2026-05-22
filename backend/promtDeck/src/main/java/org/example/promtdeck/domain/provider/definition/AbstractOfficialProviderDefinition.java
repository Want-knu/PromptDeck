package org.example.promtdeck.domain.provider.definition;

import org.example.promtdeck.domain.provider.dto.response.ProviderSettingOptionsResponse;
import org.example.promtdeck.domain.provider.type.AuthType;
import org.example.promtdeck.domain.provider.type.HttpMethodType;
import org.example.promtdeck.domain.provider.type.ProviderType;
import org.springframework.util.StringUtils;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public abstract class AbstractOfficialProviderDefinition implements ProviderDefinition {

    private final ProviderType type;
    private final String endpointLabel;
    private final String endpointTemplate;
    private final AuthType authType;
    private final String authHeaderName;
    private final String authQueryParamName;
    private final String headersJson;
    private final String optionSchemaLabel;
    private final List<ModelSpec> models;

    protected AbstractOfficialProviderDefinition(
            ProviderType type,
            String endpointLabel,
            String endpointTemplate,
            AuthType authType,
            String authHeaderName,
            String authQueryParamName,
            String headersJson,
            String optionSchemaLabel,
            List<ModelSpec> models
    ) {
        this.type = type;
        this.endpointLabel = endpointLabel;
        this.endpointTemplate = endpointTemplate;
        this.authType = authType;
        this.authHeaderName = authHeaderName;
        this.authQueryParamName = authQueryParamName;
        this.headersJson = headersJson;
        this.optionSchemaLabel = optionSchemaLabel;
        this.models = List.copyOf(models);
    }

    @Override
    public ProviderType type() {
        return type;
    }

    @Override
    public ProviderSettingOptionsResponse.ProviderOption options() {
        String defaultModel = defaultModel();
        ModelSpec defaultModelSpec = modelSpec(defaultModel);
        ProviderSettingOptionsResponse.EndpointOption endpoint = new ProviderSettingOptionsResponse.EndpointOption(
                endpointLabel,
                endpointFor(defaultModel),
                HttpMethodType.POST,
                authType,
                authHeaderName,
                authQueryParamName,
                headersJson,
                null,
                null,
                defaultModelSpec.optionSchemaJson(),
                defaultModelSpec.responsePath()
        );

        return new ProviderSettingOptionsResponse.ProviderOption(
                modelNames(),
                defaultModel,
                List.of(endpoint),
                endpoint.value(),
                List.of(HttpMethodType.POST),
                HttpMethodType.POST,
                List.of(authType),
                authType,
                authHeaderName,
                authQueryParamName,
                List.of(defaultModelSpec.responsePath()),
                defaultModelSpec.responsePath(),
                List.of(),
                List.of(new ProviderSettingOptionsResponse.NamedJsonOption(optionSchemaLabel, defaultModelSpec.optionSchemaJson())),
                modelOptions(),
                false
        );
    }

    @Override
    public ResolvedProviderSetting resolve(ProviderSettingResolveCommand command) {
        String model = StringUtils.hasText(command.model()) ? command.model() : defaultModel();
        ModelSpec modelSpec = modelSpec(model);

        return new ResolvedProviderSetting(
                endpointFor(model),
                HttpMethodType.POST,
                authType,
                authHeaderName,
                authQueryParamName,
                headersJson,
                null,
                null,
                StringUtils.hasText(command.optionSchemaJson()) ? command.optionSchemaJson() : modelSpec.optionSchemaJson(),
                modelSpec.responsePath()
        );
    }

    protected static ModelSpec model(String name, String optionSchemaJson, String responsePath) {
        return new ModelSpec(name, optionSchemaJson, responsePath);
    }

    private String defaultModel() {
        return models.get(0).name();
    }

    private List<String> modelNames() {
        return models.stream()
                .map(ModelSpec::name)
                .toList();
    }

    private ModelSpec modelSpec(String model) {
        return models.stream()
                .filter(candidate -> candidate.name().equals(model))
                .findFirst()
                .orElse(models.get(0));
    }

    private String endpointFor(String model) {
        if (endpointTemplate.contains("%s")) {
            return endpointTemplate.formatted(model);
        }

        return endpointTemplate;
    }

    private Map<String, ProviderSettingOptionsResponse.ModelOption> modelOptions() {
        Map<String, ProviderSettingOptionsResponse.ModelOption> modelOptions = new LinkedHashMap<>();
        models.forEach(model -> modelOptions.put(
                model.name(),
                new ProviderSettingOptionsResponse.ModelOption(model.optionSchemaJson(), model.responsePath())
        ));
        return modelOptions;
    }

    protected record ModelSpec(
            String name,
            String optionSchemaJson,
            String responsePath
    ) {
    }
}
