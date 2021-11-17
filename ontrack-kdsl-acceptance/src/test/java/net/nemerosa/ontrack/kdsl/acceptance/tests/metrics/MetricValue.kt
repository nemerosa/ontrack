package net.nemerosa.ontrack.kdsl.acceptance.tests.metrics

data class MetricValue(
    val tags: List<MetricTag> = emptyList(),
    val value: Double,
    val name: String? = null,
) {
    constructor(value: Double, vararg tags: Pair<String, String>) : this(
        tags = tags.map { (name, value) -> MetricTag(name, value) },
        value = value,
    )

    constructor(value: Double, name: String, vararg tags: Pair<String, String>) : this(
        tags = tags.map { (name, value) -> MetricTag(name, value) },
        value = value,
        name = name,
    )
}