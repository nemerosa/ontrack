package net.nemerosa.ontrack.extension.svn.client;

import net.nemerosa.ontrack.common.BaseException;
import org.tmatesoft.svn.core.SVNException;

public class SVNClientException extends BaseException {
    public SVNClientException(SVNException e) {
        super(e, "Problem while accessing Subversion: %s", e);

    }
}
