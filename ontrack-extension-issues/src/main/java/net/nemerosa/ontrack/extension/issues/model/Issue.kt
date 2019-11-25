package net.nemerosa.ontrack.extension.issues.model

import java.time.LocalDateTime

/**
 * Abstract definition of an issue.
 */
interface Issue {

    val key: String

    val displayKey: String get() = key

    val summary: String

    val url: String

    val status: IssueStatus

    val updateTime: LocalDateTime

}
