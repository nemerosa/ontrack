package net.nemerosa.ontrack.extensions.environments

enum class SlotPipelineStatus(
    val finished: Boolean,
) {

    ONGOING(
        finished = false,
    ),
    DEPLOYING(
        finished = false
    ),
    CANCELLED(
        finished = true,
    ),
    ERROR(
        finished = true
    ),
    DEPLOYED(
        finished = true
    );

    companion object {
        val activeStatuses: List<SlotPipelineStatus> = values()
            .filter { !it.finished }
            .toList()
    }
}
