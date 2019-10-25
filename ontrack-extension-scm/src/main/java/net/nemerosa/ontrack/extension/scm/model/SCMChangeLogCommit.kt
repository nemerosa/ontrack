package net.nemerosa.ontrack.extension.scm.model

import java.time.LocalDateTime

/**
 * Common attributes for a commit (or revision) in a change log.
 */
interface SCMChangeLogCommit {

    /**
     * Identifier of the commit
     */
    val id: String

    /**
     * Short identifier for the commit
     */
    val shortId: String

    /**
     * Author of the commit
     */
    val author: String

    /**
     * Mail of the author of the commit. Can be `null` if not available.
     */
    val authorEmail: String?

    /**
     * Timestamp of the commit
     */
    val timestamp: LocalDateTime

    /**
     * Message associated with the commit
     */
    val message: String

    /**
     * Annotated message
     */
    val formattedMessage: String

    /**
     * Link to the revision
     */
    val link: String

}
