package net.nemerosa.ontrack.dsl

class ChangeLogCommit extends AbstractResource {

    ChangeLogCommit(Ontrack ontrack, Object node) {
        super(ontrack, node)
    }

    String getId() {
        node['id']
    }

    /**
     * Short identifier for the commit
     */
    String getShortId() {
        node['shortId']
    }

    /**
     * Author of the commit
     */
    String getAuthor() {
        node['author']
    }

    /**
     * Mail of the author of the commit. Can be <code>null</code> if not available.
     */
    String getAuthorEmail() {
        node['authorEmail']
    }

    /**
     * Timestamp of the commit
     */
    String getTimestamp() {
        node['timestamp']
    }

    /**
     * Message associated with the commit
     */
    String getMessage() {
        node['message']?.trim()
    }

    /**
     * Annotated message
     */
    String getFormattedMessage() {
        node['formattedMessage']?.trim()
    }

    /**
     * Link to the revision
     */
    String getLink() {
        node['link']
    }

}
