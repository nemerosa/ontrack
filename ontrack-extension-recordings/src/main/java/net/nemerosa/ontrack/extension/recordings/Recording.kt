package net.nemerosa.ontrack.extension.recordings

import net.nemerosa.ontrack.model.annotations.APIDescription
import java.time.LocalDateTime

/**
 * Definition of a record
 */
interface Recording {

    @APIDescription("Record unique ID")
    val id: String

    @APIDescription("Record start time")
    val startTime: LocalDateTime

    @APIDescription("Record end time")
    val endTime: LocalDateTime?

}