package net.nemerosa.ontrack.model.security;

import lombok.Data;

import java.util.Set;

/**
 * A project role is the association between an identifier, a name and a set of
 * {@linkplain net.nemerosa.ontrack.model.security.ProjectFunction project functions}.
 */
@Data
public class ProjectRole {

    /**
     * Project role's identifier
     */
    private final String id;

    /**
     * Project role's name
     */
    private final String name;

    /**
     * Associated set of project functions
     */
    private final Set<Class<? extends ProjectFunction>> functions;

}
