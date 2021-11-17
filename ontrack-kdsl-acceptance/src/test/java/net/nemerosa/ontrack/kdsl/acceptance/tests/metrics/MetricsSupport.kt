package net.nemerosa.ontrack.kdsl.acceptance.tests.metrics

/**
 * Getting informations about the metrics in Ontrack.
 */
object MetricsSupport {

    /**
     * Parses Prometheus metrics from Ontrack
     */
    fun parseMetrics(body: String): MetricCollection {
        var metric = ""
        var help = ""
        var type = ""

        val metrics = mutableListOf<Metric>()
        val values = mutableListOf<MetricValue>()

        fun flushMetric() {
            if (values.isNotEmpty()) {
                metrics += Metric(
                    name = metric,
                    type = type,
                    help = help,
                    values = values.toList(),
                )
                values.clear()
            }
        }

        body.lines().forEach { rawLine ->
            val line = rawLine.trim()
            if (line.startsWith(HELP)) {
                flushMetric()
                val content = line.removePrefix(HELP).trim()
                if (" " in content) {
                    metric = content.substringBefore(" ")
                    help = content.substringAfter(" ").trim()
                } else {
                    metric = content
                    help = ""
                }
                flushMetric()
            } else if (line.startsWith(TYPE)) {
                flushMetric()
                val content = line.removePrefix(TYPE).trim()
                val typeMetric = content.substringBefore(" ")
                if (typeMetric != metric) {
                    help = ""
                }
                type = content.substringAfter(" ").trim()
            } else {
                val tokens = line.substringBefore(" ")
                val value = line.substringAfter(" ").toDoubleOrNull() ?: 0.0

                val localName: String
                val tags = mutableListOf<MetricTag>()
                val tagsStart = tokens.trim().indexOf("{")
                val tagsEnd = tokens.trim().indexOf("}")
                if (tagsStart in 1 until tagsEnd) {
                    localName = tokens.substring(0, tagsStart).trim()
                    tokens.substring(tagsStart + 1, tagsEnd).trim()
                        .split(",")
                        .forEach { tagToken ->
                            val tagParts = tagToken.split("=").map { it.trim() }
                            if (tagParts.size == 2) {
                                val tagName = tagParts[0].trim()
                                val tagValue = tagParts[1].removePrefix("\"").removeSuffix("\"").trim()
                                tags += MetricTag(tagName, tagValue)
                            }
                        }
                } else {
                    localName = tokens.trim()
                }

                values += MetricValue(
                    tags = tags.toList(),
                    value = value,
                    name = localName.takeIf { it != metric },
                )
            }
        }

        flushMetric()

        return MetricCollection(list = metrics.toList())
    }

    private const val HELP = "# HELP"
    private const val TYPE = "# TYPE"

}