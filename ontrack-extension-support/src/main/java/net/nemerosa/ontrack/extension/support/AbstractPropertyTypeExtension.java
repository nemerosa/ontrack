package net.nemerosa.ontrack.extension.support;

import net.nemerosa.ontrack.extension.api.ExtensionFeature;
import net.nemerosa.ontrack.extension.api.PropertyTypeExtension;
import net.nemerosa.ontrack.model.structure.PropertyType;

public abstract class AbstractPropertyTypeExtension<T> extends AbstractExtension implements PropertyTypeExtension {

    private final PropertyType<T> propertyType;

    public AbstractPropertyTypeExtension(ExtensionFeature extensionFeature, PropertyType<T> propertyType) {
        super(extensionFeature);
        this.propertyType = propertyType;
    }

    @Override
    public PropertyType<T> getPropertyType() {
        return propertyType;
    }
}
