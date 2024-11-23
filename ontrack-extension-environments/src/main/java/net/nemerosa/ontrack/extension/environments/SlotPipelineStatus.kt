package net.nemerosa.ontrack.extension.environments

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
    DEPLOYED(
        finished = true
    );

    companion object {
        val activeStatuses: List<SlotPipelineStatus> = values()
            .filter { !it.finished }
            .toList()
    }
}
