package net.nemerosa.ontrack.graphql.templating

import net.nemerosa.ontrack.graphql.AbstractQLKTITSupport
import org.junit.jupiter.api.Test
import kotlin.test.assertEquals

class TemplatingControllerIT : AbstractQLKTITSupport() {

    @Test
    fun `List of template renderers`() {
        run(
            """
            {
                templatingRenderers {
                    id
                    name
                }
            }
        """
        ) { data ->
            val renderers = data.path("templatingRenderers").associate {
                it.path("id").asText() to it.path("name").asText()
            }
            assertEquals("Text", renderers["text"])
            assertEquals("HTML", renderers["html"])
            assertEquals("Markdown", renderers["markdown"])
        }
    }

}