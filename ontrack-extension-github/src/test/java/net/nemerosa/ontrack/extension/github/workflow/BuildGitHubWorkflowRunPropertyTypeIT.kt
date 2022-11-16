package net.nemerosa.ontrack.extension.github.workflow

import net.nemerosa.ontrack.extension.github.AbstractGitHubTestSupport
import org.junit.jupiter.api.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotNull

class BuildGitHubWorkflowRunPropertyTypeIT : AbstractGitHubTestSupport() {

    @Test
    fun `Saving and retrieving the property`() {
        project {
            branch {
                build {
                    setProperty(
                        this, BuildGitHubWorkflowRunPropertyType::class.java, BuildGitHubWorkflowRunProperty(
                            workflows = listOf(
                                BuildGitHubWorkflowRun(
                                    runId = 1,
                                    url = "some-url",
                                    name = "ci",
                                    runNumber = 1,
                                    running = false,
                                    event = "push",
                                )
                            )
                        )
                    )
                    assertNotNull(
                        getProperty(this, BuildGitHubWorkflowRunPropertyType::class.java)
                            ?.workflows?.firstOrNull()
                    ) { property ->
                        assertEquals(1, property.runId)
                        assertEquals("some-url", property.url)
                        assertEquals("ci", property.name)
                        assertEquals(1, property.runNumber)
                        assertEquals(false, property.running)
                    }
                }
            }
        }
    }

}