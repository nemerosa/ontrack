package net.nemerosa.ontrack.extension.github.workflow

import net.nemerosa.ontrack.extension.github.AbstractGitHubTestSupport
import org.junit.jupiter.api.Test
import kotlin.random.Random
import kotlin.test.assertEquals
import kotlin.test.assertNotNull

class BuildGitHubWorkflowRunPropertyTypeIT : AbstractGitHubTestSupport() {

    @Test
    fun `Searching for a build using its run IDs`() {
        asAdmin {
            project {
                branch {
                    build {
                        // Two random Ids
                        val id1 = Random.nextLong()
                        val id2 = Random.nextLong()
                        // ... and another for a negative test
                        val id3 = Random.nextLong()
                        // Setting two run IDs from different workflows
                        setProperty(
                            this, BuildGitHubWorkflowRunPropertyType::class.java, BuildGitHubWorkflowRunProperty(
                                workflows = listOf(
                                    BuildGitHubWorkflowRun(
                                        runId = id1,
                                        url = "url:one/10",
                                        name = "one",
                                        runNumber = 1,
                                        running = false,
                                        event = "push",
                                    ),
                                    BuildGitHubWorkflowRun(
                                        runId = id2,
                                        url = "url:two/20",
                                        name = "two",
                                        runNumber = 1,
                                        running = false,
                                        event = "push",
                                    ),
                                )
                            )
                        )
                        // Looking for the build using valid run IDs
                        assertEquals(
                            id,
                            propertyService.findBuildByBranchAndSearchkey(
                                branch.id,
                                BuildGitHubWorkflowRunPropertyType::class.java,
                                id1.toString()
                            )
                        )
                        assertEquals(
                            id,
                            propertyService.findBuildByBranchAndSearchkey(
                                branch.id,
                                BuildGitHubWorkflowRunPropertyType::class.java,
                                id2.toString()
                            )
                        )
                        // Negative test
                        assertEquals(
                            null,
                            propertyService.findBuildByBranchAndSearchkey(
                                branch.id,
                                BuildGitHubWorkflowRunPropertyType::class.java,
                                id3.toString()
                            )
                        )
                    }
                }
            }
        }
    }

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