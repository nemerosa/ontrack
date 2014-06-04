package net.nemerosa.ontrack.model.structure;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Data;

/**
 * Property value, associated with its type.
 */
@Data
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

}
