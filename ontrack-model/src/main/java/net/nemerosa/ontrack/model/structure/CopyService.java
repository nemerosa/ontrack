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

}
