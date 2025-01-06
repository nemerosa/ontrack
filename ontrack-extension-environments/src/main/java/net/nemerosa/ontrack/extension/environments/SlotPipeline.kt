package net.nemerosa.ontrack.extension.environments

import net.nemerosa.ontrack.common.Time
import net.nemerosa.ontrack.model.structure.Build
import java.time.LocalDateTime
import java.util.*

data class SlotPipeline(
    val id: String = UUID.randomUUID().toString(),
    val number: Int = 0,
    val start: LocalDateTime = Time.now,
    val end: LocalDateTime? = null,
    val status: SlotPipelineStatus = SlotPipelineStatus.CANDIDATE,
    val slot: Slot,
    val build: Build,
) {

    fun fullName() = "${slot.fullName()}#$number"

    fun withStatus(status: SlotPipelineStatus) = SlotPipeline(
        id = id,
        start = start,
        end = end,
        status = status,
        slot = slot,
        build = build,
        number = number,
    )

    fun withEnd(end: LocalDateTime) = SlotPipeline(
        id = id,
        start = start,
        end = end,
        status = status,
        slot = slot,
        build = build,
        number = number,
    )

    fun withNumber(number: Int) = SlotPipeline(
        id = id,
        start = start,
        end = end,
        status = status,
        slot = slot,
        build = build,
        number = number,
    )
}
