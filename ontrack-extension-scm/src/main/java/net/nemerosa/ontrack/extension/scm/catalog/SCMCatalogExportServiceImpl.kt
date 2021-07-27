package net.nemerosa.ontrack.extension.scm.catalog

import com.opencsv.CSVWriter
import net.nemerosa.ontrack.common.Document
import org.springframework.stereotype.Service
import java.io.StringWriter
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

@Service
class SCMCatalogExportServiceImpl : SCMCatalogExportService {

    override fun exportCatalogAsCSV(entries: List<SCMCatalogEntry>): Document {
        // Output
        val output = StringWriter()
        val csvWriter = CSVWriter(output)
        csvWriter.writeNext(
            arrayOf(
                "scm",
                "config",
                "repository",
                "lastActivity",
                "createdAt",
                "timestamp",
                "teams"
            )
        )
        entries.forEach { entry ->
            csvWriter.writeNext(
                arrayOf(
                    entry.scm,
                    entry.config,
                    entry.repository,
                    formatDateTime(entry.lastActivity),
                    formatDateTime(entry.createdAt),
                    formatDateTime(entry.timestamp),
                    formatTeams(entry.teams)
                )
            )
        }
        csvWriter.close()
        // As document
        return Document("text/plain", output.toString().toByteArray())
    }

    private fun formatTeams(teams: List<SCMCatalogTeam>?): String =
        teams?.joinToString(",") { it.id } ?: ""

    private fun formatDateTime(localDateTime: LocalDateTime?): String =
        localDateTime?.format(DateTimeFormatter.ISO_DATE_TIME) ?: ""

}