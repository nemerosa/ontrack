package net.nemerosa.ontrack.git.model

import java.time.LocalDateTime

class GitTag(
        val name: String,
        val time: LocalDateTime
) : Comparable<GitTag> {


    override fun compareTo(other: GitTag): Int {
        return this.time.compareTo(other.time)
    }
}
