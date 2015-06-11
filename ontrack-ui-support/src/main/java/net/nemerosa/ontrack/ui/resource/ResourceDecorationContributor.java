package net.nemerosa.ontrack.ui.resource;

import net.nemerosa.ontrack.model.structure.ProjectEntity;

/**
 * Defines a contributor for the decorations of a resource. It supports only extensions
 * on {@linkplain net.nemerosa.ontrack.model.structure.ProjectEntity project entities}.
 */
public interface ResourceDecorationContributor {

    /**
     * Decorates the project entity
     *
     * @param linksBuilder  Link decorator
     * @param projectEntity Entity to decorate
     */
    void contribute(LinksBuilder linksBuilder, ProjectEntity projectEntity);

}
