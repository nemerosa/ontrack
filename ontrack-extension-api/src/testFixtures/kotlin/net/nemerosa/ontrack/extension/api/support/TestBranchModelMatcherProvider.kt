package net.nemerosa.ontrack.extension.api.support

import net.nemerosa.ontrack.model.structure.Branch
import net.nemerosa.ontrack.model.structure.BranchModelMatcher
import net.nemerosa.ontrack.model.structure.BranchModelMatcherProvider
import net.nemerosa.ontrack.model.structure.Project

/**
 * This configurable [BranchModelMatcherProvider] can be injected into a test Spring configuration
 * to enable some testing around branch models.
 */
class TestBranchModelMatcherProvider : BranchModelMatcherProvider {

    /**
     * List of projects to match with a model
     */
    val projects = mutableSetOf<String>()

    /**
     * Regular expression for matching the branch [name][Branch.name].
     */
    var branchPattern = "master|release-.*"

    override fun getBranchModelMatcher(project: Project): BranchModelMatcher? =
            if (project.name in projects) {
                object : BranchModelMatcher {
                    override fun matches(branch: Branch): Boolean = branchPattern.toRegex().matches(branch.name)
                }
            } else {
                null
            }

}