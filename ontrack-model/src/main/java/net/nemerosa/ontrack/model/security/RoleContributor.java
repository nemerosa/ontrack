package net.nemerosa.ontrack.model.security;

import java.util.Collections;
import java.util.List;

/**
 * Components which contributes some functions to a role.
 */
public interface RoleContributor {

    /**
     * Gets the list of functions to contribute to this global role.
     *
     * @param role Role to contribute to
     * @return List of {@link GlobalFunction} to add - none of them must be annotated with the
     * {@link CoreFunction} annotation.
     */
    default List<Class<? extends GlobalFunction>> getGlobalFunctionContributionsForGlobalRole(String role) {
        return Collections.emptyList();
    }

    /**
     * Gets the list of project functions to contribute to this global role.
     *
     * @param role Role to contribute to
     * @return List of {@link ProjectFunction} to add - none of them must be annotated with the
     * {@link CoreFunction} annotation.
     */
    default List<Class<? extends ProjectFunction>> getProjectFunctionContributionsForGlobalRole(String role) {
        return Collections.emptyList();
    }

    /**
     * Gets the list of functions to contribute to this project role.
     *
     * @param role Role to contribute to
     * @return List of {@link ProjectFunction} to add - none of them must be annotated with the
     * {@link CoreFunction} annotation.
     */
    default List<Class<? extends ProjectFunction>> getProjectFunctionContributionsForProjectRole(String role) {
        return Collections.emptyList();
    }

}
