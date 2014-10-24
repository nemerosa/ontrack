package net.nemerosa.ontrack.model.support;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import net.nemerosa.ontrack.json.ObjectMapperFactory;
import net.nemerosa.ontrack.model.exceptions.TemplateSynchronisationSourceConfigParseException;
import net.nemerosa.ontrack.model.structure.TemplateSynchronisationSource;

public abstract class AbstractTemplateSynchronisationSource<T> implements TemplateSynchronisationSource<T> {

    protected final ObjectMapper objectMapper = ObjectMapperFactory.create();
    private final Class<T> configType;

    protected AbstractTemplateSynchronisationSource(Class<T> configType) {
        this.configType = configType;
    }

    @Override
    public T parseConfig(JsonNode node) {
        try {
            return objectMapper.treeToValue(node, configType);
        } catch (JsonProcessingException e) {
            throw new TemplateSynchronisationSourceConfigParseException(e);
        }
    }

    @Override
    public JsonNode forStorage(T config) {
        return objectMapper.valueToTree(config);
    }
}
