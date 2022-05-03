package net.nemerosa.ontrack.extension.github.ingestion.ui

import net.nemerosa.ontrack.common.getOrNull
import net.nemerosa.ontrack.extension.api.support.TestNumberValidationDataType
import net.nemerosa.ontrack.extension.github.ingestion.AbstractIngestionTestSupport
import net.nemerosa.ontrack.extension.github.workflow.BuildGitHubWorkflowRunProperty
import net.nemerosa.ontrack.extension.github.workflow.BuildGitHubWorkflowRunPropertyType
import net.nemerosa.ontrack.model.structure.ValidationRunStatusID
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.fail
import kotlin.test.assertEquals
import kotlin.test.assertNotNull

internal class GitHubIngestionValidateDataMutationsIT : AbstractIngestionTestSupport() {

    @Test
    fun `Data validation without any prior run using build by run ID`() {
        asAdmin {
            withGitHubIngestionSettings {
                project {
                    branch {
                        build {
                            setProperty(this, BuildGitHubWorkflowRunPropertyType::class.java,
                                BuildGitHubWorkflowRunProperty(
                                    runId = 10,
                                    url = "",
                                    name = "some-workflow",
                                    runNumber = 1,
                                    running = true,
                                ))
                            run("""
                                mutation {
                                    gitHubIngestionValidateDataByRunId(input: {
                                        owner: "nemerosa",
                                        repository: "${project.name}",
                                        validation: "test",
                                        validationData: {
                                            type: "${TestNumberValidationDataType::class.java.name}",
                                            data: 50
                                        },
                                        validationStatus: "PASSED",
                                        runId: 10,
                                    }) {
                                        errors {
                                            message
                                            exception
                                            location
                                        }
                                    }
                                }
                            """) { data ->
                                checkGraphQLUserErrors(data, "gitHubIngestionValidateDataByRunId")
                                // Checks the validation stamp has been created
                                val vs = structureService.findValidationStampByName(
                                    project.name,
                                    branch.name,
                                    "test"
                                ).getOrNull() ?: fail("Validation stamp not created")
                                // Checks the build has been validated
                                val run = structureService.getValidationRunsForBuildAndValidationStamp(
                                    buildId = id,
                                    validationStampId = vs.id,
                                    offset = 0,
                                    count = 1,
                                ).firstOrNull()
                                assertNotNull(run, "Validation run created") {
                                    assertEquals(
                                        ValidationRunStatusID.PASSED,
                                        it.lastStatusId
                                    )
                                    val data = it.data?.data
                                    assertEquals(50, data, "Validation run data has been set")
                                }
                            }
                        }
                    }
                }
            }
        }
    }

}