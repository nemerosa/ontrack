package net.nemerosa.ontrack.extension.av.metrics

object AutoVersioningMetrics {

    object Queue {
        const val producedCount = "ontrack_extension_auto_versioning_queue_produced_count"
        const val consumedCount = "ontrack_extension_auto_versioning_queue_consumed_count"
    }

    object Tags {
        const val ROUTING_KEY = "routingKey"
        const val QUEUE = "queue"
    }

}