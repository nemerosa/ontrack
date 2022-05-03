package net.nemerosa.ontrack.extension.github.ingestion.support

import net.nemerosa.ontrack.extension.github.ingestion.processing.model.IPullRequest
import net.nemerosa.ontrack.extension.github.ingestion.processing.model.Repository
import net.nemerosa.ontrack.model.structure.Branch
import net.nemerosa.ontrack.model.structure.Build
import net.nemerosa.ontrack.model.structure.Project

/**
 * Service used to access the model resources (project, branch) when dealing with
 * the ingestion.
 */
interface IngestionModelAccessService {

    /**
     * Gets or creates a project for the given repository.
     *
     * @param repository Repository
     * @param configuration GitHub configuration name
     */
    fun getOrCreateProject(
        repository: Repository,
        configuration: String?,
    ): Project

    /**
     * Gets or creates a branch for the given [project].
     *
     * @param project Parent project
     * @param headBranch Git branch
     * @param pullRequest Pull request (if any)
     */
    fun getOrCreateBranch(
        project: Project,
        headBranch: String,
        pullRequest: IPullRequest?,
    ): Branch

    /**
     * Finds a build using its workflow run ID.
     */
    fun findBuildByRunId(
        repository: Repository,
        runId: Long,
    ): Build?

    /**
     * Finds a build using its name.
     */
    fun findBuildByBuildName(
        repository: Repository,
        buildName: String,
    ): Build?

    /**
     * Finds a build using its release property (label).
     */
    fun findBuildByBuildLabel(
        repository: Repository,
        buildLabel: String,
    ): Build?
}

/**
 * Prefix for the tags
 */
const val REFS_TAGS_PREFIX = "refs/tags/"
