package net.nemerosa.ontrack.extension.dm.export

import net.nemerosa.ontrack.extension.dm.model.EndToEndPromotionRecord
import net.nemerosa.ontrack.model.metrics.Metric

interface PromotionMetricsWorker {

    fun process(record: EndToEndPromotionRecord, recorder: (Metric) -> Unit)

}