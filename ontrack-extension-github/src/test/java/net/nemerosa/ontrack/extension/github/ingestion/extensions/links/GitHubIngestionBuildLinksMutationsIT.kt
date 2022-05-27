package net.nemerosa.ontrack.extension.github.ingestion.extensions.links

import net.nemerosa.ontrack.extension.general.ReleaseProperty
import net.nemerosa.ontrack.extension.general.ReleasePropertyType
import net.nemerosa.ontrack.extension.github.ingestion.AbstractIngestionTestSupport
import net.nemerosa.ontrack.extension.github.workflow.BuildGitHubWorkflowRunProperty
import net.nemerosa.ontrack.extension.github.workflow.BuildGitHubWorkflowRunPropertyType
import net.nemerosa.ontrack.json.getRequiredTextField
import net.nemerosa.ontrack.model.security.Roles
import org.junit.jupiter.api.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

internal class GitHubIngestionBuildLinksMutationsIT : AbstractIngestionTestSupport() {

    @Test
    fun `Automation users can set the build links`() {
        basicTest { code ->
            asAccountWithGlobalRole(Roles.GLOBAL_AUTOMATION) {
                code()
            }
        }
    }

    @Test
    fun `Build by ID`() {
        basicTest()
    }

    private fun basicTest(
        runIdProperty: Long = 10,
        runIdQuery: Long = 10,
        expectedLinks: Map<String, String> = mapOf(
            "one" to "build-one",
            "two" to "build-two",
        ),
        asAuth: (code: () -> Unit) -> Unit = { code -> asAdmin { code() } },
    ) {
        asAdmin {
            withGitHubIngestionSettings {
                val targets = mapOf(
                    "one" to project {
                        branch {
                            build(name = "build-one")
                        }
                    },
                    "two" to project {
                        branch {
                            build(name = "build-two") {
                                setProperty(
                                    this,
                                    ReleasePropertyType::class.java,
                                    ReleaseProperty("2.0.0")
                                )
                            }
                        }
                    }
                )
                project {
                    branch {
                        build {
                            setProperty(
                                this, BuildGitHubWorkflowRunPropertyType::class.java,
                                BuildGitHubWorkflowRunProperty(
                                    runId = runIdProperty,
                                    url = "",
                                    name = "some-workflow",
                                    runNumber = 1,
                                    running = true,
                                )
                            )
                            asAuth {
                                run(
                                    """
                                    mutation {
                                        gitHubIngestionBuildLinksByRunId(input: {
                                            owner: "nemerosa",
                                            repository: "${project.name}",
                                            runId: $runIdQuery,
                                            buildLinks: [
                                            {
                                                project: "${targets["one"]?.name}",
                                                buildRef: "build-one"
                                            },
                                            {
                                                project: "${targets["two"]?.name}",
                                                buildRef: "#2.0.0"
                                            }
                                            ]
                                        }) {
                                            payload {
                                                uuid
                                            }
                                            errors {
                                                message
                                                exception
                                                location
                                            }
                                        }
                                    }
                                """
                                ) { data ->
                                    checkGraphQLUserErrors(data, "gitHubIngestionBuildLinksByRunId") { node ->
                                        val uuid = node.path("payload").getRequiredTextField("uuid")
                                        assertTrue(uuid.isNotBlank(), "UUID has been returned")
                                    }
                                    asAdmin {
                                        val links = structureService.getBuildsUsedBy(this).pageItems.map {
                                            it.project.name to it.name
                                        }.toMap()
                                        assertEquals(
                                            expectedLinks.mapKeys { (key, _) ->
                                                targets[key]?.name ?: ""
                                            },
                                            links
                                        )
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    @Test
    fun `Build by ID not found`() {
        basicTest(
            runIdProperty = 10,
            runIdQuery = 11,
            expectedLinks = emptyMap(),
        )
    }

}