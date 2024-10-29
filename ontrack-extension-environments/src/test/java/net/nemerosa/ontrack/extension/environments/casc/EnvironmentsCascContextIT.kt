package net.nemerosa.ontrack.extension.environments.casc

import net.nemerosa.ontrack.extension.casc.AbstractCascTestSupport
import net.nemerosa.ontrack.extension.environments.Environment
import net.nemerosa.ontrack.extension.environments.service.EnvironmentService
import net.nemerosa.ontrack.json.asJson
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import kotlin.test.assertEquals
import kotlin.test.assertNotNull
import kotlin.test.assertNull

class EnvironmentsCascContextIT : AbstractCascTestSupport() {

    @Autowired
    private lateinit var environmentService: EnvironmentService

    @Autowired
    private lateinit var environmentsCascContext: EnvironmentsCascContext

    @Test
    fun `Defining environments keeps existing environments by default`() {
        asAdmin {
            deleteAllEnvironments()
            environmentService.save(
                Environment(
                    name = "demo",
                    description = "",
                    order = 50,
                    tags = emptyList(),
                )
            )
            casc(
                """
                ontrack:
                    config:
                        environments:
                            environments:
                                - name: staging
                                  description: Repetition environment
                                  order: 100
                                  tags:
                                    - release
                                - name: production
                                  order: 200
                                  tags:
                                    - release
            """.trimIndent()
            )
            assertNotNull(environmentService.findByName("staging")) {
                assertEquals("Repetition environment", it.description)
                assertEquals(100, it.order)
                assertEquals(listOf("release"), it.tags)
            }
            assertNotNull(environmentService.findByName("production")) {
                assertEquals("", it.description)
                assertEquals(200, it.order)
                assertEquals(listOf("release"), it.tags)
            }
            // Demo environment has been kept
            assertNotNull(environmentService.findByName("demo"), "Demo environment kept")
        }
    }

    @Test
    fun `Defining environments in an authoritative way`() {
        asAdmin {
            deleteAllEnvironments()
            environmentService.save(
                Environment(
                    name = "demo",
                    description = "",
                    order = 50,
                    tags = emptyList(),
                )
            )
            casc(
                """
                ontrack:
                    config:
                        environments:
                            keepEnvironments: false
                            environments:
                                - name: staging
                                  description: Repetition environment
                                  order: 100
                                  tags:
                                    - release
                                - name: production
                                  order: 200
                                  tags:
                                    - release
            """.trimIndent()
            )
            assertNotNull(environmentService.findByName("staging")) {
                assertEquals("Repetition environment", it.description)
                assertEquals(100, it.order)
                assertEquals(listOf("release"), it.tags)
            }
            assertNotNull(environmentService.findByName("production")) {
                assertEquals("", it.description)
                assertEquals(200, it.order)
                assertEquals(listOf("release"), it.tags)
            }
            // Demo environment has been deleted
            assertNull(environmentService.findByName("demo"), "Demo environment deleted")
        }
    }

    @Test
    fun `Rendering environments`() {
        asAdmin {
            deleteAllEnvironments()
            environmentService.save(
                Environment(
                    name = "staging",
                    description = "Repetition environment",
                    order = 100,
                    tags = listOf("release"),
                )
            )
            environmentService.save(
                Environment(
                    name = "production",
                    description = "",
                    order = 200,
                    tags = listOf("release"),
                )
            )
            val json = environmentsCascContext.render()
            assertEquals(
                mapOf(
                    "keepEnvironments" to true,
                    "environments" to listOf(
                        mapOf(
                            "name" to "staging",
                            "description" to "Repetition environment",
                            "order" to 100,
                            "tags" to listOf("release"),
                        ),
                        mapOf(
                            "name" to "production",
                            "description" to "",
                            "order" to 200,
                            "tags" to listOf("release"),
                        ),
                    )
                ).asJson(),
                json
            )
        }
    }

    private fun deleteAllEnvironments() {
        environmentService.findAll().forEach {
            environmentService.delete(it)
        }
    }

}