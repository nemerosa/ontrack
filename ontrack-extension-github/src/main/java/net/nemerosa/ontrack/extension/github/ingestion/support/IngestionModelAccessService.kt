package net.nemerosa.ontrack.extension.github.ingestion.support

import net.nemerosa.ontrack.extension.github.ingestion.processing.model.IPullRequest
import net.nemerosa.ontrack.extension.github.ingestion.processing.model.Repository
import net.nemerosa.ontrack.model.structure.*

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
     * Finds a project from a repository.
     */
    fun findProjectFromRepository(repository: Repository): Project?

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

    /**
     * Gets or creates a validation stamp
     *
     * @param branch Parent branch
     * @param vsName Name of the validation stamp
     * @param vsDescription Description of the validation stamp
     */
    fun setupValidationStamp(
        branch: Branch,
        vsName: String,
        vsDescription: String?
    ): ValidationStamp

    /**
     * Gets or creates a promotion level
     *
     * @param branch Parent branch
     * @param plName Name of the promotion level
     * @param plDescription Description of the promotion level
     */
    fun setupPromotionLevel(
        branch: Branch,
        plName: String,
        plDescription: String?
    ): PromotionLevel
}

/**
 * Prefix for the tags
 */
const val REFS_TAGS_PREFIX = "refs/tags/"
