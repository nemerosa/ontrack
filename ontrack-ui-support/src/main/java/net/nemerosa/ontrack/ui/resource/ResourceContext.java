package net.nemerosa.ontrack.ui.resource;

import net.nemerosa.ontrack.model.security.ProjectFunction;

import java.net.URI;

public interface ResourceContext {

    /**
     * @see net.nemerosa.ontrack.ui.controller.URIBuilder#build(Object)
     */
    URI uri(Object methodInvocation);

    /**
     * Gets a builder for links
     */
    LinksBuilder links();

    /**
     * Checks if the <code>fn</code> function is granted for the current user
     * for the <code>projectId</code> project.
     */
    boolean isProjectFunctionGranted(int projectId, Class<? extends ProjectFunction> fn);
}
