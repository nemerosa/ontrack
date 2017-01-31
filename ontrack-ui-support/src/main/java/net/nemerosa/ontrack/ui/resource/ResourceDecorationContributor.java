package net.nemerosa.ontrack.ui.resource;

import net.nemerosa.ontrack.model.structure.ProjectEntity;
import net.nemerosa.ontrack.model.structure.ProjectEntityType;

import java.util.List;

/**
 * Defines a contributor for the decorations of a resource. It supports only extensions
 * on {@linkplain net.nemerosa.ontrack.model.structure.ProjectEntity project entities}.
 */
public interface ResourceDecorationContributor<P extends ProjectEntity> {

    /**
     * Gets the link definitions for a class of project entity
     *
     * @return List of link definitions
     */
    List<LinkDefinition<P>> getLinkDefinitions();

    /**
     * Does this contributor applies to the given project entity class?
     *
     * @param projectEntityType Project entity type to get the link definitions for
     * @return true if applicable
     */
    boolean applyTo(ProjectEntityType projectEntityType);
}
