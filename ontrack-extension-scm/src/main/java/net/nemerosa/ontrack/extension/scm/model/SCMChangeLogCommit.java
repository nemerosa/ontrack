package net.nemerosa.ontrack.extension.scm.model;

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
