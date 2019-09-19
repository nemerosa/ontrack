package net.nemerosa.ontrack.extension.git.branching

import net.nemerosa.ontrack.model.structure.Project

/**
 * Management of branching models.
 */
interface BranchingModelService {

    /**
     * Returns the branching model being used by a project.
     *
     * @param project Project to get the model for
     * @return The branching model, never `null` since the
     * [BranchingModel.DEFAULT] model is alaways a fallback.
     */
    fun getBranchingModel(project: Project): BranchingModel

}