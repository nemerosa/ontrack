package net.nemerosa.ontrack.service

import net.nemerosa.ontrack.model.structure.BranchModelMatcher
import net.nemerosa.ontrack.model.structure.BranchModelMatcherProvider
import net.nemerosa.ontrack.model.structure.BranchModelMatcherService
import net.nemerosa.ontrack.model.structure.Project
import org.springframework.stereotype.Service

@Service
class BranchModelMatcherServiceImpl(
        private val branchModelMatcherProviders: List<BranchModelMatcherProvider>
) : BranchModelMatcherService {

    override fun getBranchModelMatcher(project: Project): BranchModelMatcher? {
        return branchModelMatcherProviders
                .mapNotNull { it.getBranchModelMatcher(project) }
                .reduce { acc, m -> acc and m }
    }

}