package net.nemerosa.ontrack.extension.svn.model;

import net.nemerosa.ontrack.common.BaseException;

public class SVNIndexationException extends BaseException {

    private final String revisionMessage;
    private final long revision;

    public SVNIndexationException(long revision, String message, Exception ex) {
        super(ex, "Indexation error at %d", revision);
        this.revision = revision;
        this.revisionMessage = message;
    }

    public String getRevisionMessage() {
        return revisionMessage;
    }

    public long getRevision() {
        return revision;
    }

}
