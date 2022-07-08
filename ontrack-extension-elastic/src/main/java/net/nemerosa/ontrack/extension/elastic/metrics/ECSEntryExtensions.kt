package net.nemerosa.ontrack.extension.elastic.metrics

import net.nemerosa.ontrack.model.metrics.Metric

/**
 * Converting an Ontrack metric to an ECS entry.
 */
fun Metric.toECSEntry() = ECSEntry(
    timestamp = timestamp,
    event = ECSEvent(
        category = metric,
    ),
    labels = tags,
    ontrack = fields,
)
