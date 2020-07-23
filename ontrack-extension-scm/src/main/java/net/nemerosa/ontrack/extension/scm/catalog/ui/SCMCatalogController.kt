package net.nemerosa.ontrack.extension.scm.catalog.ui

import net.nemerosa.ontrack.common.Document
import net.nemerosa.ontrack.extension.scm.catalog.SCMCatalog
import net.nemerosa.ontrack.extension.scm.catalog.SCMCatalogEntry
import net.nemerosa.ontrack.extension.scm.catalog.SCMCatalogExportService
import net.nemerosa.ontrack.ui.controller.AbstractResourceController
import net.nemerosa.ontrack.ui.resource.Resources
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import org.springframework.web.servlet.mvc.method.annotation.MvcUriComponentsBuilder.on
import javax.servlet.http.HttpServletResponse

@RestController
@RequestMapping("extension/scm/catalog")
class SCMCatalogController(
        private val scmCatalog: SCMCatalog,
        private val scmCatalogExportService: SCMCatalogExportService
) : AbstractResourceController() {

    /**
     * Gets the entries of the catalog
     */
    @GetMapping("entries")
    fun entries(): Resources<SCMCatalogEntry> =
            Resources.of(
                    scmCatalog.catalogEntries.toList(),
                    uri(on(SCMCatalogController::class.java).entries())
            )

    @GetMapping("export/csv")
    fun exportAsCsv(response: HttpServletResponse): Document {
        // Gets the list of all entries
        val entries = scmCatalog.catalogEntries.toList()
        // Attachment
        response.addHeader("Content-Disposition", "attachment; filename=ontrack-scm-catalog.csv")
        // Export as CSV
        return scmCatalogExportService.exportCatalogAsCSV(entries)
    }

}