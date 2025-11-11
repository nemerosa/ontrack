package net.nemerosa.ontrack.docs

import net.nemerosa.ontrack.model.annotations.getAPITypeDescription
import net.nemerosa.ontrack.model.docs.getDocumentationExampleCode
import net.nemerosa.ontrack.model.docs.getFieldsDocumentation
import net.nemerosa.ontrack.model.templating.TemplatingRenderableDoc
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired

class TemplatingRenderableDocumentationIT : AbstractDocGenIT() {

    @Autowired
    private lateinit var templatingRenderableDocs: List<TemplatingRenderableDoc>

    @Test
    fun `Templating renderables`() {

        fun getTRDFileId(trd: TemplatingRenderableDoc): String =
            "templating-renderable-${trd.id}"

        fun getTRDTitle(trd: TemplatingRenderableDoc): String =
            "${trd.displayName} (${trd.id})"

        fun generateTRD(directoryContext: DocGenDirectoryContext, trd: TemplatingRenderableDoc) {
            val description = getAPITypeDescription(trd::class)
            val example = getDocumentationExampleCode(trd::class)

            val fileId = getTRDFileId(trd)

            directoryContext.writeFile(
                fileId = fileId,
                level = 5,
                title = getTRDTitle(trd),
                header = description,
                fields = emptyList(),
                example = example,
                extendedConfig = { s ->
                    // Context
                    s.append("Context: ${trd.contextName}\n\n")
                    // Fields
                    s.append("Available fields:\n\n")
                    trd.fields.forEach { field ->
                        s.append("* `${field.name}`: ${field.description}\n\n")
                        field.config?.let {
                            val list = getFieldsDocumentation(it)
                            directoryContext.writeFields(s, list, level = 2)
                        }
                    }
                },
            )
        }


        docGenSupport.inDirectory("templating/renderables") {
            writeIndex(
                fileId = "appendix-templating-renderable-index",
                level = 4,
                title = "List of special templating objects",
                items = templatingRenderableDocs.associate { trd ->
                    getTRDFileId(trd) to getTRDTitle(trd)
                }
            )

            templatingRenderableDocs.forEach { trd ->
                generateTRD(this, trd)
            }

        }
    }

}