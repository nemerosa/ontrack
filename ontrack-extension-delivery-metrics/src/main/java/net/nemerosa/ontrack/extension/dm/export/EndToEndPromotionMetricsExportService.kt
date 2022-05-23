package net.nemerosa.ontrack.extension.dm.export

import java.time.LocalDateTime

interface EndToEndPromotionMetricsExportService {

    fun exportMetrics(
        branches: String,
        start: LocalDateTime,
        end: LocalDateTime,
        refProject: String? = null,
        targetProject: String? = null,
    )

}