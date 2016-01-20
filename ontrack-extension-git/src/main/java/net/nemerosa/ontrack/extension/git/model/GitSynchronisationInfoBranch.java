package net.nemerosa.ontrack.extension.git.model;

import lombok.Data;
import net.nemerosa.ontrack.git.model.GitCommit;

/**
 * Information about a branch in a Git repository.
 */
@Data
public class GitSynchronisationInfoBranch {

    /**
     * Name of the branch
     */
    private final String name;

    /**
     * Last commit on the branch
     */
    private final GitCommit commit;

}
