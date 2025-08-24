package net.nemerosa.ontrack.model.structure

/**
 * Service used for the duplication, copying and cloning of information.
 */
interface CopyService {

    /**
     * Copies the configuration of the [source branch][BranchCopyRequest.sourceBranchId]
     * to the [target branch][targetBranch].
     */
    fun copy(targetBranch: Branch, request: BranchCopyRequest): Branch

    /**
     * Copies the configuration of the source branch
     * to the target branch.
     */
    fun copy(
        targetBranch: Branch,
        sourceBranch: Branch,
        replacementFn: (String) -> String,
    ): Branch

    /**
     * Clones the `branch` into a new branch
     *
     * @param branch  Branch to clone (untouched)
     * @param request Cloning instructions
     * @return Created branch
     */
    fun cloneBranch(branch: Branch, request: BranchCloneRequest): Branch

    /**
     * Clones the `project` into a new project
     *
     * @param project Project to clone (untouched)
     * @param request Cloning instructions
     * @return Created project
     */
    fun cloneProject(project: Project, request: ProjectCloneRequest): Project

    /**
     * Bulk update for a branch, by using replacements on all its components.
     *
     * @param branch  Branch to update
     * @param request Update instructions
     * @return Updated branch
     */
    fun update(branch: Branch, request: BranchBulkUpdateRequest): Branch
}
