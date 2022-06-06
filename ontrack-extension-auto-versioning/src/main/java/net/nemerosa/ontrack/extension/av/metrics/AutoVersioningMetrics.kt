package net.nemerosa.ontrack.extension.av.metrics

object AutoVersioningMetrics {

    object Queue {
        const val producedCount = "ontrack_extension_auto_versioning_queue_produced_count"
        const val consumedCount = "ontrack_extension_auto_versioning_queue_consumed_count"
    }

    object Processing {
        const val completedCount = "ontrack_extension_auto_versioning_processing_completed_count"
        const val uncaughtErrorCount = "ontrack_extension_auto_versioning_processing_uncaught_error_count"
    }

    object PostProcessing {
        const val startedCount = "ontrack_extension_auto_versioning_post_processing_started_count"
        const val successCount = "ontrack_extension_auto_versioning_post_processing_success_count"
        const val errorCount = "ontrack_extension_auto_versioning_post_processing_error_count"

        const val time = "ontrack_extension_auto_versioning_post_processing_time"
    }

    object Tags {
        const val ROUTING_KEY = "routingKey"
        const val QUEUE = "queue"
        const val OUTCOME = "outcome"
        const val POST_PROCESSING = "postProcessing"
    }

}