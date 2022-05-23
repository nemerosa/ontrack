package net.nemerosa.ontrack.extension.dm.export

import net.nemerosa.ontrack.extension.dm.model.EndToEndPromotionRecord
import net.nemerosa.ontrack.model.metrics.Metric
import org.springframework.stereotype.Component
import java.time.Duration

@Component
class PromotionLevelSuccessRateMetrics : PromotionMetricsCollector {
    override fun createWorker() = object : PromotionMetricsWorker {
        override fun process(record: EndToEndPromotionRecord, recorder: (Metric) -> Unit) {
            if (record.ref.promotion != null) {
                val refPromotionCreation = record.ref.promotionCreation
                val targetPromotionCreation = record.target.promotionCreation
                val value: Double = if (refPromotionCreation != null) {
                    if (targetPromotionCreation != null) {
                        1.0 // Both ends are promoted
                    } else {
                        0.0 // Only the ref is promoted
                    }
                } else if (targetPromotionCreation != null) {
                    0.0 // Only the target is promoted
                } else {
                    0.0 // No end is promoted
                }
                recorder(
                    Metric(
                        metric = EndToEndPromotionMetrics.PROMOTION_SUCCESS_RATE,
                        tags = EndToEndPromotionMetrics.endToEndMetricTags(record, record.ref.promotion),
                        fields = mapOf("value" to value),
                        timestamp = record.ref.buildCreation
                    )
                )
            }
        }

    }
}