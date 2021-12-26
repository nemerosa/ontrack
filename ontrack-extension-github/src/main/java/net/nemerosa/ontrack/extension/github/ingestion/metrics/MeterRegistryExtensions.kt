package net.nemerosa.ontrack.extension.github.ingestion.metrics

import io.micrometer.core.instrument.MeterRegistry
import io.micrometer.core.instrument.Tag
import net.nemerosa.ontrack.extension.github.ingestion.payload.IngestionHookPayload

/**
 * Utility method to increment a counter metric.
 *
 * @param name Name of the counter
 * @param tags List of tags to associate with the metric.
 */
private fun MeterRegistry.increment(
    name: String,
    vararg tags: Pair<String, String>
) {
    val counter = counter(name, tags.map { Tag.of(it.first, it.second) })
    counter.increment()
}

/**
 * Utility method to record a time metric.
 *
 * @param T Return type of the code to measure
 * @param name Name of the timer
 * @param tags List of tags to associate with the metric.
 * @param code Code whose execution duration must be recorded
 * @return Value returned by the code being measured
 */
private fun <T> MeterRegistry.time(
    name: String,
    vararg tags: Pair<String, String>,
    code: () -> T
): T {
    val timer = timer(name, tags.map { Tag.of(it.first, it.second) })
    return timer.record(code)!!
}


/**
 * Incrementing a counter for a payload
 */
fun MeterRegistry.increment(
    payload: IngestionHookPayload,
    name: String,
    vararg additionalTags: Pair<String,String>,
) {
    increment(
        name,
        *payload.metricsTags(*additionalTags)
    )
}

/**
 * Timing for a payload
 */
fun <T> MeterRegistry.timeForPayload(
    payload: IngestionHookPayload,
    name: String,
    code: () -> T
): T = time(
    name,
    *payload.metricsTags()
) {
    code()
}

/**
 * Tags for a payload
 */
private fun IngestionHookPayload.metricsTags(vararg additionalTags: Pair<String,String>) = arrayOf(
    INGESTION_METRIC_EVENT_TAG to gitHubEvent,
    INGESTION_METRIC_OWNER_TAG to (repository?.owner?.login ?: ""),
    INGESTION_METRIC_REPOSITORY_TAG to (repository?.name ?: ""),
) + additionalTags

/**
 * Event tag
 */
const val INGESTION_METRIC_EVENT_TAG = "event"

/**
 * Owner tag
 */
const val INGESTION_METRIC_OWNER_TAG = "owner"

/**
 * Owner tag
 */
const val INGESTION_METRIC_REPOSITORY_TAG = "repository"

/**
 * Routing tag
 */
const val INGESTION_METRIC_ROUTING_TAG = "routing"

/**
 * Queue tag
 */
const val INGESTION_METRIC_QUEUE_TAG = "queue"
