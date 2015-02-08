package net.nemerosa.ontrack.model.security;

import lombok.Data;

import java.io.Serializable;
import java.util.Set;

/**
 * A global role defines the association between a name, a set of
 * {@linkplain net.nemerosa.ontrack.model.security.GlobalFunction global functions}
 * and a set of {@linkplain net.nemerosa.ontrack.model.security.ProjectFunction project functions}
 * that are attributed for all projects.
 */
@Data
public class GlobalRole implements Serializable {

    /**
     * Global role's identifier
     */
    private final String id;

    /**
     * Global role's name
     */
    private final String name;

    /**
     * Description of the role
     */
    private final String description;

    /**
     * Global functions
     */
    private final Set<Class<? extends GlobalFunction>> globalFunctions;

    /**
     * Project functions to grant for all projects
     */
    private final Set<Class<? extends ProjectFunction>> projectFunctions;


    public boolean isGlobalFunctionGranted(Class<? extends GlobalFunction> fn) {
        return globalFunctions.contains(fn);
    }

    public boolean isProjectFunctionGranted(Class<? extends ProjectFunction> fn) {
        return projectFunctions.stream().anyMatch(fn::isAssignableFrom);
    }
}
