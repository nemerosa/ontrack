package net.nemerosa.ontrack.extension.git.service;

import net.nemerosa.ontrack.model.Ack;
import net.nemerosa.ontrack.model.structure.Branch;
import net.nemerosa.ontrack.model.structure.ID;

public interface GitService {

    /**
     * Tests if a branch is correctly configured for Git.
     */
    boolean isBranchConfiguredForGit(Branch branch);

    /**
     * Launches the build/tag synchronisation for a branch
     */
    Ack launchBuildSync(ID branchId);
}
