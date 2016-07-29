package net.nemerosa.ontrack.extension.issues.model;

import net.nemerosa.ontrack.model.exceptions.InputException;

public class IssueServiceConfigurationIdentifierFormatException extends InputException {
    public IssueServiceConfigurationIdentifierFormatException(String value) {
        super("Wrong format for an issue service configuration ID: %s", value);
    }
}
