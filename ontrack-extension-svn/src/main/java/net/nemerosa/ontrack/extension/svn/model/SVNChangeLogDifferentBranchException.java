package net.nemerosa.ontrack.extension.svn.model;

import net.nemerosa.ontrack.model.exceptions.InputException;

public class SVNChangeLogDifferentBranchException extends InputException {
    public SVNChangeLogDifferentBranchException() {
        super("The SVN change log can only be performed for two builds on the same branch");
    }
}
