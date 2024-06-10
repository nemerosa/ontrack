package net.nemerosa.ontrack.model.structure

/**
 * Service which, given a [Branch], returns its display name. This can be the actual
 * [name][Branch.name] of the branch or its SCM path (like a Git branch name).
 */
interface BranchDisplayNameService {

    /**
     * Gets the display name for this branch, using a default naming policy
     */
    @Deprecated("Will be removed in V5. Use the method with the branch name policy.")
    fun getBranchDisplayName(branch: Branch): String

    /**
     * Gets the display name for this branch, using a given branch name policy
     */
    fun getBranchDisplayName(branch: Branch, policy: BranchNamePolicy): String

}