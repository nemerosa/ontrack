package net.nemerosa.ontrack.extension.scm.service;

import net.nemerosa.ontrack.model.structure.Branch;
import net.nemerosa.ontrack.model.structure.Project;

import java.util.Optional;

/**
 * Service used to get the SCM service associated with a branch, implemented by actual SCM extensions.
 */
public interface SCMServiceProvider {

    /**
     * Gets the SCM service associated with a branch, or empty if none is available.
     *
     * @deprecated Use the method at project level
     */
    @Deprecated
    Optional<SCMService> getScmService(Branch branch);

    /**
     * Gets the SCM service associated with a project, or empty if none is available.
     */
    Optional<SCMService> getScmService(Project project);

}
