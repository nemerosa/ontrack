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

    object Tags {
        const val ROUTING_KEY = "routingKey"
        const val QUEUE = "queue"
        const val OUTCOME = "outcome"
    }

}