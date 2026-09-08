package net.nemerosa.ontrack.extension.scm.mock

import net.nemerosa.ontrack.common.Time
import net.nemerosa.ontrack.extension.scm.changelog.SCMCommit
import java.time.LocalDateTime

data class MockCommit(
    val repository: String,
    val revision: Long,
    override val id: String,
    override val message: String,
    /**
     * Defaults to the time the commit is registered, and is a parameter only so that a
     * repository restored from a [MockRepositoryData] keeps the times it was registered with
     * instead of dating every commit from the restart.
     */
    override val timestamp: LocalDateTime = Time.now(),
) : SCMCommit {
    override val shortId: String = id
    override val author: String = "unknown"
    override val authorEmail: String? = null
    override val link: String = "mock://$repository/$id"
}
