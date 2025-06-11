package net.nemerosa.ontrack.extension.scm.model;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Data;
import net.nemerosa.ontrack.model.structure.Branch;

@Data
@AllArgsConstructor(access = AccessLevel.PROTECTED)
public class SCMIssueCommitBranchInfo {

    private final Branch branch;

    public static SCMIssueCommitBranchInfo of(Branch branch) {
        return new SCMIssueCommitBranchInfo(branch);
    }
}
