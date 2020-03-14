package net.nemerosa.ontrack.model.structure;

import com.fasterxml.jackson.annotation.JsonIgnore;
import org.jetbrains.annotations.Nullable;

import static org.apache.commons.lang3.Validate.isTrue;

/**
 * A <b>ProjectEntity</b> is an {@link Entity} that belongs into a {@link Project}. It has also a {@link #getDescription()}
 */
public interface ProjectEntity extends Entity {

    /**
     * Gets the description of this entity.
     */
    @Nullable
    String getDescription();

    /**
     * Returns the project this entity is associated with
     */
    @JsonIgnore
    Project getProject();

    /**
     * Returns the ID of the project that contains this entity. This method won't return <code>null</code>
     * but the ID could be {@linkplain ID#NONE undefined}.
     */
    @JsonIgnore
    default ID getProjectId() {
        return getProject().getId();
    }

    /**
     * Shortcut to get the ID as a value.
     *
     * @throws java.lang.IllegalArgumentException If the project ID is not {@linkplain ID#isSet() set}.
     */
    default int projectId() {
        ID id = getProjectId();
        isTrue(ID.isDefined(id), "Project ID must be defined");
        return id.get();
    }

    /**
     * Gets the type of entity as an enum.
     */
    @JsonIgnore
    ProjectEntityType getProjectEntityType();

    /**
     * Representation, like "Branch P/X"
     */
    @JsonIgnore
    String getEntityDisplayName();

    /**
     * Creation signature of the project entity.
     *
     * @return Creation signature for the project entity.
     */
    Signature getSignature();


}
