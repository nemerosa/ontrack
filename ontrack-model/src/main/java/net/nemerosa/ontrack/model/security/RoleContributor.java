package net.nemerosa.ontrack.model.security;

import java.util.Collections;
import java.util.List;
import java.util.Map;

/**
 * Components which contributes some functions to a role.
 */
public interface RoleContributor {

    /**
     * Gets a list of global roles to add.
     */
    default List<RoleDefinition> getGlobalRoles() {
        return Collections.emptyList();
    }

    /**
     * Gets a list of project roles to add.
     */
    default List<RoleDefinition> getProjectRoles() {
        return Collections.emptyList();
    }

    /**
     * Gets the list of functions to contribute per global role.
     *
     * @return List of {@link GlobalFunction} per role - none of them must be annotated with the
     * {@link CoreFunction} annotation.
     */
    default Map<String, List<Class<? extends GlobalFunction>>> getGlobalFunctionContributionsForGlobalRoles() {
        return Collections.emptyMap();
    }

    /**
     * Gets the list of project functions to contribute per global role.
     *
     * @return List of {@link ProjectFunction} per role - none of them must be annotated with the
     * {@link CoreFunction} annotation.
     */
    default Map<String, List<Class<? extends ProjectFunction>>> getProjectFunctionContributionsForGlobalRoles() {
        return Collections.emptyMap();
    }

    /**
     * Gets the list of functions to contribute per project role.
     *
     * @return List of {@link ProjectFunction} per role - none of them must be annotated with the
     * {@link CoreFunction} annotation.
     */
    default Map<String, List<Class<? extends ProjectFunction>>> getProjectFunctionContributionsForProjectRoles() {
        return Collections.emptyMap();
    }

}
