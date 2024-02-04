package net.nemerosa.ontrack.extension.scm.mock

import net.nemerosa.ontrack.common.Time
import net.nemerosa.ontrack.extension.scm.changelog.SCMChangeLogCommit
import java.time.LocalDateTime

data class MockCommit(
    val repository: String,
    val revision: Long,
    override val id: String,
    override val message: String,
) : SCMChangeLogCommit {
    override val shortId: String = id
    override val author: String = "unknown"
    override val authorEmail: String? = null
    override val timestamp: LocalDateTime = Time.now()
    override val formattedMessage: String = message
    override val link: String = "mock://$repository/$id"
}