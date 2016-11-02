package net.nemerosa.ontrack.ui.resource;

import net.nemerosa.ontrack.model.structure.ProjectEntity;

/**
 * Service used to register all {@link ResourceDecorationContributor} instances.
 */
public interface ResourceDecorationContributorService {

    /**
     * Decorates the project entity
     *
     * @param linksBuilder  Link decorator
     * @param projectEntity Entity to decorate
     */
    @Deprecated
    void contribute(LinksBuilder linksBuilder, ProjectEntity projectEntity);

}
