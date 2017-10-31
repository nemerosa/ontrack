package net.nemerosa.ontrack.service

import com.fasterxml.jackson.databind.node.IntNode
import com.fasterxml.jackson.databind.node.TextNode
import net.nemerosa.ontrack.extension.api.support.TestNumberValidationDataType
import net.nemerosa.ontrack.extension.api.support.TestValidationData
import net.nemerosa.ontrack.extension.api.support.TestValidationDataType
import net.nemerosa.ontrack.it.AbstractServiceTestSupport
import net.nemerosa.ontrack.json.JsonParseException
import net.nemerosa.ontrack.json.JsonUtils
import net.nemerosa.ontrack.model.exceptions.ValidationRunDataInputException
import net.nemerosa.ontrack.model.security.ValidationRunCreate
import net.nemerosa.ontrack.model.security.ValidationRunStatusChange
import net.nemerosa.ontrack.model.security.ValidationStampCreate
import net.nemerosa.ontrack.model.structure.*
import net.nemerosa.ontrack.test.TestUtils
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test

class ValidationRunIT : AbstractServiceTestSupport() {

    private lateinit var branch: Branch
    private lateinit var build: Build
    private lateinit var vs: ValidationStamp

    @Before
    fun init() {
        // Build & Branch
        build = doCreateBuild()
        branch = build.branch
        // Creates a validation stamp with an associated percentage data type w/ threshold
        vs = asUser().with(branch, ValidationStampCreate::class.java).call {
            structureService.newValidationStamp(
                    ValidationStamp.of(
                            branch,
                            NameDescription.nd("VSPercent", "")
                    ).withDataType(
                            ServiceConfiguration(
                                    TestValidationDataType::class.java.name,
                                    JsonUtils.format(
                                            TestValidationData(
                                                    critical = 1,
                                                    high = 0,
                                                    medium = 0
                                            )
                                    )
                            )
                    )
            )
        }
    }

    @Test
    fun validationRunWithData() {
        // Creates a validation run
        val run = createValidationRunWithData()

        // Loads the validation run
        val loadedRun = asUserWithView(branch).call { structureService.getValidationRun(run.id) }

        // Checks the data is still there
        val data = loadedRun.data
        assertNotNull("Data type is loaded", data)
        assertEquals(TestValidationDataType::class.java.name, data.id)
        TestUtils.assertJsonEquals(
                JsonUtils.`object`()
                        .with("critical", 2)
                        .with("high", 4)
                        .with("medium", 8)
                        .end(),
                data.data
        )

        // Checks the status
        val status = loadedRun.lastStatus
        assertNotNull(status)
        assertEquals(ValidationRunStatusID.STATUS_PASSED, status.statusID)
    }

    @Test
    fun validationRunWithDataAndStatusUpdate() {
        // Creates a validation run with data
        val run = createValidationRunWithData(ValidationRunStatusID.STATUS_FAILED)
        // Checks the initial status
        val status = run.lastStatus
        assertNotNull(status)
        assertEquals(ValidationRunStatusID.STATUS_FAILED.id, status.statusID.id)
        // Updates the status
        asUser().with(branch, ValidationRunStatusChange::class.java).execute {
            structureService.newValidationRunStatus(
                    run,
                    ValidationRunStatus.of(
                            Signature.of("test"),
                            ValidationRunStatusID.STATUS_DEFECTIVE,
                            "This is a defect"
                    )
            )
        }
        // Reloads
        val newRun = asUser().withView(branch).call {
            structureService.getValidationRun(run.id)
        }
        // Checks the new status
        val newStatus = newRun.lastStatus
        assertNotNull(newStatus)
        assertEquals(ValidationRunStatusID.STATUS_DEFECTIVE.id, newStatus.statusID.id)
    }

    private fun createValidationRunWithData(statusId: ValidationRunStatusID = ValidationRunStatusID.STATUS_PASSED): ValidationRun {
        return asUser().with(branch, ValidationRunCreate::class.java).call {
            structureService.newValidationRun(
                    ValidationRun.of(
                            build,
                            vs,
                            1,
                            Signature.of("test"),
                            statusId,
                            ""
                    ).withData(
                            ServiceConfiguration(
                                    TestValidationDataType::class.java.name,
                                    JsonUtils.format(
                                            TestValidationData(
                                                    critical = 2,
                                                    high = 4,
                                                    medium = 8
                                            )
                                    )
                            )
                    )
            )
        }
    }

