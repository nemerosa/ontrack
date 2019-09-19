package net.nemerosa.ontrack.model.structure

interface BranchModelMatcherService {

    fun getBranchModelMatcher(project: Project): BranchModelMatcher?

}