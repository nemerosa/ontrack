package net.nemerosa.ontrack.model.structure

import org.springframework.stereotype.Component

@Component
class NOPBranchModelMatcherProvider : BranchModelMatcherProvider {
    override fun getBranchModelMatcher(project: Project): BranchModelMatcher? = null
}
