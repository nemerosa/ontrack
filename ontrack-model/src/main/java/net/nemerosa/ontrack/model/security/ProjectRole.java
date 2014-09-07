package net.nemerosa.ontrack.model.security;

import lombok.Data;

import java.io.Serializable;
import java.util.Set;

/**
 * A project role is the association between an identifier, a name and a set of
 * {@linkplain net.nemerosa.ontrack.model.security.ProjectFunction project functions}.
 */
@Data
public class ProjectRole implements Serializable {

    /**
     * Project role's identifier
     */
    private final String id;

    /**
     * Project role's name
     */
    private final String name;

    /**
     * Description
     */
    private final String description;

    /**
     * Associated set of project functions
     */
    private final Set<Class<? extends ProjectFunction>> functions;

    public boolean isGranted(Class<? extends ProjectFunction> functionToCheck) {
        return functions.stream().anyMatch(functionToCheck::isAssignableFrom);
    }
}
