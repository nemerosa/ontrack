package net.nemerosa.ontrack.extension.support;

import com.fasterxml.jackson.databind.JsonNode;
import net.nemerosa.ontrack.json.JsonParseException;
import net.nemerosa.ontrack.json.JsonUtils;
import net.nemerosa.ontrack.model.exceptions.PropertyTypeStorageReadException;
import net.nemerosa.ontrack.model.exceptions.PropertyValidationException;
import net.nemerosa.ontrack.model.extension.ExtensionFeature;
import net.nemerosa.ontrack.model.structure.Property;
import net.nemerosa.ontrack.model.structure.PropertyType;
import org.apache.commons.lang3.StringUtils;

public abstract class AbstractPropertyType<T> implements PropertyType<T> {

    private final ExtensionFeature extensionFeature;

    protected AbstractPropertyType(ExtensionFeature extensionFeature) {
        this.extensionFeature = extensionFeature;
    }

    @Override
    public ExtensionFeature getFeature() {
        return extensionFeature;
    }

    @Override
    public Property<T> of(T value) {
        return Property.of(this, value);
    }

    @Override
    public JsonNode forStorage(T value) {
        return format(value);
    }

    protected static <V> V parse(JsonNode node, Class<V> type) {
        try {
            return JsonUtils.parse(node, type);
        } catch (JsonParseException e) {
            throw new PropertyTypeStorageReadException(type, e);
        }
    }

    protected static JsonNode format(Object value) {
        return JsonUtils.format(value);
    }

    protected void validateNotBlank(String value, String message) {
        if (StringUtils.isBlank(value)) {
            throw new PropertyValidationException(message);
        }
    }
}
