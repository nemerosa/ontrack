package net.nemerosa.ontrack.extension.git.model;

import net.nemerosa.ontrack.common.BaseException;
import net.nemerosa.ontrack.model.structure.ID;

public class GitBranchNotConfiguredException extends BaseException {
    public GitBranchNotConfiguredException(ID branchId) {
        super("Branch %s is not configured for Git.", branchId);
    }
}
