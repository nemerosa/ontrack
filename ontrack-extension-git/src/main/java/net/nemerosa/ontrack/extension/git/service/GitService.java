package net.nemerosa.ontrack.extension.git.service;

import net.nemerosa.ontrack.model.structure.Branch;

public interface GitService {

    /**
     * Tests if a branch is correctly configured for Git.
     */
    boolean isBranchConfiguredForGit(Branch branch);

}
