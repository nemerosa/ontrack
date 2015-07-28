package net.nemerosa.ontrack.extension.svn.model;

import net.nemerosa.ontrack.model.exceptions.InputException;

public class SVNConfigurationValidationException extends InputException {
    public SVNConfigurationValidationException(String pattern, Object... parameters) {
        super(pattern, parameters);
    }
}
