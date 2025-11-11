package net.nemerosa.ontrack.docs

import net.nemerosa.ontrack.model.annotations.getAPITypeDescription
import net.nemerosa.ontrack.model.docs.DocumentationIgnore
import net.nemerosa.ontrack.model.docs.DocumentationQualifier
import net.nemerosa.ontrack.model.docs.getDocumentationExampleCode
import net.nemerosa.ontrack.model.docs.getFieldsDocumentation
import net.nemerosa.ontrack.model.templating.TemplatingSource
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import kotlin.reflect.full.findAnnotation
import kotlin.reflect.full.hasAnnotation
import kotlin.test.fail

class TemplatingSourcesDocumentationIT : AbstractDocGenIT() {

    @Autowired
    private lateinit var templatingSources: List<TemplatingSource>


    @Test
    fun `Templating sources generation`() {
        docGenSupport.inDirectory("templating/sources") {

            writeIndex(
                fileId = "appendix-templating-sources-index",
                level = 4,
                title = "List of templating sources",
                items = templatingSources.associate { templatingSource ->
                    getTemplatingSourceFileId(templatingSource) to getTemplatingSourceTitle(templatingSource)
                }
            )

            templatingSources.forEach { templatingSource ->
                generateTemplatingSource(this, templatingSource)
            }
        }
    }

    private fun generateTemplatingSource(directoryContext: DocGenDirectoryContext, templatingSource: TemplatingSource) {
        val description = getAPITypeDescription(templatingSource::class)
        val parameters = if (templatingSource::class.hasAnnotation<DocumentationIgnore>()) {
            emptyList()
        } else {
            try {
                getFieldsDocumentation(templatingSource::class)
            } catch (any: Exception) {
                fail(
                    message = "Error getting parameters for templating source: ${templatingSource::class.java.name}",
                    cause = any,
                )
            }
        }
        val example = getDocumentationExampleCode(templatingSource::class)
        val types = templatingSource.types

        val fileId = getTemplatingSourceFileId(templatingSource)

        directoryContext.writeFile(
            fileId = fileId,
            level = 5,
            title = getTemplatingSourceTitle(templatingSource),
            header = description,
            fields = parameters,
            example = example,
            extendedHeader = { s ->
                s.append("Applicable for:\n\n")
                types.forEach { type ->
                    s.append("* ").append(type.displayName).append("\n")
                }
                s.append("\n")
            }
        )
    }

    private fun getTemplatingSourceFileId(templatingSource: TemplatingSource): String {
        val qualifier = templatingSource::class.findAnnotation<DocumentationQualifier>()?.value
        if (qualifier.isNullOrBlank()) {
            return "templating-source-${templatingSource.field}"
        } else {
            return "templating-source-${qualifier}-${templatingSource.field}"
        }
    }

    private fun getTemplatingSourceTitle(templatingSource: TemplatingSource): String {
        val qualifier = templatingSource::class.findAnnotation<DocumentationQualifier>()
        if (qualifier == null) {
            return templatingSource.field
        } else {
            return "${qualifier.name}.${templatingSource.field}"
        }
    }

}