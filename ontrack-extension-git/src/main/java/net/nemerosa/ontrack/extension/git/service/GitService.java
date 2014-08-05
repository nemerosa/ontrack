package net.nemerosa.ontrack.extension.git.service;

import net.nemerosa.ontrack.extension.api.model.BuildDiffRequest;
import net.nemerosa.ontrack.extension.git.model.*;
import net.nemerosa.ontrack.model.Ack;
import net.nemerosa.ontrack.model.structure.Branch;
import net.nemerosa.ontrack.model.structure.ID;
import org.eclipse.jgit.revwalk.RevCommit;

import java.util.function.BiConsumer;
import java.util.function.Predicate;

public interface GitService {

    /**
     * Tests if a branch is correctly configured for Git.
     */
    boolean isBranchConfiguredForGit(Branch branch);

    /**
     * Launches the build/tag synchronisation for a branch
     */
    Ack launchBuildSync(ID branchId);

    /**
     * Change log
     */
    GitChangeLog changeLog(BuildDiffRequest request);

    /**
     * Change log commits
     */
    GitChangeLogCommits getChangeLogCommits(GitChangeLog changeLog);

    /**
     * Change log issues
     */
    GitChangeLogIssues getChangeLogIssues(GitChangeLog changeLog);

    /**
     * Change log files
     */
    GitChangeLogFiles getChangeLogFiles(GitChangeLog changeLog);

    /**
     * Loops over each correctly configured branch.
     */
    void forEachConfiguredBranch(BiConsumer<Branch, GitConfiguration> consumer);

    /**
     * Scans the whole history of a repository.
     *
     * @param configuration Repository to scan
     * @param scanFunction  Function that scans the commits. Returns <code>true</code> if the scan
     *                      must not go on, <code>false</code> otherwise.
     * @return <code>true</code> if at least one call to <code>scanFunction</code> has returned <code>true</code>.
     */
    boolean scanCommits(GitConfiguration configuration, Predicate<RevCommit> scanFunction);

    /**
     * Gets information about an issue in a Git-configured branch
     */
    OntrackGitIssueInfo getIssueInfo(ID branchId, String key);
}
