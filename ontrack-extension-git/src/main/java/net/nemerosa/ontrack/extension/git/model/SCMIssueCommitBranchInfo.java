package net.nemerosa.ontrack.extension.git.model;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.experimental.Wither;
import net.nemerosa.ontrack.model.structure.Branch;
import net.nemerosa.ontrack.model.structure.BranchStatusView;
import net.nemerosa.ontrack.model.structure.BuildView;

@Data
@AllArgsConstructor(access = AccessLevel.PROTECTED)
public class SCMIssueCommitBranchInfo {

    private final Branch branch;
    @Wither
    private final BuildView buildView;
    @Wither
    private final BranchStatusView branchStatusView;

    public static SCMIssueCommitBranchInfo of(Branch branch) {
        return new SCMIssueCommitBranchInfo(branch, null, null);
    }
}
