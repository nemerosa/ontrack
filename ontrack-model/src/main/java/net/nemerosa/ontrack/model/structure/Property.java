package net.nemerosa.ontrack.model.structure;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.Collections;
import java.util.Map;
import java.util.Optional;

/**
 * Property value, associated with its type.
 */
@Data
@AllArgsConstructor(access = AccessLevel.PROTECTED)
public class Property<T> {

    /**
     * Type for this property
     */
    @JsonIgnore
    private final PropertyType<T> type;

    /**
     * Value for this property
     */
    private final T value;

    /**
     * Editable status
     */
    private final boolean editable;

    /**
     * Additional decorations
     */
    private final Map<String,?> decorations;

    /**
     * Descriptor for the property type
     */
    public PropertyTypeDescriptor getTypeDescriptor() {
        return PropertyTypeDescriptor.of(type);
    }

    /**
     * Empty indicator
     */
    public boolean isEmpty() {
        return value == null;
    }

    /**
     * List of additional decorations for the property
     */
    public Map<String, ?> getDecorations() {
        return decorations;
    }

    /**
     * As an option
     */
    public Optional<T> option() {
        return Optional.ofNullable(value);
    }

    /**
     * Editable property
     */
    public Property<T> editable(boolean editable) {
        return new Property<>(type, value, editable, decorations);
    }

    public static <T> Property<T> empty(PropertyType<T> type) {
        return new Property<>(type, null, false, Collections.emptyMap());
    }

    public static <T> Property<T> of(PropertyType<T> type, T value) {
        return new Property<>(type, value, false, Collections.emptyMap());
    }

    public static <T> Property<T> of(PropertyType<T> type, T value, Map<String,?> decorations) {
        return new Property<>(type, value, false, decorations);
    }

    public boolean containsValue(String propertyValue) {
        return value != null && type.containsValue(value, propertyValue);
    }
}
