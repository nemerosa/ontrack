package net.nemerosa.ontrack.model.structure;

/**
 * Service used for the duplication, copying and cloning of information.
 */
public interface CopyService {

    /**
     * Copies the configuration of the {@linkplain BranchCopyRequest#getSourceBranchId() source branch}
     * to the target branch.
     */
    Branch copy(Branch branch, BranchCopyRequest request);

    /**
     * Clones the <code>branch</code> into a new branch
     *
     * @param branch  Branch to clone (untouched)
     * @param request Cloning instructions
     * @return Created branch
     */
    Branch clone(Branch branch, BranchCloneRequest request);

    /**
     * Clones the <code>project</code> into a new project
     *
     * @param project Project to clone (untouched)
     * @param request Cloning instructions
     * @return Created project
     */
    Project cloneProject(Project project, ProjectCloneRequest request);
}