    @Test
    fun validationRunWithoutData() {
        // Build & Branch
        val build = doCreateBuild()
        val branch = build.branch
        // Creates a validation stamp with no required data
        val vs = asUser().with(branch, ValidationStampCreate::class.java).call {
            structureService.newValidationStamp(
                    ValidationStamp.of(
                            branch,
                            NameDescription.nd("VSNormal", "")
                    )
            )
        }
        // Creates a validation run
        val run = asUser().with(branch, ValidationRunCreate::class.java).call {
            structureService.newValidationRun(
                    ValidationRun.of(
                            build,
                            vs,
                            1,
                            Signature.of("test"),
                            ValidationRunStatusID.STATUS_PASSED,
                            ""
                    )
            )
        }

        // Loads the validation run
        val loadedRun = asUserWithView(branch).call { structureService.getValidationRun(run.id) }

        // Checks the data
        val data = loadedRun.data
        assertNull("No data is loaded", data)

        // Checks the status
        val status = loadedRun.lastStatus
        assertNotNull(status)
        assertEquals(ValidationRunStatusID.STATUS_PASSED, status.statusID)
    }

    @Test(expected = ValidationRunDataInputException::class)
    fun validationRunWithInvalidData() {
        // Creates a validation run
        asUser().with(branch, ValidationRunCreate::class.java).call {
            structureService.newValidationRun(
                    ValidationRun.of(
                            build,
                            vs,
                            1,
                            Signature.of("test"),
                            ValidationRunStatusID.STATUS_PASSED,
                            ""
                    ).withData(
                            ServiceConfiguration(
                                    TestValidationDataType::class.java.name,
                                    JsonUtils.`object`().with("critical", -1).end()
                            )
                    )
            )
        }
    }

    @Test(expected = ValidationRunDataInputException::class)
    fun validationRunWithInvalidDataFormat() {
        // Creates a validation run
        asUser().with(branch, ValidationRunCreate::class.java).call {
            structureService.newValidationRun(
                    ValidationRun.of(
                            build,
                            vs,
                            1,
                            Signature.of("test"),
                            ValidationRunStatusID.STATUS_PASSED,
                            ""
                    ).withData(
                            ServiceConfiguration(
                                    TestValidationDataType::class.java.name,
                                    TextNode("test")
                            )
                    )
            )
        }
    }

    @Test(expected = ValidationRunDataInputException::class)
    fun validationRunWithInvalidDataType() {
        // Creates a validation run
        asUser().with(branch, ValidationRunCreate::class.java).call {
            structureService.newValidationRun(
                    ValidationRun.of(
                            build,
                            vs,
                            1,
                            Signature.of("test"),
                            ValidationRunStatusID.STATUS_PASSED,
                            ""
                    ).withData(
                            ServiceConfiguration(
                                    // Wrong type
                                    TestNumberValidationDataType::class.java.name,
                                    IntNode(80)
                            )
                    )
            )
        }
    }

    @Test(expected = ValidationRunDataInputException::class)
    fun validationRunWithUnrequestedData() {
        // Build & Branch
        val build = doCreateBuild()
        val branch = build.branch
        // Creates a "normal" validation stamp
        val vs = asUser().with(branch, ValidationStampCreate::class.java).call {
            structureService.newValidationStamp(
                    ValidationStamp.of(
                            branch,
                            NameDescription.nd("VSPercent", "")
                    )
            )
        }
        // Creates a validation run with data
        asUser().with(branch, ValidationRunCreate::class.java).call {
            structureService.newValidationRun(
                    ValidationRun.of(
                            build,
                            vs,
                            1,
                            Signature.of("test"),
                            ValidationRunStatusID.STATUS_PASSED,
                            ""
                    ).withData(
                            ServiceConfiguration(
                                    TestNumberValidationDataType::class.java.name,
                                    IntNode(80)
                            )
                    )
            )
        }
    }

    @Test(expected = ValidationRunDataInputException::class)
    fun validationRunWithMissingData() {
        // Creates a validation run with no data
        asUser().with(branch, ValidationRunCreate::class.java).call {
            structureService.newValidationRun(
                    ValidationRun.of(
                            build,
                            vs,
                            1,
                            Signature.of("test"),
                            ValidationRunStatusID.STATUS_PASSED,
                            ""
                    )
            )
        }
    }

}