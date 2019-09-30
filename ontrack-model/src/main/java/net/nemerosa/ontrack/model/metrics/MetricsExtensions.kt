package net.nemerosa.ontrack.model.metrics

import io.micrometer.core.instrument.MeterRegistry
import io.micrometer.core.instrument.Tag

/**
 * Utility method to increment a counter metric.
 *
 * @param name Name of the counter
 * @param tags List of tags to associate with the metric.
 */
fun MeterRegistry.increment(
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
fun <T> MeterRegistry.time(
        name: String,
        vararg tags: Pair<String, String>,
        code: () -> T
): T {
    val timer = timer(name, tags.map { Tag.of(it.first, it.second) })
    return timer.record(code)
}


/**
 * Utility method to collect REB metrics
 *
 * @param T Return type of the code to measure
 * @param started Name of the started counter
 * @param success Name of the success counter
 * @param error Name of the error counter
 * @param time Name of the timer metric
 * @param tags List of tags to associate with the metric.
 * @param code Code whose execution duration must be recorded
 * @return Value returned by the code being measured
 */
fun <T> MeterRegistry.measure(
        started: String,
        success: String,
        error: String,
        time: String,
        tags: Map<String, String>,
        code: () -> T
): T {
    val actualTags = tags.toList().toTypedArray()
    increment(started, *actualTags)
    return try {
        time(time, *actualTags) {
            val result = code()
            increment(success, *actualTags)
            result
        }
    } catch (ex: Exception) {
        increment(error, *actualTags)
        throw ex
    }
}
