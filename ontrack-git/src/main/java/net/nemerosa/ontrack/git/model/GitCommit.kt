package net.nemerosa.ontrack.git.model

import java.time.LocalDateTime

class GitCommit(
        val id: String,
        val shortId: String,
        val author: GitPerson,
        val committer: GitPerson,
        val commitTime: LocalDateTime,
        val fullMessage: String,
        val shortMessage: String
) : Comparable<GitCommit> {
    override fun compareTo(other: GitCommit): Int {
        return this.commitTime.compareTo(other.commitTime)
    }
}
