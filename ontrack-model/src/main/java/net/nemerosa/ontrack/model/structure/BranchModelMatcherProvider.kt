package net.nemerosa.ontrack.model.structure

import org.springframework.stereotype.Component

interface BranchModelMatcherProvider {

    fun getBranchModelMatcher(project: Project): BranchModelMatcher?

}

@Component
class NOPBranchModelMatcherProvider : BranchModelMatcherProvider {
    override fun getBranchModelMatcher(project: Project): BranchModelMatcher? = null
}