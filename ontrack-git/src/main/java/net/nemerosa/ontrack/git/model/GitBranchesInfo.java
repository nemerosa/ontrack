package net.nemerosa.ontrack.git.model;

import lombok.Data;

import java.util.Collections;
import java.util.List;

/**
 * Information about the branches in a Git repository.
 */
@Data
public class GitBranchesInfo {

    private final List<GitBranchInfo> branches;

    public static GitBranchesInfo empty() {
        return new GitBranchesInfo(Collections.emptyList());
    }
}
