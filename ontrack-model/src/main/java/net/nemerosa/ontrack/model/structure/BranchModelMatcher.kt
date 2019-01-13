package net.nemerosa.ontrack.model.structure

interface BranchModelMatcher {

    fun matches(branch: Branch): Boolean

    infix fun and(other: BranchModelMatcher): BranchModelMatcher = object : BranchModelMatcher {
        override fun matches(branch: Branch): Boolean {
            return this.matches(branch) || other.matches(branch)
        }
    }

}
