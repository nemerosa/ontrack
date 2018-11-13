package net.nemerosa.ontrack.extension.git.service;

import net.nemerosa.ontrack.extension.api.model.BuildDiffRequest;
import net.nemerosa.ontrack.extension.git.model.*;
import net.nemerosa.ontrack.extension.git.property.GitBranchConfigurationProperty;
import net.nemerosa.ontrack.extension.scm.service.SCMService;
import net.nemerosa.ontrack.model.Ack;
import net.nemerosa.ontrack.model.structure.Branch;
import net.nemerosa.ontrack.model.structure.ID;
import net.nemerosa.ontrack.model.structure.Project;
import org.eclipse.jgit.revwalk.RevCommit;

import java.util.List;
import java.util.Optional;
import java.util.concurrent.Future;
import java.util.function.BiConsumer;
import java.util.function.Predicate;

public interface GitService extends SCMService {

    /**
     * Tests if a branch is correctly configured for Git.
     */
    boolean isBranchConfiguredForGit(Branch branch);

    /**
     * Gets the configuration for a project.
     */
    Optional<GitConfiguration> getProjectConfiguration(Project project);

    /**
     * Gets the configuration for a branch
     *
     * @param branch Branch to check
     * @return Configuration.
     */
    Optional<GitBranchConfiguration> getBranchConfiguration(Branch branch);

    /**
     * Launches the build/tag synchronisation for a branch
     */
    Optional<Future<?>> launchBuildSync(ID branchId, boolean synchronous);

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
     * Gets information about a commit in a Git-configured project.
     */
    OntrackGitCommitInfo getCommitProjectInfo(ID projectId, String commit);

    /**
     * Gets the list of remote branches, as defined under <code>ref/heads</code>.
     */
    List<String> getRemoteBranches(GitConfiguration gitConfiguration);

    /**
     * Gets a diff on a list of file changes, filtering the changes using ANT-like patterns
     */
    String diff(GitChangeLog changeLog, List<String> patterns);

    /**
     * Synchronises the Git repository attached to this project.
     *
     * @param project Project
     * @param request Sync request
     * @return Result. The synchronisation will occur asynchronously, but the acknowledgment returns
     * if the project did contain a Git configuration or not.
     */
    Ack projectSync(Project project, GitSynchronisationRequest request);

    /**
     * Synchronises the Git repository attached to this configuration.
     *
     * @param gitConfiguration Configuration to sync
     * @param request          Sync request
     * @return Result. The synchronisation will occur asynchronously, but the acknowledgment returns if the
     * synchronisation was actually launched.
     */
    Optional<Future<?>> sync(GitConfiguration gitConfiguration, GitSynchronisationRequest request);

    /**
     * Gets the Git synchronisation information.
     *
     * @param project Project configured for Git
     * @return Synchronisation information
     */
    GitSynchronisationInfo getProjectGitSyncInfo(Project project);

    void scheduleGitBuildSync(Branch branch, GitBranchConfigurationProperty property);

    void unscheduleGitBuildSync(Branch branch, GitBranchConfigurationProperty property);

    /**
     * Checks the log history and returns <code>true</code> if the token can be found.
     *
     * @param branchConfiguration Git configuration
     * @param token               Expression to be searched for
     * @return Result of the search
     */
    boolean isPatternFound(GitBranchConfiguration branchConfiguration, String token);
}
