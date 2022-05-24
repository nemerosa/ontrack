package net.nemerosa.ontrack.extension.dm.export

import net.nemerosa.ontrack.extension.dm.model.EndToEndPromotionRecord
import net.nemerosa.ontrack.model.metrics.Metric
import org.springframework.stereotype.Component
import java.time.Duration

@Component
class PromotionLevelTTRMetrics : PromotionMetricsCollector {

    override fun createWorker(): PromotionMetricsWorker {
        val indexes = mutableMapOf<Reference, EndToEndPromotionRecord>()
        return object : PromotionMetricsWorker {
            override fun process(record: EndToEndPromotionRecord, recorder: (Metric) -> Unit) {
                if (record.ref.promotion != null) {
                    // Checks the indexes
                    val reference = Reference(
                        refProject = record.ref.project,
                        refBranch = record.ref.branch,
                        refPromotion = record.ref.promotion,
                        project = record.target.project,
                        branch = record.target.branch
                    )
                    val previousRecord = indexes[reference]
                    // If we have a previous reference
                    if (previousRecord != null) {
                        // Are both ends of the dependency promoted?
                        if (record.ref.promotionCreation != null && record.target.promotionCreation != null) {
                            // The issues are fixed :)
                            // We can emit the metric...
                            val maxPromotionTime = maxOf(record.ref.promotionCreation, record.target.promotionCreation)
                            val elapsedTime = Duration.between(previousRecord.ref.buildCreation, maxPromotionTime)
                            val value = elapsedTime.toSeconds().toDouble()
                            recorder(
                                Metric(
                                    metric = EndToEndPromotionMetrics.PROMOTION_TTR,
                                    tags = EndToEndPromotionMetrics.endToEndMetricTags(record, record.ref.promotion),
                                    fields = mapOf(
                                        "value" to value
                                    ),
                                    timestamp = maxPromotionTime
                                )
                            )
                            // ... and remove the previous record
                            indexes.remove(reference)
                        } else {
                            // Adding a reference
                            indexes[reference] = record
                            // ... and going forward with more recent records
                        }
                    }
                    // We don't have a previous reference
                    else {
                        // Are both ends of the dependency promoted?
                        if (record.ref.promotionCreation != null && record.target.promotionCreation != null) {
                            // We don't need to record anything, all is good
                        } else {
                            // Adding a reference
                            indexes[reference] = record
                            // ... and going forward with more recent records
                        }
                    }
                }
            }

        }
    }

    /**
     * Index for references
     */
    private data class Reference(
        val refProject: String,
        val refBranch: String,
        val refPromotion: String,
        val project: String,
        val branch: String,
    )
}