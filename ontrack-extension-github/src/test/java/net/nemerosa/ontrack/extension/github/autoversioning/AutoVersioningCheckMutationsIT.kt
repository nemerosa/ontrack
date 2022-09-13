package net.nemerosa.ontrack.extension.github.autoversioning

import net.nemerosa.ontrack.extension.av.config.AutoVersioningConfigurationService
import net.nemerosa.ontrack.extension.av.setAutoVersioning
import net.nemerosa.ontrack.extension.general.autoValidationStampProperty
import net.nemerosa.ontrack.extension.github.ingestion.AbstractIngestionTestSupport
import net.nemerosa.ontrack.extension.github.workflow.BuildGitHubWorkflowRunProperty
import net.nemerosa.ontrack.extension.github.workflow.BuildGitHubWorkflowRunPropertyType
import net.nemerosa.ontrack.json.getRequiredTextField
import net.nemerosa.ontrack.model.structure.Build
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import kotlin.test.assertEquals
import kotlin.test.assertNotNull
import kotlin.test.assertTrue

internal class AutoVersioningCheckMutationsIT : AbstractIngestionTestSupport() {

    @Autowired
    protected lateinit var autoVersioningConfigurationService: AutoVersioningConfigurationService

    @Test
    fun `Checking the auto versioning`() {
        asAdmin {
            withGitHubIngestionSettings {

                val (linkedBuild430, linkedBuild431) = project<Pair<Build, Build>> {
                    branch<Pair<Build, Build>>("main") {
                        val gold = promotionLevel("GOLD")
                        val b430 = build("4.3.0")
                        b430.promote(gold)
                        val b431 = build("4.3.1")
                        b431.promote(gold)
                        b430 to b431
                    }
                }

                project {
                    branch {
                        val vs = validationStamp()
                        autoValidationStampProperty(project, autoCreateIfNotPredefined = true)
                        autoVersioningConfigurationService.setAutoVersioning(this) {
                            autoVersioningConfig {
                                project = linkedBuild430.project.name
                                branch = linkedBuild430.branch.name
                                promotion = "GOLD"
                                validationStamp = vs.name
                            }
                        }
                        val build = build()
                        setProperty(
                            build,
                            BuildGitHubWorkflowRunPropertyType::class.java,
                            BuildGitHubWorkflowRunProperty(
                                runId = 1,
                                url = "",
                                name = "some-workflow",
                                runNumber = 1,
                                running = true,
                            )
                        )
                        build.linkTo(linkedBuild431)

                        run(
                            """
                            mutation {
                                gitHubCheckAutoVersioningByRunId(input: {
                                    owner: "nemerosa",
                                    repository: "${project.name}",
                                    runId: 1,
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
                            checkGraphQLUserErrors(data, "gitHubCheckAutoVersioningByRunId") { node ->
                                val uuid = node.path("payload").getRequiredTextField("uuid")
                                assertTrue(uuid.isNotBlank(), "UUID has been returned")
                            }
                            val run =
                                structureService.getValidationRunsForBuildAndValidationStamp(build.id, vs.id, 0, 1)
                                    .firstOrNull()
                            assertNotNull(run, "Validation has been created") {
                                assertEquals("PASSED", it.lastStatusId, "Validation passed")
                            }
                        }
                    }
                }
            }
        }
    }

}