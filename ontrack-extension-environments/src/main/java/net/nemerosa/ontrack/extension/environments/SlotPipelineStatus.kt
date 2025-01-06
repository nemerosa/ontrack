package net.nemerosa.ontrack.extension.environments

enum class SlotPipelineStatus(
    val finished: Boolean,
) {

    CANDIDATE(
        finished = false,
    ),
    RUNNING(
        finished = false
    ),
    CANCELLED(
        finished = true,
    ),
    DONE(
        finished = true
    );

    companion object {
        val activeStatuses: List<SlotPipelineStatus> = values()
            .filter { !it.finished }
            .toList()
    }
}
