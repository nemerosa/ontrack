package net.nemerosa.ontrack.extension.svn;

import net.nemerosa.ontrack.common.BaseException;

public class MissingSVNProjectConfigurationException extends BaseException {
    public MissingSVNProjectConfigurationException(String projectName) {
        super("No SVN configuration can be found on project \"%s\".", projectName);
    }
}
