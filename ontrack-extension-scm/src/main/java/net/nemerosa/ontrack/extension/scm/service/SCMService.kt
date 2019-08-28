package net.nemerosa.ontrack.extension.scm.service;

import net.nemerosa.ontrack.extension.scm.model.SCMPathInfo;
import net.nemerosa.ontrack.model.structure.Branch;

import java.util.Optional;

/**
 * Common methods for the SCM accesses
 */
public interface SCMService {

    /**
     * Downloads the file at the given path for a branch
     */
    Optional<String> download(Branch branch, String path);

    /**
     * Gets the SCM path info of a branch
     */
    Optional<SCMPathInfo> getSCMPathInfo(Branch branch);

}
