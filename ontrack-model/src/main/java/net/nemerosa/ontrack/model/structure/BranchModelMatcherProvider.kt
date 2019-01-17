package net.nemerosa.ontrack.model.structure

interface BranchModelMatcherProvider {

    fun getBranchModelMatcher(project: Project): BranchModelMatcher?

}

