package net.nemerosa.ontrack.extension.github.ingestion.support

import net.nemerosa.ontrack.extension.github.ingestion.processing.model.IPullRequest
import net.nemerosa.ontrack.extension.github.ingestion.processing.model.Repository
import net.nemerosa.ontrack.model.structure.Branch
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
}