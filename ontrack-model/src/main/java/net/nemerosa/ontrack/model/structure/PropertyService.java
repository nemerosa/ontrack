package net.nemerosa.ontrack.model.structure;

import com.fasterxml.jackson.databind.JsonNode;
import net.nemerosa.ontrack.model.Ack;
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
     * Gets the edition form for a given property for an entity. The content of the form may be filled or not,
     * according to the fact if the property is actually set for this entity or not. If the property is not
     * opened for edition, the call could be rejected with an authorization exception.
     *
     * @param entity           Entity to get the edition form for
     * @param propertyTypeName Fully qualified name of the property to get the form for
     * @return An edition form to be used by the client
     */
    Form getPropertyEditionForm(ProjectEntity entity, String propertyTypeName);

    /**
     * Gets the value for a given property for an entity. If the property is not set, a non-null
     * {@link net.nemerosa.ontrack.model.structure.Property} is returned but is marked as
     * {@linkplain net.nemerosa.ontrack.model.structure.Property#isEmpty() empty}.
     * If the property is not opened for viewing, the call could be rejected with an
     * authorization exception.
     *
     * @param entity           Entity to get the edition form for
     * @param propertyTypeName Fully qualified name of the property to get the form for
     * @return A response that defines the property
     */
    <T> Property<T> getProperty(ProjectEntity entity, String propertyTypeName);

    /**
     * Edits the value of a property.
     *
     * @param entity           Entity to edit
     * @param propertyTypeName Fully qualified name of the property to edit
     * @param data             Raw JSON data for the property value
     */
    Ack editProperty(ProjectEntity entity, String propertyTypeName, JsonNode data);

    /**
     * Deletes the value of a property.
     *
     * @param entity Type      of the entity to edit
     * @param propertyTypeName Fully qualified name of the property to delete
     */
    Ack deleteProperty(ProjectEntity entity, String propertyTypeName);
}
