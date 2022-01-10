package net.nemerosa.ontrack.extension.github.workflow

import net.nemerosa.ontrack.extension.github.AbstractGitHubTestJUnit4Support
import org.junit.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotNull

class BuildGitHubWorkflowRunPropertyTypeIT : AbstractGitHubTestJUnit4Support() {

    @Test
    fun `Saving and retrieving the property`() {
        project {
            branch {
                build {
                    setProperty(this, BuildGitHubWorkflowRunPropertyType::class.java, BuildGitHubWorkflowRunProperty(
                        runId = 1,
                        url = "some-url",
                        name = "ci",
                        runNumber = 1,
                        running = false,
                    ))
                    assertNotNull(
                        getProperty(this, BuildGitHubWorkflowRunPropertyType::class.java)
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