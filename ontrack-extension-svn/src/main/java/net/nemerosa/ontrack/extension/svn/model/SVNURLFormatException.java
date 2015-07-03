package net.nemerosa.ontrack.extension.svn.model;

import net.nemerosa.ontrack.model.exceptions.InputException;

public class SVNURLFormatException extends InputException {
    public SVNURLFormatException(String pattern, Object... parameters) {
        super(pattern, parameters);
    }
}
