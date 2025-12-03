package net.nemerosa.ontrack.docs

import net.nemerosa.ontrack.extension.config.ci.conditions.Condition
import net.nemerosa.ontrack.model.annotations.getAPITypeDescription
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired

/**
 * Generation of the documentation for the CI Configuration conditions.
 */
class CIConfigConditionsDocumentationIT : AbstractDocGenIT() {

    @Autowired
    private lateinit var conditions: List<Condition>

    @Test
    fun `CI Configuration conditions`() {
        docGenSupport.inDirectory("ci-config/conditions") {

            writeFile(
                fileName = "index",
            ) { s ->
                s.title("List of CI Configuration conditions")
                for (condition in conditions.sortedBy { it.name }) {
                    val id = condition.name
                    s.tocItem(id, fileName = "${id}.md", description = getAPITypeDescription(condition::class))
                }
            }

            conditions.forEach { condition ->
                generateCondition(this, condition)
            }

        }
    }

    private fun generateCondition(
        directoryContext: DocGenDirectoryContext,
        condition: Condition
    ) {
        val id = condition.name

        directoryContext.writeFile(
            fileId = id,
            title = id,
        ) { s ->
            // Description
            val description = getAPITypeDescription(condition::class)
            if (!description.isNullOrBlank()) {
                s.paragraph(description)
            }

            s.configuration(condition.schema, description = condition.schemaDescription)
            s.example(condition::class)

        }
    }

}