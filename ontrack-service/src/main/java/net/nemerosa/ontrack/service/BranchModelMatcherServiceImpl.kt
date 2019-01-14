package net.nemerosa.ontrack.service

import net.nemerosa.ontrack.model.structure.BranchModelMatcher
import net.nemerosa.ontrack.model.structure.BranchModelMatcherProvider
import net.nemerosa.ontrack.model.structure.BranchModelMatcherService
import net.nemerosa.ontrack.model.structure.Project
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
@Transactional
class BranchModelMatcherServiceImpl(
        private val branchModelMatcherProviders: List<BranchModelMatcherProvider>
) : BranchModelMatcherService {

    override fun getBranchModelMatcher(project: Project): BranchModelMatcher? {
        return branchModelMatcherProviders
                .mapNotNull { it.getBranchModelMatcher(project) }
                .fold(null as BranchModelMatcher?) { acc, m -> m and acc }
    }

}