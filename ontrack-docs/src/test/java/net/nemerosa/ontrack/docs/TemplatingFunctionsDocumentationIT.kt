package net.nemerosa.ontrack.docs

import net.nemerosa.ontrack.model.annotations.getAPITypeDescription
import net.nemerosa.ontrack.model.docs.getDocumentationExampleCode
import net.nemerosa.ontrack.model.docs.getFieldsDocumentation
import net.nemerosa.ontrack.model.templating.TemplatingFunction
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired

class TemplatingFunctionsDocumentationIT : AbstractDocGenIT() {

    @Autowired
    private lateinit var templatingFunctions: List<TemplatingFunction>

    @Test
    fun `Templating functions generation`() {
        docGenSupport.inDirectory("templating/functions") {

            writeIndex(
                fileId = "appendix-templating-functions-index",
                level = 4,
                title = "List of templating functions",
                items = templatingFunctions.associate { templatingFunction ->
                    getTemplatingFunctionFileId(templatingFunction) to templatingFunction.id
                }
            )

            templatingFunctions.forEach { templatingFunction ->
                generateTemplatingFunction(this, templatingFunction)
            }
        }
    }

    private fun generateTemplatingFunction(
        directoryContext: DocGenDirectoryContext,
        templatingFunction: TemplatingFunction
    ) {
        val id = templatingFunction.id
        val description = getAPITypeDescription(templatingFunction::class)
        val parameters = getFieldsDocumentation(templatingFunction::class)
        val example = getDocumentationExampleCode(templatingFunction::class)

        val fileId = getTemplatingFunctionFileId(templatingFunction)

        directoryContext.writeFile(
            fileId = fileId,
            level = 5,
            title = id,
            header = description,
            fields = parameters,
            example = example,
        )
    }

    private fun getTemplatingFunctionFileId(templatingFunction: TemplatingFunction) =
        "templating-function-${templatingFunction.id}"

}