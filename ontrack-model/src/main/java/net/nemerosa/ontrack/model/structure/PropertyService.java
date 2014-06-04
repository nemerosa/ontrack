package net.nemerosa.ontrack.model.structure;

import java.util.List;

/**
 * Management of properties.
 */
public interface PropertyService {

    /**
     * List of all property types
     */
    List<PropertyType<?>> getPropertyTypes();

    /**
     * List of property values for an entity
     */
    List<Property<?>> getProperties(Entity entity);

}
