package net.nemerosa.ontrack.extension.dm.export

import net.nemerosa.ontrack.extension.dm.model.EndToEndPromotionRecord
import net.nemerosa.ontrack.model.metrics.Metric
import org.springframework.stereotype.Component
import java.time.Duration

@Component
class PromotionLevelLeadTimeMetrics : PromotionMetricsCollector {
    override fun createWorker() = object : PromotionMetricsWorker {
        override fun process(record: EndToEndPromotionRecord, recorder: (Metric) -> Unit) {
            val refPromotionCreation = record.ref.promotionCreation
            val targetPromotionCreation = record.target.promotionCreation
            if (record.ref.promotion != null && refPromotionCreation != null && targetPromotionCreation != null) {
                val maxPromotionTime = maxOf(refPromotionCreation, targetPromotionCreation)
                val time = Duration.between(record.ref.buildCreation, maxPromotionTime)
                val value = time.toSeconds().toDouble()
                recorder(
                    Metric(
                        metric = EndToEndPromotionMetrics.PROMOTION_LEAD_TIME,
                        tags = EndToEndPromotionMetrics.endToEndMetricTags(record, record.ref.promotion),
                        fields = mapOf("value" to value),
                        timestamp = record.ref.buildCreation
                    )
                )
            }
        }

    }
}