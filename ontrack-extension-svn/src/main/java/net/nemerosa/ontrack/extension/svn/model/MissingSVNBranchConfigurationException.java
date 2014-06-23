package net.nemerosa.ontrack.extension.svn.model;

import net.nemerosa.ontrack.common.BaseException;

public class MissingSVNBranchConfigurationException extends BaseException {
    public MissingSVNBranchConfigurationException(String branchName) {
        super("No SVN configuration can be found on branch \"%s\".", branchName);
    }
}
