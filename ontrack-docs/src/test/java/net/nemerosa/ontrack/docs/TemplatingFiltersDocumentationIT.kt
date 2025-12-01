package net.nemerosa.ontrack.docs

import net.nemerosa.ontrack.model.annotations.getAPITypeDescription
import net.nemerosa.ontrack.model.docs.getDocumentationExampleCode
import net.nemerosa.ontrack.model.templating.TemplatingFilter
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired

class TemplatingFiltersDocumentationIT : AbstractDocGenIT() {

    @Autowired
    private lateinit var templatingFilters: List<TemplatingFilter>

    @Test
    fun `Templating filters generation`() {
        docGenSupport.inDirectory("templating/filters") {

            writeFile(
                fileName = "index",
            ) { s ->
                s.title("List of templating filters.")
                for (templatingFilter in templatingFilters) {
                    s.tocItem(templatingFilter.id, fileName = "${getTemplatingFilterFileId(templatingFilter)}.md")
                }
            }

            templatingFilters.forEach { templatingFilter ->
                generateTemplatingFilter(this, templatingFilter)
            }
        }
    }

    private fun generateTemplatingFilter(directoryContext: DocGenDirectoryContext, templatingFilter: TemplatingFilter) {
        val id = templatingFilter.id
        val description = getAPITypeDescription(templatingFilter::class)
        val example = getDocumentationExampleCode(templatingFilter::class)

        val fileId = getTemplatingFilterFileId(templatingFilter)

        directoryContext.writeFile(
            fileId = fileId,
            title = id,
            header = description,
            fields = emptyList(),
            example = example,
            links = emptyList(),
            linksPrefix = "../../../",
        )
    }

    private fun getTemplatingFilterFileId(templatingFilter: TemplatingFilter) =
        "templating-filter-${templatingFilter.id}"

}