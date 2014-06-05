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
     * List of property values for a given entity and for the current user.
     *
     * @param entity Entity
     * @return List of properties for this entity
     */
    List<Property<?>> getProperties(ProjectEntity entity);

    /**
     * Gets the list of editable properties for a given entity and for the current user.
     *
     * @param entity Entity
     * @return List of editable properties for this entity
     */
    List<PropertyTypeDescriptor> getEditableProperties(ProjectEntity entity);
}
