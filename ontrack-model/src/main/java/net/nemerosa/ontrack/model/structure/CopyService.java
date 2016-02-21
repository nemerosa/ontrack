package net.nemerosa.ontrack.model.structure;

import java.util.function.Function;

/**
 * Service used for the duplication, copying and cloning of information.
 */
public interface CopyService {

    /**
     * Copies the configuration of the {@linkplain BranchCopyRequest#sourceBranchId source branch}
     * to the target branch.
     */
    Branch copy(Branch branch, BranchCopyRequest request);

    /**
     * Copies the configuration of the source branch
     * to the target branch.
     */
    Branch copy(Branch targetBranch, Branch sourceBranch, Function<String, String> replacementFn, SyncPolicy syncPolicy);

    /**
     * Clones the <code>branch</code> into a new branch
     *
     * @param branch  Branch to clone (untouched)
     * @param request Cloning instructions
     * @return Created branch
     */
    Branch cloneBranch(Branch branch, BranchCloneRequest request);

    /**
     * Clones the <code>project</code> into a new project
     *
     * @param project Project to clone (untouched)
     * @param request Cloning instructions
     * @return Created project
     */
    Project cloneProject(Project project, ProjectCloneRequest request);

    /**
     * Bulk update for a branch, by using replacements on all its components.
     *
     * @param branch  Branch to update
     * @param request Update instructions
     * @return Updated branch
     */
    Branch update(Branch branch, BranchBulkUpdateRequest request);
}
