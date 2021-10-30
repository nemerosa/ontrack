package net.nemerosa.ontrack.extension.github.ingestion.processing.model

@Suppress("EnumEntryName")
enum class WorkflowJobStepStatus {
    queued,
    in_progress,
    completed;
}