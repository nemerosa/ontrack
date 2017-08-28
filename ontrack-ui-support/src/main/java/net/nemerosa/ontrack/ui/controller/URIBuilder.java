package net.nemerosa.ontrack.ui.controller;

import net.nemerosa.ontrack.model.extension.Extension;
import net.nemerosa.ontrack.model.structure.ProjectEntity;
import net.nemerosa.ontrack.model.support.Action;

import java.net.URI;

public interface URIBuilder {

    URI build(Object methodInvocation);

    /**
     * Builds the URI to a page.
     *
     * @param path      Path to the page, as a pattern
     * @param arguments Arguments for the path pattern
     * @return Full URI to the page
     */
    URI page(String path, Object... arguments);

    /**
     * Returns the access URI for an entity
     */
    URI getEntityURI(ProjectEntity entity);

    /**
     * Returns the page URI for an entity
     */
    URI getEntityPage(ProjectEntity entity);

    /**
     * Given an action with a relative URI, gets a new Action with a fully resolved URI
     */
    default Action resolveActionWithExtension(Extension extension, Action action) {
        return action.withUri(
                String.format("extension/%s/%s",
                        extension.getFeature().getId(),
                        action.getUri())
        );
    }
}
