package net.nemerosa.ontrack.ui.controller;

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
}
