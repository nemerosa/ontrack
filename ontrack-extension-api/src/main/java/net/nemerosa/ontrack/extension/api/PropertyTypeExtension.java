package net.nemerosa.ontrack.extension.api;

import net.nemerosa.ontrack.model.structure.PropertyType;

/**
 * This extension allows the definition of a property.
 */
public interface PropertyTypeExtension extends Extension {

    /**
     * Property type defined by this extension.
     *
     * @param <T> Expected type for the property (note that this only about getting
     *            the expected type at client side of this method call - requesting
     *            the wrong type would still cause a {@link java.lang.ClassCastException}).
     */
    <T> PropertyType<T> getPropertyType();

}
