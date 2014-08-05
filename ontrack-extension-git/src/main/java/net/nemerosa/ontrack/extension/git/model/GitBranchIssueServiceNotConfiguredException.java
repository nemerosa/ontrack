package net.nemerosa.ontrack.extension.git.model;

import net.nemerosa.ontrack.common.BaseException;
import net.nemerosa.ontrack.model.structure.ID;

public class GitBranchIssueServiceNotConfiguredException extends BaseException {
    public GitBranchIssueServiceNotConfiguredException(ID branchId) {
        super("Branch %s has no issue service configured for Git.", branchId);
    }
}
