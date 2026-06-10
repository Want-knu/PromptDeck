package org.example.promtdeck.domain.provider.definition;

import org.example.promtdeck.domain.provider.dto.response.ProviderSettingOptionsResponse;
import org.example.promtdeck.domain.provider.type.ProviderType;
import org.example.promtdeck.global.common.ErrorCode;
import org.example.promtdeck.global.exception.CustomException;
import org.springframework.stereotype.Component;

import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

@Component
public class ProviderDefinitionRegistry {

    private static final List<ProviderType> PROVIDER_ORDER = List.of(
            ProviderType.OPENAI,
            ProviderType.GEMINI,
            ProviderType.CLAUDE,
            ProviderType.CUSTOM
    );

    private final Map<ProviderType, ProviderDefinition> definitions;

    public ProviderDefinitionRegistry(List<ProviderDefinition> definitions) {
        this.definitions = definitions.stream()
                .sorted(Comparator.comparingInt(definition -> providerOrder(definition.type())))
                .collect(Collectors.toMap(
                        ProviderDefinition::type,
                        Function.identity(),
                        (left, right) -> {
                            throw new IllegalStateException("Duplicated provider definition: " + left.type());
                        },
                        LinkedHashMap::new
                ));
    }

    public ResolvedProviderSetting resolve(ProviderSettingResolveCommand command) {
        return get(command.providerType()).resolve(command);
    }

    public ProviderSettingOptionsResponse options() {
        Map<ProviderType, ProviderSettingOptionsResponse.ProviderOption> providers = new LinkedHashMap<>();
        definitions.forEach((type, definition) -> providers.put(type, definition.options()));

        return new ProviderSettingOptionsResponse(
                definitions.keySet().stream().toList(),
                providers
        );
    }

    private ProviderDefinition get(ProviderType type) {
        ProviderDefinition definition = definitions.get(type);

        if (definition == null) {
            throw new CustomException(ErrorCode.UNSUPPORTED_PROVIDER);
        }

        return definition;
    }

    private static int providerOrder(ProviderType type) {
        int index = PROVIDER_ORDER.indexOf(type);
        return index < 0 ? Integer.MAX_VALUE : index;
    }
}
