package net.nemerosa.ontrack.extension.recordings

import java.time.LocalDateTime

/**
 * Definition of a record
 */
interface Recording {

    val id: String
    val startTime: LocalDateTime
    val endTime: LocalDateTime?

}