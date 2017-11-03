package net.nemerosa.ontrack.boot.ui

import net.nemerosa.ontrack.extension.api.support.TestNumberValidationDataType
import net.nemerosa.ontrack.json.JsonUtils
import net.nemerosa.ontrack.model.exceptions.ValidationRunStatusRequiredException
import net.nemerosa.ontrack.model.security.ValidationRunCreate
import net.nemerosa.ontrack.model.security.ValidationStampCreate
import net.nemerosa.ontrack.model.structure.*
import org.junit.Test
import org.springframework.beans.factory.annotation.Autowired
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith
import kotlin.test.assertNotNull
import kotlin.test.assertNull

class ValidationRunControllerIT : AbstractWebTestSupport() {

    @Autowired
    private lateinit var validationRunController: ValidationRunController

    @Autowired
    private lateinit var testNumberValidationDataType: TestNumberValidationDataType

    @Test
    fun `Validation with no required data`() {
        // Creates a validation stamp with no data type
        val vs = doCreateValidationStamp()
        // Creates a build
        val build = doCreateBuild(vs.branch, NameDescription.nd("1", ""))
        // Validates the build
        val run = asUser().with(vs, ValidationRunCreate::class.java).call {
            validationRunController.newValidationRun(
                    build.id,
                    ValidationRunRequest(
                            null,
                            ServiceConfiguration(vs.name, null),
                            null,
                            "PASSED",
                            "No description"
                    )
            )
        }
        // Checks the run has no data
        assertNull(run.data, "Validation run has no data")
    }

    @Test
    fun `Validation with required data and status being passed`() {
        // Creates a validation stamp with a percentage type
        val branch = doCreateBranch()
        val vs = asUser().with(branch.getProject().id(), ValidationStampCreate::class.java).call({
            structureService.newValidationStamp(
                    ValidationStamp.of(
                            branch,
                            NameDescription.nd("VS1", "")
                    ).withDataType(
                            testNumberValidationDataType.config(60)
                    )
            )
        }
        )
        // Creates a build
        val build = doCreateBuild(branch, NameDescription.nd("1", ""))
        // Validates the build with some data, and a fixed status
        val run = asUser().with(vs, ValidationRunCreate::class.java).call {
            validationRunController.newValidationRun(
                    build.id,
                    ValidationRunRequest(
                            null,
                            ServiceConfiguration(
                                    vs.name,
                                    JsonUtils.format(mapOf("value" to 70))
                            ),
                            null,
                            "FAILED",
                            "No description"
                    )
            )
        }
        // Checks the run has some data
        @Suppress("UNCHECKED_CAST")
        val data: ValidationRunData<Int> = run.data as ValidationRunData<Int>
        assertNotNull(data, "Validation run has some data")
        assertEquals(TestNumberValidationDataType::class.qualifiedName, data.descriptor.id)
        assertEquals(70, data.data)
    }

    @Test
    fun `Validation with no data type and with status`() {
        // Creates a validation stamp
        val vs = doCreateValidationStamp()
        // Creates a build
        val build = doCreateBuild(vs.branch, NameDescription.nd("1", ""))
        // Validates the build
        val run = asUser().with(vs, ValidationRunCreate::class.java).call {
            validationRunController.newValidationRun(
                    build.id,
                    ValidationRunRequest(
                            null,
                            ServiceConfiguration(
                                    vs.name,
                                    null
                            ),
                            null,
                            "FAILED",
                            "No description"
                    )
            )
        }
        // Checks the status
        assertEquals("FAILED", run.lastStatus.statusID.id)
    }

    @Test
    fun `Status is required when no data type is associated with the validation stamp`() {
        // Creates a validation stamp
        val vs = doCreateValidationStamp()
        // Creates a build
        val build = doCreateBuild(vs.branch, NameDescription.nd("1", ""))
        // Validates the build
        assertFailsWith<ValidationRunStatusRequiredException> {
            asUser().with(vs, ValidationRunCreate::class.java).call {
                validationRunController.newValidationRun(
                        build.id,
                        ValidationRunRequest(
                                null,
                                ServiceConfiguration(
                                        vs.name,
                                        null
                                ),
                                null,
                                null,
                                "No description"
                        )
                )
            }
        }.apply {
            assertEquals("Status is required for validation stamp ${vs.name}.", message)
        }
    }

}