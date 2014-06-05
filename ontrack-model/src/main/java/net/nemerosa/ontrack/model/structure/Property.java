package net.nemerosa.ontrack.model.structure;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Data;

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

    public static <T> Property<T> empty(PropertyType<T> type) {
        return new Property<>(type, null);
    }

    public static <T> Property<T> of(PropertyType<T> type, T value) {
        return new Property<>(type, value);
    }
}
