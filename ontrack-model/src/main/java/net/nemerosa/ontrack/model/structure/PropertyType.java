package net.nemerosa.ontrack.model.structure;

import com.fasterxml.jackson.databind.JsonNode;
import net.nemerosa.ontrack.model.extension.Extension;
import net.nemerosa.ontrack.model.form.Form;
import net.nemerosa.ontrack.model.security.SecurityService;
import org.apache.commons.lang3.StringUtils;
import org.jetbrains.annotations.Nullable;

import java.util.Set;
import java.util.function.Function;

/**
 * Defines the type for a property.
 *
 * @param <T> Type of object supported by this type
 */
public interface PropertyType<T> extends Extension {

    /**
     * Display name for this property
     */
    String getName();

    /**
     * Description for this property
     */
    String getDescription();

    /**
     * List of entity types this property applies to.
     */
    Set<ProjectEntityType> getSupportedEntityTypes();

    /**
     * Edition policy.
     * <p>
     * Can this property be directly edited by a used on the given
     * associated entity.
     *
     * @param entity          Entity where to edit the property
     * @param securityService The access to the security layer
     * @return Authorization policy for this entity
     */
    boolean canEdit(ProjectEntity entity, SecurityService securityService);

    /**
     * Defines the authorization policy for viewing this property.
     *
     * @param entity          Entity where to view the property
     * @param securityService The access to the security layer
     * @return Authorization policy for this entity
     */
    boolean canView(ProjectEntity entity, SecurityService securityService);

    /**
     * Form to create/update this property.
     *
     * @param entity Entity to edit the property for
     * @param value  Value to update if set. If not set, this means the creation of a new property.
     */
    Form getEditionForm(ProjectEntity entity, T value);

    /**
     * Creation of a property value from a value. Should perform validation.
     */
    Property<T> of(T value);

    /**
     * Gets the JSON representation of a property value
     */
    JsonNode forStorage(T value);

    /**
     * Parses the client JSON representation for a property value. This method
     * <i>MUST</i> perform validation before accepting the value.
     *
     * @see #getEditionForm(ProjectEntity, Object)
     */
    T fromClient(JsonNode node);

    /**
     * Parses the storage JSON representation for a property value.
     */
    T fromStorage(JsonNode node);

    /**
     * Parses the JSON representation for a property value and creates the property value directly.
     *
     * @see #fromStorage(JsonNode)
     * @see #of(Object)
     */
    default Property<T> of(JsonNode node) {
        return of(fromStorage(node));
    }

    /**
     * Replaces a value by another one by transforming each string of the value into another one.
     *
     * @param value               Value to replace
     * @param replacementFunction Replacement function to used to transform each string into a new one
     * @return Transformed value
     */
    T replaceValue(T value, Function<String, String> replacementFunction);

    /**
     * Checks if the property <code>value</code> contains the given search token.
     *
     * @param value         Value to search into
     * @param propertyValue Search token
     * @return <code>true</code> is found
     */
    default boolean containsValue(T value, String propertyValue) {
        return StringUtils.containsIgnoreCase(value.toString(), propertyValue);
    }

    /**
     * Type name for this property type.
     */
    default String getTypeName() {
        return getClass().getName();
    }

    /**
     * Copy/clones the {@code value} defined for the {@code sourceEntity} for being suitable
     * in the {@code targetEntity} after applying the replacement function.
     * <p>
     * By default, just applies the textual replacements.
     *
     * @param sourceEntity  Owner of the property to copy
     * @param value         Property value to copy
     * @param targetEntity  Entity to associate the new property with
     * @param replacementFn Replacement function for textual values
     * @see #replaceValue(Object, Function)
     */
    default T copy(ProjectEntity sourceEntity, T value, ProjectEntity targetEntity, Function<String, String> replacementFn) {
        return replaceValue(value, replacementFn);
    }

    /**
     * This method is called when the property value changes (created or updated) for an entity
     *
     * @param entity Entity for which the property is changed
     * @param value  New value
     */
    default void onPropertyChanged(ProjectEntity entity, T value) {
    }

    /**
     * This method is called when the property is deleted for an entity
     *
     * @param entity   Entity for which the property is deleted
     * @param oldValue Old value
     */
    default void onPropertyDeleted(ProjectEntity entity, T oldValue) {
    }

    /**
     * Gets the additional SQL criteria to add to a search.
     *
     * @param token Token to look for this property type
     * @return Search arguments (or <code>null</code> if no search is possible)
     * @see PropertySearchArguments
     */
    @Nullable
    default PropertySearchArguments getSearchArguments(String token) {
        return null;
    }
}
