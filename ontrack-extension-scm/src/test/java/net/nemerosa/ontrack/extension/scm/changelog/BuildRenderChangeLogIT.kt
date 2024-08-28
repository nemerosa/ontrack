package net.nemerosa.ontrack.extension.scm.changelog

import org.junit.jupiter.api.Test
import kotlin.test.assertEquals

class BuildRenderChangeLogIT: AbstractSCMChangeLogTestSupport() {

    /**
     * Rendering a change log using the GraphQL Build.render field.
     */
    @Test
    fun `Build render change log`() {
        prepareChangeLogTestCase { _, from, to ->
            run(
                """
                    query BuildChangeLog(
                        ${'$'}buildToId: Int!,
                        ${'$'}template: String!,
                    ) {
                        build(id: ${'$'}buildToId) {
                            render(
                                format: "plain",
                                template: ${'$'}template
                            )
                        }
                    }
                """,
                mapOf(
                    "buildToId" to to.id(),
                    "template" to """
                        ${'$'}{build.changelog?from=${from.id}}
                    """.trimIndent()
                )
            ) { data ->
                val render = data.path("build").path("render").asText()
                assertEquals(
                    """
                        * ISS-21 Some new feature
                        * ISS-22 Some fixes are needed
                        * ISS-23 Some nicer UI
                        """.trimIndent(),
                    render
                )
            }
        }
    }

}