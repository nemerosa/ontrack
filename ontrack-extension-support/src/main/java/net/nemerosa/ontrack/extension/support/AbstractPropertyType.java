package net.nemerosa.ontrack.extension.support;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import net.nemerosa.ontrack.json.ObjectMapperFactory;
import net.nemerosa.ontrack.model.exceptions.PropertyTypeStorageReadException;
import net.nemerosa.ontrack.model.structure.Property;
import net.nemerosa.ontrack.model.structure.PropertyType;

public abstract class AbstractPropertyType<T> implements PropertyType<T> {

    private static final ObjectMapper mapper = ObjectMapperFactory.create();

    @Override
    public Property<T> of(T value) {
        validate(value);
        return Property.of(this, value);
    }

    @Override
    public JsonNode forStorage(T value) {
        return mapper.valueToTree(value);
    }

    protected abstract void validate(T value);

    protected static <V> V parse(JsonNode node, Class<V> type) {
        try {
            return mapper.treeToValue(node, type);
        } catch (JsonProcessingException e) {
            throw new PropertyTypeStorageReadException(type, e);
        }
    }
}
