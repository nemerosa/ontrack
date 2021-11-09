package net.nemerosa.ontrack.extension.github.ingestion.support

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
     */
    fun getOrCreateProject(
        repository: Repository
    ): Project

    /**
     * Gets or creates a branch for the given [project].
     *
     * @param project Parent project
     * @param headBranch Git branch
     * @param baseBranch Base branch (not null only for pull requests)
     */
    fun getOrCreateBranch(
        project: Project,
        headBranch: String,
        baseBranch: String?,
    ): Branch
}