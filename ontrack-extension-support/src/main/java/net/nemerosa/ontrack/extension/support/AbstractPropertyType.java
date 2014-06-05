package net.nemerosa.ontrack.extension.support;

import net.nemerosa.ontrack.model.structure.Property;
import net.nemerosa.ontrack.model.structure.PropertyType;

public abstract class AbstractPropertyType<T> implements PropertyType<T> {

    @Override
    public Property<T> of(T value) {
        validate(value);
        return Property.of(this, value);
    }

    protected abstract void validate(T value);
}
