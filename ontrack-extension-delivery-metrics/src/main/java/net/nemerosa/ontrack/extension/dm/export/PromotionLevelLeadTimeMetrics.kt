package net.nemerosa.ontrack.extension.dm.export

import net.nemerosa.ontrack.extension.dm.model.EndToEndPromotionRecord
import net.nemerosa.ontrack.model.metrics.Metric
import org.springframework.stereotype.Component
import java.time.Duration
import java.time.LocalDateTime

@Component
class PromotionLevelLeadTimeMetrics : PromotionMetricsCollector {
    override fun createWorker() = object : PromotionMetricsWorker {
        override fun process(record: EndToEndPromotionRecord, recorder: (Metric) -> Unit) {
            recordTime(record)?.let { (timestamp, duration) ->
                val value = duration.toSeconds().toDouble()
                recorder(
                    Metric(
                        metric = EndToEndPromotionMetrics.PROMOTION_LEAD_TIME,
                        tags = EndToEndPromotionMetrics.endToEndMetricTags(record, record.ref.promotion!!),
                        fields = mapOf("value" to value),
                        timestamp = timestamp
                    )
                )
            }
        }

    }

    final fun recordTime(record: EndToEndPromotionRecord): Pair<LocalDateTime, Duration>? {
        val refPromotionCreation = record.ref.promotionCreation
        val targetPromotionCreation = record.target.promotionCreation
        return if (record.ref.promotion != null && refPromotionCreation != null && targetPromotionCreation != null) {
            val maxPromotionTime = maxOf(refPromotionCreation, targetPromotionCreation)
            return record.ref.buildCreation to Duration.between(record.ref.buildCreation, maxPromotionTime)
        } else {
            null
        }
    }
}