package net.nemerosa.ontrack.extension.dm.export

import net.nemerosa.ontrack.extension.dm.data.EndToEndPromotionFilter
import net.nemerosa.ontrack.extension.dm.data.EndToEndPromotionsHelper
import net.nemerosa.ontrack.model.metrics.Metric
import net.nemerosa.ontrack.model.metrics.MetricsExportService
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.time.LocalDateTime

@Service
@Transactional
class DefaultEndToEndPromotionMetricsExportService(
    private val endToEndPromotionsHelper: EndToEndPromotionsHelper,
    private val promotionMetricsCollectors: List<PromotionMetricsCollector>,
    private val metricsExportService: MetricsExportService,
) : EndToEndPromotionMetricsExportService {

    override fun exportMetrics(
        branches: String,
        start: LocalDateTime,
        end: LocalDateTime,
        refProject: String?,
        targetProject: String?,
    ) {
        val regex = branches.toRegex()
        val filter = EndToEndPromotionFilter(
            afterTime = start,
            beforeTime = end,
            buildOrder = true,
            refProject = refProject,
            targetProject = targetProject,
        )
        val workers = promotionMetricsCollectors.map {
            it.createWorker()
        }
        val cache = mutableListOf<Metric>()
        endToEndPromotionsHelper.forEachEndToEndPromotionRecord(filter) { record ->
            if (regex.matches(record.ref.branch) && regex.matches(record.target.branch)) {
                workers.forEach { worker ->
                    worker.process(record) { metric ->
                        cache += metric
                        flushMetrics(cache, force = false)
                    }
                }
            }
        }
        flushMetrics(cache, force = true)
    }

    private fun flushMetrics(cache: MutableList<Metric>, force: Boolean) {
        if (cache.isNotEmpty() && (cache.size >= 1024 || force)) {
            metricsExportService.batchExportMetrics(cache)
            cache.clear()
        }
    }

}