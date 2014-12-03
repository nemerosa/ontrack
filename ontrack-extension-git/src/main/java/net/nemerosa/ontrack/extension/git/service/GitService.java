package net.nemerosa.ontrack.extension.git.service;

import net.nemerosa.ontrack.extension.api.model.BuildDiffRequest;
import net.nemerosa.ontrack.extension.git.model.*;
import net.nemerosa.ontrack.model.Ack;
import net.nemerosa.ontrack.model.structure.Branch;
import net.nemerosa.ontrack.model.structure.ID;
import net.nemerosa.ontrack.model.structure.Project;
import org.eclipse.jgit.revwalk.RevCommit;

import java.util.List;
import java.util.Optional;
import java.util.function.BiConsumer;
import java.util.function.Predicate;

public interface GitService {

    /**
     * Tests if a branch is correctly configured for Git.
     */
    boolean isBranchConfiguredForGit(Branch branch);

    /**
     * Gets the configuration for a project.
     */
    GitConfiguration getProjectConfiguration(Project project);

    /**
     * Gets the configuration for a branch
     *
     * @param branch Branch to check
     * @return Configuration.
     */
    GitBranchConfiguration getGitBranchConfiguration(Branch branch);

    /**
     * Gets the configuration for a branch
     *
     * @param branch Branch to check
     * @return Configuration. Never null but can be
     * {@link net.nemerosa.ontrack.extension.git.model.GitConfiguration#isValid() invalid}.
     */
    @Deprecated
    GitConfiguration getBranchConfiguration(Branch branch);

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
     * Loops over each correctly configured project.
     */
    void forEachConfiguredProject(BiConsumer<Project, GitConfiguration> consumer);

    /**
     * Loops over each correctly configured branch. Branch template definitions are excluded.
     */
    void forEachConfiguredBranch(BiConsumer<Branch, GitBranchConfiguration> consumer);

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

    /**
     * Looks up a commit in the given <code>configuration</code>.
     *
     * @param id Commit long or short ID
     * @return The content of a commit if it exists, empty otherwise.
     */
    Optional<GitUICommit> lookupCommit(GitConfiguration configuration, String id);

    /**
     * Gets information about a commit in a Git-configured branch.
     */
    OntrackGitCommitInfo getCommitInfo(ID branchId, String commit);

    /**
     * Gets the list of remote branches, as defined under <code>ref/heads</code>.
     */
    List<String> getRemoteBranches(GitConfiguration gitConfiguration);
}
