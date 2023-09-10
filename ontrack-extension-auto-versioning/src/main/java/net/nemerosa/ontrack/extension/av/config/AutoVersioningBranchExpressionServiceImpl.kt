package net.nemerosa.ontrack.extension.av.config

import net.nemerosa.ontrack.common.getOrNull
import net.nemerosa.ontrack.model.structure.Branch
import net.nemerosa.ontrack.model.structure.Project
import net.nemerosa.ontrack.model.structure.StructureService
import org.springframework.stereotype.Service

@Service
class AutoVersioningBranchExpressionServiceImpl(
    private val structureService: StructureService,
) : AutoVersioningBranchExpressionService {

    override fun getLatestBranch(eligibleTargetBranch: Branch, project: Project, avBranchExpression: String): Branch? {
        if (avBranchExpression == "same") {
            return structureService.findBranchByName(
                project = project.name,
                branch = eligibleTargetBranch.name,
            ).getOrNull()
        } else {
            throw AutoVersioningBranchExpressionParsingException(avBranchExpression)
        }
    }

}