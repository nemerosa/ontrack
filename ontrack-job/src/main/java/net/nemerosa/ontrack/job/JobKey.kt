package net.nemerosa.ontrack.job

import io.micrometer.core.instrument.Tag

data class JobKey(
        val type: JobType,
        val id: String
) {

    fun sameType(type: JobType): Boolean {
        return this.type == type
    }

    fun sameCategory(category: JobCategory): Boolean {
        return this.type.category == category
    }

    override fun toString(): String {
        return String.format(
                "%s[%s]",
                type,
                id
        )
    }

    val metricTags: List<Tag>
        get() = listOf(
                Tag.of("job-id", id),
                Tag.of("job-type", type.key),
                Tag.of("job-category", type.category.key)
        )


    companion object {
        @JvmStatic
        fun of(type: JobType, id: String): JobKey {
            return JobKey(type, id)
        }
    }
}
