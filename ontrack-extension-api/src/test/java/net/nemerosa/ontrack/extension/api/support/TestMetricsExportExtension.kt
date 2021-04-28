package net.nemerosa.ontrack.extension.api.support

import net.nemerosa.ontrack.common.RunProfile
import net.nemerosa.ontrack.extension.api.MetricsExportExtension
import net.nemerosa.ontrack.extension.support.AbstractExtension
import org.springframework.context.annotation.Profile
import org.springframework.stereotype.Component
import java.time.LocalDateTime
import java.time.temporal.ChronoUnit
import kotlin.test.assertTrue

@Component
@Profile(RunProfile.UNIT_TEST)
class TestMetricsExportExtension(
    extensionFeature: TestExtensionFeature
) : AbstractExtension(extensionFeature), MetricsExportExtension {

    val data = mutableListOf<MetricsData>()

    private var enabled = false

    fun clear() {
        data.clear()
    }

    override fun exportMetrics(
        metric: String,
        tags: Map<String, String>,
        fields: Map<String, Double>,
        timestamp: LocalDateTime?
    ) {
        if (enabled) {
            data += MetricsData(
                metric = metric,
                tags = tags,
                fields = fields,
                timestamp = timestamp
            )
        }
    }

    operator fun contains(item: MetricsData): Boolean =
        data.any {
            it.metric == item.metric &&
                    item.tags in it.tags &&
                    item.fields in it.fields &&
                    (item.timestamp == null || it.timestamp == null || item.timestamp.truncatedTo(ChronoUnit.SECONDS) == it.timestamp.truncatedTo(
                        ChronoUnit.SECONDS))

        }

    operator fun <T> Map<String, T>.contains(other: Map<String, T>): Boolean =
        this.toList().containsAll(other.toList())

    fun <T> with(code: () -> T): T {
        val old = enabled
        try {
            enabled = true
            clear()
            return code()
        } finally {
            enabled = old
        }
    }

    fun assertHasMetric(
        metric: String,
        tags: Map<String, String> = emptyMap(),
        fields: Map<String, Double> = emptyMap(),
        timestamp: LocalDateTime? = null
    ) {
        val item = MetricsData(
            metric, tags, fields, timestamp
        )
        assertTrue(
            item in this,
            "Expected metric:\n\n - $item\n\nin list:\n${data.joinToString("\n") { " - $it" }}"
        )
    }
}

data class MetricsData(
    val metric: String,
    val tags: Map<String, String>,
    val fields: Map<String, Double>,
    val timestamp: LocalDateTime?
) {
    override fun toString(): String {
        return "MetricsData(metric='$metric', tags=$tags, fields=$fields, timestamp=$timestamp)"
    }
}