package net.nemerosa.ontrack.extension.workflows.engine

enum class WorkflowInstanceNodeStatus(
    val finished: Boolean,
) {

    IDLE(finished = false),
    STARTED(finished = false),
    STOPPED(finished = true),
    ERROR(finished = true),
    SUCCESS(finished = true),

}