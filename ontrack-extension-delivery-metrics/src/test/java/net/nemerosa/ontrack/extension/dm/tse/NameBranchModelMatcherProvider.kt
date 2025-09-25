package net.nemerosa.ontrack.extension.dm.tse

import net.nemerosa.ontrack.extension.scm.branching.BranchingModelService
import net.nemerosa.ontrack.model.structure.Branch
import net.nemerosa.ontrack.model.structure.BranchModelMatcher
import net.nemerosa.ontrack.model.structure.BranchModelMatcherProvider
import net.nemerosa.ontrack.model.structure.Project
import org.springframework.stereotype.Component

/**
 * [BranchModelMatcherProvider] based on [branch name][Branch.name] only.
 *
 * See https://github.com/nemerosa/ontrack/issues/706 for a more global fix.
 */
@Component
class NameBranchModelMatcherProvider(
    private val branchingModelService: BranchingModelService,
) : BranchModelMatcherProvider {

    override fun getBranchModelMatcher(project: Project): BranchModelMatcher {
        val branchingModel = branchingModelService.getBranchingModel(project)
        return object : BranchModelMatcher {
            override fun matches(branch: Branch): Boolean {
                return branchingModel.matches(branch.name)
            }
        }
    }

}