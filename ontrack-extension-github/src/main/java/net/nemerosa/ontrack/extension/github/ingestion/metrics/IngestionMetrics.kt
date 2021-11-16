package net.nemerosa.ontrack.extension.github.ingestion.metrics

object IngestionMetrics {

    object Hook {
        const val signatureErrorCount = "ontrack_extension_github_ingestion_hook_signature_error_count"
        const val repositoryRejectedCount = "ontrack_extension_github_ingestion_hook_repository_rejected_count"
        const val repositoryAcceptedCount = "ontrack_extension_github_ingestion_hook_repository_accepted_count"
        const val acceptedCount = "ontrack_extension_github_ingestion_hook_accepted_count"
        const val ignoredCount = "ontrack_extension_github_ingestion_hook_ignored_count"
    }

    object Queue {
        const val producedCount = "ontrack_extension_github_ingestion_queue_produced_count"
        const val consumedCount = "ontrack_extension_github_ingestion_queue_consumed_count"
    }

    object Process {
        const val startedCount = "ontrack_extension_github_ingestion_process_started_count"
        const val successCount = "ontrack_extension_github_ingestion_process_success_count"
        const val ignoredCount = "ontrack_extension_github_ingestion_process_ignored_count"
        const val errorCount = "ontrack_extension_github_ingestion_process_error_count"
        const val finishedCount = "ontrack_extension_github_ingestion_process_finished_count"

        const val time = "ontrack_extension_github_ingestion_process_time"
    }

}