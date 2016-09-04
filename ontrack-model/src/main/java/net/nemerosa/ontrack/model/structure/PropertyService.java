package net.nemerosa.ontrack.model.structure;

import com.fasterxml.jackson.databind.JsonNode;
import net.nemerosa.ontrack.model.Ack;
import net.nemerosa.ontrack.model.exceptions.PropertyTypeNotFoundException;
import net.nemerosa.ontrack.model.form.Form;

import java.util.Collection;
import java.util.List;
import java.util.function.BiFunction;
import java.util.function.Function;
import java.util.function.Predicate;

/**
 * Management of properties.
 */
public interface PropertyService {

    /**
     * List of all property types
     */
    List<PropertyType<?>> getPropertyTypes();

    /**
     * Gets a property type using its name
     *
     * @param propertyTypeName Fully qualified name of the property type
     * @param <T>              Type of property
     * @return Property type
     * @throws PropertyTypeNotFoundException If not found
     */
    <T> PropertyType<T> getPropertyTypeByName(String propertyTypeName);

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
     * @param propertyTypeName Fully qualified name of the property to get the property for
     * @return A response that defines the property
     */
    <T> Property<T> getProperty(ProjectEntity entity, String propertyTypeName);

    /**
     * Same than {@link #getProperty(ProjectEntity, String)} but using the class of
     * the property type.
     *
     * @param entity            Entity to get the edition form for
     * @param propertyTypeClass Class of the property type to get the property for
     * @return A response that defines the property
     */
    <T> Property<T> getProperty(ProjectEntity entity, Class<? extends PropertyType<T>> propertyTypeClass);

    /**
     * Edits the value of a property.
     *
     * @param entity           Entity to edit
     * @param propertyTypeName Fully qualified name of the property to edit
     * @param data             Raw JSON data for the property value
     */
    Ack editProperty(ProjectEntity entity, String propertyTypeName, JsonNode data);

    /**
     * Edits the value of a property.
     *
     * @param entity       Entity to edit
     * @param propertyType The type of the property to edit
     * @param data         Property value
     */
    <T> Ack editProperty(ProjectEntity entity, Class<? extends PropertyType<T>> propertyType, T data);

    /**
     * Deletes the value of a property.
     *
     * @param entity Type of the entity to edit
     * @param propertyTypeName Fully qualified name of the property to delete
     */
    Ack deleteProperty(ProjectEntity entity, String propertyTypeName);

    /**
     * Deletes the value of a property.
     *
     * @param entity Type of the entity to edit
     * @param propertyType Class of the property to delete
     */
    default <T> Ack deleteProperty(ProjectEntity entity, Class<? extends PropertyType<T>> propertyType) {
        return deleteProperty(entity, propertyType.getName());
    }

    /**
     * Searches for all entities with the corresponding property value.
     */
    <T> Collection<ProjectEntity> searchWithPropertyValue(
            Class<? extends PropertyType<T>> propertyTypeClass,
            BiFunction<ProjectEntityType, ID, ProjectEntity> entityLoader,
            Predicate<T> predicate
    );

    /**
     * Tests if a property is defined.
     */
    default <T> boolean hasProperty(ProjectEntity entity, Class<? extends PropertyType<T>> propertyTypeClass) {
        return !getProperty(entity, propertyTypeClass).isEmpty();
    }

    /**
     * Copy/clones the {@code property} into the {@code targetEntity} after applying the replacement function.
     *
     * @param sourceEntity  Owner of the property to copy
     * @param property      Property to copy
     * @param targetEntity  Entity to associate the new property with
     * @param replacementFn Replacement function for textual values
     * @param <T>           Type of the property
     */
    <T> void copyProperty(ProjectEntity sourceEntity, Property<T> property, ProjectEntity targetEntity, Function<String, String> replacementFn);
}
