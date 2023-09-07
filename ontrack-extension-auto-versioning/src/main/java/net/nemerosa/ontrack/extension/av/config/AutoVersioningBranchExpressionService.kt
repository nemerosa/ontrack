package net.nemerosa.ontrack.extension.av.config

import net.nemerosa.ontrack.model.structure.Branch
import net.nemerosa.ontrack.model.structure.Project

interface AutoVersioningBranchExpressionService {

    /**
     * Given an eligible target branch for auto versioning, and a source project, and an expression, returns
     * the latest branch eligible for auto versioning for the source project.
     *
     * > For now, only "same" is supported as an expression. It looks for a branch with the same exact name
     * as the [eligibleTargetBranch] branch in the [project] source project.
     *
     * @param eligibleTargetBranch Eligible target branch for auto versioning
     * @param project Source project where to look for the branch
     * @param avBranchExpression Expression
     * @return Eligible source branch
     */
    fun getLatestBranch(eligibleTargetBranch: Branch, project: Project, avBranchExpression: String): Branch?

}