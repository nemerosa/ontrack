package net.nemerosa.ontrack.extension.scm.service;

import net.nemerosa.ontrack.model.structure.Branch;

import java.util.Optional;

/**
 * Service used to get the SCM service associated with a branch.
 */
public interface SCMServiceDetector {

    /**
     * Gets the SCM service associated with a branch, or empty if none is available.
     */
    Optional<SCMService> getScmService(Branch branch);

}
