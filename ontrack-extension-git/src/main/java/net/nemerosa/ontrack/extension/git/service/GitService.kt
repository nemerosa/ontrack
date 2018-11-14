package net.nemerosa.ontrack.extension.git.service

import net.nemerosa.ontrack.extension.api.model.BuildDiffRequest
import net.nemerosa.ontrack.extension.git.model.*
import net.nemerosa.ontrack.extension.git.property.GitBranchConfigurationProperty
import net.nemerosa.ontrack.extension.scm.service.SCMService
import net.nemerosa.ontrack.model.Ack
import net.nemerosa.ontrack.model.structure.Branch
import net.nemerosa.ontrack.model.structure.ID
import net.nemerosa.ontrack.model.structure.Project
import org.eclipse.jgit.revwalk.RevCommit
import java.util.Optional
import java.util.concurrent.Future
import java.util.function.BiConsumer
import java.util.function.Predicate

interface GitService : SCMService {

    /**
     * Tests if a branch is correctly configured for Git.
     */
    fun isBranchConfiguredForGit(branch: Branch): Boolean

    /**
     * Gets the configuration for a project.
     */
    // TODO Kotlin, use nullable
    fun getProjectConfiguration(project: Project): Optional<GitConfiguration>

    /**
     * Gets the configuration for a branch
     *
     * @param branch Branch to check
     * @return Configuration.
     */
    // TODO Kotlin, use nullable
    fun getBranchConfiguration(branch: Branch): Optional<GitBranchConfiguration>

    /**
     * Launches the build/tag synchronisation for a branch
     */
    // TODO Kotlin, use nullable
    fun launchBuildSync(branchId: ID, synchronous: Boolean): Optional<Future<*>>

    /**
     * Change log
     */
    fun changeLog(request: BuildDiffRequest): GitChangeLog

    /**
     * Change log commits
     */
    fun getChangeLogCommits(changeLog: GitChangeLog): GitChangeLogCommits

    /**
     * Change log issues
     */
    fun getChangeLogIssues(changeLog: GitChangeLog): GitChangeLogIssues

    /**
     * Change log files
     */
    fun getChangeLogFiles(changeLog: GitChangeLog): GitChangeLogFiles

    /**
     * Loops over each correctly configured project.
     */
    fun forEachConfiguredProject(consumer: BiConsumer<Project, GitConfiguration>)

    /**
     * Loops over each correctly configured branch. Branch template definitions are excluded.
     */
    fun forEachConfiguredBranch(consumer: BiConsumer<Branch, GitBranchConfiguration>)

    /**
     * Gets information about an issue in a Git-configured branch
     */
    fun getIssueInfo(branchId: ID, key: String): OntrackGitIssueInfo

    /**
     * Looks up a commit in the given `configuration`.
     *
     * @param id Commit long or short ID
     * @return The content of a commit if it exists, empty otherwise.
     */
    // TODO Kotlin, use nullable
    fun lookupCommit(configuration: GitConfiguration, id: String): Optional<GitUICommit>

    /**
     * Gets information about a commit in a Git-configured project.
     */
    fun getCommitProjectInfo(projectId: ID, commit: String): OntrackGitCommitInfo

    /**
     * Gets the list of remote branches, as defined under `ref/heads`.
     */
    fun getRemoteBranches(gitConfiguration: GitConfiguration): List<String>

    /**
     * Gets a diff on a list of file changes, filtering the changes using ANT-like patterns
     */
    fun diff(changeLog: GitChangeLog, patterns: List<String>): String

    /**
     * Synchronises the Git repository attached to this project.
     *
     * @param project Project
     * @param request Sync request
     * @return Result. The synchronisation will occur asynchronously, but the acknowledgment returns
     * if the project did contain a Git configuration or not.
     */
    fun projectSync(project: Project, request: GitSynchronisationRequest): Ack

    /**
     * Synchronises the Git repository attached to this configuration.
     *
     * @param gitConfiguration Configuration to sync
     * @param request          Sync request
     * @return Result. The synchronisation will occur asynchronously, but the acknowledgment returns if the
     * synchronisation was actually launched.
     */
    // TODO Kotlin, use nullable
    fun sync(gitConfiguration: GitConfiguration, request: GitSynchronisationRequest): Optional<Future<*>>

    /**
     * Gets the Git synchronisation information.
     *
     * @param project Project configured for Git
     * @return Synchronisation information
     */
    fun getProjectGitSyncInfo(project: Project): GitSynchronisationInfo

    fun scheduleGitBuildSync(branch: Branch, property: GitBranchConfigurationProperty)

    fun unscheduleGitBuildSync(branch: Branch, property: GitBranchConfigurationProperty)

    /**
     * Checks the log history and returns `true` if the token can be found.
     *
     * @param branchConfiguration Git configuration
     * @param token               Expression to be searched for
     * @return Result of the search
     */
    fun isPatternFound(branchConfiguration: GitBranchConfiguration, token: String): Boolean
}
