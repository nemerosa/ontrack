package net.nemerosa.ontrack.extension.svn.model;

import net.nemerosa.ontrack.model.exceptions.InputException;

public class SVNChangeLogUUIDException extends InputException {
    public SVNChangeLogUUIDException(String uuid) {
        super("The SVN change log with UUID %s has been requested but not found. " +
                "It was either invalid or not valid any longer.", uuid);
    }
}
