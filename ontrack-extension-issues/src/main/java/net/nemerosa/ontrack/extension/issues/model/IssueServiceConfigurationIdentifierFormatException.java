package net.nemerosa.ontrack.extension.issues.model;

import net.nemerosa.ontrack.common.BaseException;

public class IssueServiceConfigurationIdentifierFormatException extends BaseException {
    public IssueServiceConfigurationIdentifierFormatException(String value) {
        super("Wrong format for an issue service configuration ID: %s", value);
    }
}
