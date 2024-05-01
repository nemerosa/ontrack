package net.nemerosa.ontrack.extension.workflows.engine

enum class WorkflowInstanceStatus(
    val finished: Boolean,
) {

    STARTED(
        finished = false,
    ),
    RUNNING(
        finished = false,
    ),
    ERROR(
        finished = true,
    ),
    SUCCESS(
        finished = true,
    ),

}