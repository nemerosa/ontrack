package net.nemerosa.ontrack.extension.scm.model;

import java.time.LocalDateTime;

/**
 * Common attributes for a commit (or revision) in a change log.
 */
public interface SCMChangeLogCommit {

    /**
     * Identifier of the commit
     */
    String getId();

    /**
     * Short identifier for the commit
     */
    default String getShortId() {
        return getId();
    }

    /**
     * Author of the commit
     */
    String getAuthor();

    /**
     * Mail of the author of the commit. Can be <code>null</code> if not available.
     */
    String getAuthorEmail();

    /**
     * Timestamp of the commit
     */
    LocalDateTime getTimestamp();

    /**
     * Message associated with the commit
     */
    String getMessage();

    /**
     * Annotated message
     */
    String getFormattedMessage();

    /**
     * Link to the revision
     */
    String getLink();

}
