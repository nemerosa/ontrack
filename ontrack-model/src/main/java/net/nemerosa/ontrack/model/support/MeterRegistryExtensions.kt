package net.nemerosa.ontrack.model.support

import io.micrometer.core.instrument.MeterRegistry
import io.micrometer.core.instrument.Tag

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
