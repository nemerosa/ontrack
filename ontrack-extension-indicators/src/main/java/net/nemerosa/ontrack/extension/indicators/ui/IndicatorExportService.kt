package net.nemerosa.ontrack.extension.indicators.ui

import net.nemerosa.ontrack.common.Document
import net.nemerosa.ontrack.extension.indicators.model.IndicatorReportingFilter
import net.nemerosa.ontrack.extension.indicators.model.IndicatorType

interface IndicatorExportService {

    fun exportCSV(filter: IndicatorReportingFilter, types: List<IndicatorType<*, *>>): Document

}