package net.nemerosa.ontrack.extension.svn;

import net.nemerosa.ontrack.common.BaseException;

public class MissingSVNBranchConfigurationException extends BaseException {
    public MissingSVNBranchConfigurationException(String branchName) {
        super("No SVN configuration can be found on branch \"%s\".", branchName);
    }
}
