package net.nemerosa.ontrack.service.job

import io.micrometer.core.instrument.Tag
import net.nemerosa.ontrack.job.JobKey

val JobKey.metricTags: List<Tag>
    get() = listOf(
            Tag.of("job-id", id),
            Tag.of("job-type", type.key),
            Tag.of("job-category", type.category.key)
    )
