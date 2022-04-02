package net.nemerosa.ontrack.ui.resource;

import net.nemerosa.ontrack.model.security.GlobalFunction;
import net.nemerosa.ontrack.model.security.ProjectFunction;
import net.nemerosa.ontrack.model.structure.ProjectEntity;
import net.nemerosa.ontrack.ui.controller.EntityURIBuilder;

import java.net.URI;

public interface ResourceContext {

    /**
     * @see EntityURIBuilder#build(Object)
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

    /**
     * Checks if the <code>fn</code> function is granted for the current user
     * for the <code>projectEntity</code>'s project.
     */
    default boolean isProjectFunctionGranted(ProjectEntity projectEntity, Class<? extends ProjectFunction> fn) {
        return isProjectFunctionGranted(projectEntity.projectId(), fn);
    }

    /**
     * Checks if the <code>fn</code> function is granted for the current user.
     */
    boolean isGlobalFunctionGranted(Class<? extends GlobalFunction> fn);

    /**
     * Checks if the current user is logged.
     */
    boolean isLogged();
}
