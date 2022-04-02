package net.nemerosa.ontrack.ui.controller;

import net.nemerosa.ontrack.model.structure.ProjectEntity;

import java.net.URI;

public interface URIBuilder {

    URI build(Object methodInvocation);

    /**
     * Builds an absolute URL from a relative URI.
     *
     * @param relativeUri Relative URI
     * @return Complete URL
     */
    URI url(String relativeUri);

    /**
     * Builds the URI to a page.
     *
     * @param path      Path to the page, as a pattern
     * @param arguments Arguments for the path pattern
     * @return Full URI to the page
     */
    URI page(String path, Object... arguments);

    /**
     * Returns the page URI for an entity
     */
    URI getEntityPage(ProjectEntity entity);
}
