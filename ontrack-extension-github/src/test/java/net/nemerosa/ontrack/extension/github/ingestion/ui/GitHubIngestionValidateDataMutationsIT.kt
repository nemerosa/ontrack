package net.nemerosa.ontrack.extension.github.ingestion.ui

import net.nemerosa.ontrack.common.getOrNull
import net.nemerosa.ontrack.extension.api.support.TestNumberValidationDataType
import net.nemerosa.ontrack.extension.general.ReleaseProperty
import net.nemerosa.ontrack.extension.general.ReleasePropertyType
import net.nemerosa.ontrack.extension.github.ingestion.AbstractIngestionTestSupport
import net.nemerosa.ontrack.extension.github.workflow.BuildGitHubWorkflowRunProperty
import net.nemerosa.ontrack.extension.github.workflow.BuildGitHubWorkflowRunPropertyType
import net.nemerosa.ontrack.json.getRequiredTextField
import net.nemerosa.ontrack.model.security.Roles
import net.nemerosa.ontrack.model.structure.ValidationRunStatusID
import net.nemerosa.ontrack.model.structure.config
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.fail
import org.springframework.beans.factory.annotation.Autowired
import kotlin.test.assertEquals
import kotlin.test.assertNotNull
import kotlin.test.assertNull
import kotlin.test.assertTrue

internal class GitHubIngestionValidateDataMutationsIT : AbstractIngestionTestSupport() {

    @Autowired
    private lateinit var testNumberValidationDataType: TestNumberValidationDataType

    @Test
    fun `Automation users can set the validation data`() {
        basicTest { code ->
            asAccountWithGlobalRole(Roles.GLOBAL_AUTOMATION) {
                code()
            }
        }
    }

    @Test
    fun `Build by ID, no prior validation run`() {
        basicTest { code ->
            asAdmin {
                code()
            }
        }
    }

    private fun basicTest(
        asAuth: (code: () -> Unit) -> Unit,
    ) {
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
                                )
                            )
                            asAuth {
                                run("""
                                    mutation {
                                        gitHubIngestionValidateDataByRunId(input: {
                                            owner: "nemerosa",
                                            repository: "${project.name}",
                                            validation: "test",
                                            validationData: {
                                                type: "${TestNumberValidationDataType::class.java.name}",
                                                data: {
                                                    value: 50
                                                }
                                            },
                                            validationStatus: "PASSED",
                                            runId: 10,
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
                                """) { data ->
                                    checkGraphQLUserErrors(data, "gitHubIngestionValidateDataByRunId") { node ->
                                        val uuid = node.path("payload").getRequiredTextField("uuid")
                                        assertTrue(uuid.isNotBlank(), "UUID has been returned")
                                    }
                                    asAdmin {
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
                                            val runData = it.data?.data
                                            assertEquals(50, runData, "Validation run data has been set")
                                        }
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
        asAdmin {
            withGitHubIngestionSettings {
                project {
                    branch {
                        build {
                            setProperty(this, BuildGitHubWorkflowRunPropertyType::class.java,
                                BuildGitHubWorkflowRunProperty(
                                    runId = 11, // Will not be found
                                    url = "",
                                    name = "some-workflow",
                                    runNumber = 1,
                                    running = true,
                                )
                            )
                            run("""
                                mutation {
                                    gitHubIngestionValidateDataByRunId(input: {
                                        owner: "nemerosa",
                                        repository: "${project.name}",
                                        validation: "test",
                                        validationData: {
                                            type: "${TestNumberValidationDataType::class.java.name}",
                                            data: {
                                                value: 50
                                            }
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
                                // Checks the validation stamp has not been created
                                assertNull(
                                    structureService.findValidationStampByName(
                                        project.name,
                                        branch.name,
                                        "test"
                                    ).getOrNull(),
                                    "Validation stamp has not been created"
                                )
                            }
                        }
                    }
                }
            }
        }
    }

    @Test
    fun `Build by name, no prior validation run`() {
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
                                )
                            )
                            run("""
                                mutation {
                                    gitHubIngestionValidateDataByBuildName(input: {
                                        owner: "nemerosa",
                                        repository: "${project.name}",
                                        validation: "test",
                                        validationData: {
                                            type: "${TestNumberValidationDataType::class.java.name}",
                                            data: {
                                                value: 50
                                            }
                                        },
                                        validationStatus: "PASSED",
                                        buildName: "$name"
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
                                    val runData = it.data?.data
                                    assertEquals(50, runData, "Validation run data has been set")
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    @Test
    fun `Build by label, no prior validation run`() {
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
                                )
                            )
                            setProperty(this, ReleasePropertyType::class.java,
                                ReleaseProperty("1.0.0")
                            )
                            run("""
                                mutation {
                                    gitHubIngestionValidateDataByBuildLabel(input: {
                                        owner: "nemerosa",
                                        repository: "${project.name}",
                                        validation: "test",
                                        validationData: {
                                            type: "${TestNumberValidationDataType::class.java.name}",
                                            data: {
                                                value: 50
                                            }
                                        },
                                        validationStatus: "PASSED",
                                        buildLabel: "1.0.0"
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
                                    val runData = it.data?.data
                                    assertEquals(50, runData, "Validation run data has been set")
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    @Test
    fun `Build by ID, no prior validation run, data validation`() {
        asAdmin {
            withGitHubIngestionSettings {
                project {
                    branch {
                        val vs = validationStamp(
                            name = "test",
                            validationDataTypeConfig = testNumberValidationDataType.config(100)
                        )
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
                                            data: {
                                                value: 50
                                            }
                                        },
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
                                // Checks the build has been validated
                                val run = structureService.getValidationRunsForBuildAndValidationStamp(
                                    buildId = id,
                                    validationStampId = vs.id,
                                    offset = 0,
                                    count = 1,
                                ).firstOrNull()
                                assertNotNull(run, "Validation run created") {
                                    assertEquals(
                                        ValidationRunStatusID.FAILED,
                                        it.lastStatusId
                                    )
                                    val runData = it.data?.data
                                    assertEquals(50, runData, "Validation run data has been set")
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    @Test
    fun `Build by ID, existing validation run`() {
        asAdmin {
            withGitHubIngestionSettings {
                project {
                    branch {
                        val vs = validationStamp("test")
                        build {
                            setProperty(this, BuildGitHubWorkflowRunPropertyType::class.java,
                                BuildGitHubWorkflowRunProperty(
                                    runId = 10,
                                    url = "",
                                    name = "some-workflow",
                                    runNumber = 1,
                                    running = true,
                                ))
                            // Existing validation (without any data)
                            validate(vs)
                            // Setting the data
                            run("""
                                mutation {
                                    gitHubIngestionValidateDataByRunId(input: {
                                        owner: "nemerosa",
                                        repository: "${project.name}",
                                        validation: "test",
                                        validationData: {
                                            type: "${TestNumberValidationDataType::class.java.name}",
                                            data: {
                                                value: 50
                                            }
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
                                    val runData = it.data?.data
                                    assertEquals(50, runData, "Validation run data has been set")
                                }
                            }
                        }
                    }
                }
            }
        }
    }

}