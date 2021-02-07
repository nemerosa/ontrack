package net.nemerosa.ontrack.dsl.v4

import net.nemerosa.ontrack.dsl.v4.doc.DSL
import net.nemerosa.ontrack.dsl.v4.doc.DSLMethod

@DSL
class ChangeLogCommit extends AbstractResource {

    ChangeLogCommit(Ontrack ontrack, Object node) {
        super(ontrack, node)
    }

    @DSLMethod("Gets the full hash of the commit")
    String getId() {
        node['id']
    }

    /**
     * Short identifier for the commit
     */
    @DSLMethod("Gets the abbreviated hash of the commit")
    String getShortId() {
        node['shortId']
    }

    /**
     * Author of the commit
     */
    @DSLMethod("Gets the author name of the commit")
    String getAuthor() {
        node['author']
    }

    /**
     * Mail of the author of the commit. Can be <code>null</code> if not available.
     */
    @DSLMethod("Gets the author email of the commit. Can be `null` if not available.")
    String getAuthorEmail() {
        node['authorEmail']
    }

    /**
     * Timestamp of the commit
     */
    @DSLMethod("Gets the timestamp of the commit as a ISO date string.")
    String getTimestamp() {
        node['timestamp']
    }

    /**
     * Message associated with the commit
     */
    @DSLMethod("Gets the trimmed message of the commit.")
    String getMessage() {
        node['message']?.trim()
    }

    /**
     * Annotated message
     */
    @DSLMethod("Gets the formatted message of the commit, where issues might have been replaced by links.")
    String getFormattedMessage() {
        node['formattedMessage']?.trim()
    }

    /**
     * Link to the revision
     */
    @DSLMethod("Gets a link to SCM for this commit.")
    String getLink() {
        node['link']
    }

}
