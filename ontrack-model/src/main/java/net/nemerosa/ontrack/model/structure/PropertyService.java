package net.nemerosa.ontrack.model.structure;

import net.nemerosa.ontrack.model.form.Form;

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

    /**
     * Gets the edition form for a given property for an entity. The content of the form may be filled or not,
     * according to the fact if the property is actually set for this entity or not. If the property is not
     * opened for edition, the call could be rejected with an authorization exception.
     *
     * @param entity           Entity to get the edition form for
     * @param propertyTypeName Fully qualified name of the property to get the form for
     * @return An edition form to be used by the client
     */
    Form getPropertyEditionForm(ProjectEntity entity, String propertyTypeName);
}
