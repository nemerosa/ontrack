package net.nemerosa.ontrack.docs

import net.nemerosa.ontrack.model.metrics.MetricsMeterDocumentation
import net.nemerosa.ontrack.model.metrics.MetricsMeterType
import org.junit.jupiter.api.Test
import kotlin.reflect.full.findAnnotation
import kotlin.test.assertEquals

class DocumentationUtilsTest {

    @Test
    fun `Listing fields of a Kotlin object`() {
        val fields = getObjectFields<MetricsMeterDocumentation>(SampleDoc)
        assertEquals(
            listOf(
                ObjectField(
                    name = "queuingMetric",
                    description = null,
                    annotation = SampleDoc.Queuing::queuingMetric.findAnnotation<MetricsMeterDocumentation>(),
                    path = listOf("Queuing"),
                    value = "queue_metrics"
                ),
                ObjectField(
                    name = "schedulingMetric",
                    description = null,
                    annotation = SampleDoc.Settings.Scheduling::schedulingMetric.findAnnotation<MetricsMeterDocumentation>(),
                    path = listOf("Settings", "Scheduling"),
                    value = "scheduling_metrics"
                ),
            ),
            fields
        )
    }

}

@Suppress("ConstPropertyName")
object SampleDoc {

    object Settings {

        object Scheduling {
            @MetricsMeterDocumentation(
                type = MetricsMeterType.COUNT
            )
            const val schedulingMetric = "scheduling_metrics"
        }

    }

    object Queuing {
        @MetricsMeterDocumentation(
            type = MetricsMeterType.COUNT
        )
        const val queuingMetric = "queue_metrics"
    }

}