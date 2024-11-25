package net.nemerosa.ontrack.extension.workflows.engine

enum class WorkflowInstanceNodeStatus(
    val finished: Boolean,
) {

    CREATED(finished = false),
    WAITING(finished = false),
    STARTED(finished = false),
    CANCELLED(finished = true),
    ERROR(finished = true),
    SUCCESS(finished = true),

}