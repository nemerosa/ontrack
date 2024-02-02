package net.nemerosa.ontrack.extension.av.config

import net.nemerosa.ontrack.model.structure.Branch
import net.nemerosa.ontrack.model.structure.Project
import org.springframework.stereotype.Service

@Service
class AutoVersioningBranchExpressionServiceImpl(
    private val branchSourceFactory: BranchSourceFactory,
) : AutoVersioningBranchExpressionService {

    override fun getLatestBranch(eligibleTargetBranch: Branch, promotion: String, project: Project, avBranchExpression: String): Branch? {
        val (id, config) = BranchSourceExpression.parseBranchSourceExpression(avBranchExpression)
        val branchSource = branchSourceFactory.getBranchSource(id)
        return branchSource.getLatestBranch(config, project, eligibleTargetBranch, promotion)
    }

}