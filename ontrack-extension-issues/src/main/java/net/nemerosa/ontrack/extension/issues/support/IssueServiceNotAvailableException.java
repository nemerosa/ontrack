package net.nemerosa.ontrack.extension.issues.support;

import net.nemerosa.ontrack.common.BaseException;

public class IssueServiceNotAvailableException extends BaseException {
    public IssueServiceNotAvailableException(String id) {
        super("Issue service [%s] cannot be found or is not enabled", id);
    }
}
