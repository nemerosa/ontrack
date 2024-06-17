package net.nemerosa.ontrack.extension.av.config

import net.nemerosa.ontrack.model.structure.Branch
import net.nemerosa.ontrack.model.structure.Project
import net.nemerosa.ontrack.model.structure.StructureService
import org.springframework.stereotype.Component
import kotlin.jvm.optionals.getOrNull

/**
 * Returns the same branch, if it exists, as the target branch.
 */
@Component
class SameBranchSource(
    private val structureService: StructureService,
) : AbstractBranchSource("same") {

    override fun getLatestBranch(
        config: String?,
        project: Project,
        targetBranch: Branch,
        promotion: String,
        includeDisabled: Boolean
    ): Branch? =
        structureService.findBranchByName(
            project = project.name,
            branch = targetBranch.name,
        ).getOrNull()?.takeIf { includeDisabled || !it.isDisabled }

}