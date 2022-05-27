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

    @Test
    fun `Build by name`() {
        basicTest(buildIdentification = BuildIdentification.BUILD_NAME)
    }

    private fun basicTest(
        buildIdentification: BuildIdentification = BuildIdentification.RUN_ID,
        runIdProperty: Long = 10,
        runIdQuery: Long = 10,
        buildName: String = "my-build-name",
        expectedLinks: Map<String, String> = mapOf(
            "one" to "build-one",
            "two" to "build-two",
        ),
        asAuth: (code: () -> Unit) -> Unit = { code -> asAdmin { code() } },
    ) {

        val mutationName = when (buildIdentification) {
            BuildIdentification.RUN_ID -> "gitHubIngestionBuildLinksByRunId"
            BuildIdentification.BUILD_NAME -> "gitHubIngestionBuildLinksByBuildName"
        }

        val mutationParams = when (buildIdentification) {
            BuildIdentification.RUN_ID -> "runId: $runIdQuery,"
            BuildIdentification.BUILD_NAME -> """buildName: "$buildName","""
        }

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
                        build(buildName) {
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
                                        $mutationName(input: {
                                            owner: "nemerosa",
                                            repository: "${project.name}",
                                            $mutationParams
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
                                    checkGraphQLUserErrors(data, mutationName) { node ->
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

    enum class BuildIdentification {
        RUN_ID,
        BUILD_NAME,
        // TODO BUILD_LABEL,
    }

}