package net.nemerosa.ontrack.extension.git.branching

import net.nemerosa.ontrack.extension.git.service.GitService
import net.nemerosa.ontrack.model.structure.Branch
import net.nemerosa.ontrack.model.structure.BranchModelMatcher
import net.nemerosa.ontrack.model.structure.BranchModelMatcherProvider
import net.nemerosa.ontrack.model.structure.Project
import org.springframework.stereotype.Component

@Component
class GitBranchModelMatcherProvider(
        private val branchingModelService: BranchingModelService,
        private val gitService: GitService
) : BranchModelMatcherProvider {
    override fun getBranchModelMatcher(project: Project): BranchModelMatcher? {
        val projectConfiguration = gitService.getProjectConfiguration(project)
        return if (projectConfiguration != null) {
            val branchingModel = branchingModelService.getBranchingModel(project)
            object : BranchModelMatcher {
                override fun matches(branch: Branch): Boolean {
                    val branchConfiguration = gitService.getBranchConfiguration(branch)
                    return branchConfiguration
                            ?.branch
                            ?.let { branchName -> branchingModel.matches(branchName) }
                            ?: false
                }

            }
        } else {
            null
        }
    }
}