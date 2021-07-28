package net.nemerosa.ontrack.extension.indicators.ui

import com.opencsv.CSVWriter
import net.nemerosa.ontrack.common.Document
import net.nemerosa.ontrack.extension.indicators.model.IndicatorReportingFilter
import net.nemerosa.ontrack.extension.indicators.model.IndicatorReportingService
import net.nemerosa.ontrack.extension.indicators.model.IndicatorType
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.io.StringWriter

@Service
@Transactional
class IndicatorExportServiceImpl(
    private val indicatorReportingService: IndicatorReportingService
): IndicatorExportService {

    override fun exportCSV(filter: IndicatorReportingFilter, types: List<IndicatorType<*, *>>): Document {
        // Reporting
        val report = indicatorReportingService.report(filter, types)
        // Output
        val output = StringWriter()
        val csvWriter = CSVWriter(output)
        // Headers
        val titles = listOf("project") + types.map { it.id }
        csvWriter.writeNext(titles.toTypedArray())
        // Lines
        for (item in report.items) {
            val row = mutableListOf<String>()
            row += item.project.name
            for (indicator in item.indicators) {
                val representation = indicator.toClientString()
                row += representation
            }
            csvWriter.writeNext(row.toTypedArray())
        }
        csvWriter.close()
        // As document
        return Document("text/plain", output.toString().toByteArray())
    }

}