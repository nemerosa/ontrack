package net.nemerosa.ontrack.extension.issues.model;

import net.nemerosa.ontrack.common.BaseException;

public class IssueServiceNotConfiguredException extends BaseException {
    public IssueServiceNotConfiguredException() {
        super("No issue service has been configured.");
    }
}
