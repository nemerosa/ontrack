package net.nemerosa.ontrack.extension.svn.model;

import net.nemerosa.ontrack.common.BaseException;

public class SVNRevisionNotFoundException extends BaseException {
    public SVNRevisionNotFoundException(long revision) {
        super("Revision %d was not found.", revision);
    }
}
