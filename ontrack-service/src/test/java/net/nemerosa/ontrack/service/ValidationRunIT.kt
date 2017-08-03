package net.nemerosa.ontrack.service

import com.fasterxml.jackson.databind.node.IntNode
import com.fasterxml.jackson.databind.node.TextNode
import net.nemerosa.ontrack.it.AbstractServiceTestSupport
import net.nemerosa.ontrack.json.JsonUtils
import net.nemerosa.ontrack.model.exceptions.ValidationRunDataInputException
import net.nemerosa.ontrack.model.security.ValidationRunCreate
import net.nemerosa.ontrack.model.security.ValidationStampCreate
import net.nemerosa.ontrack.model.structure.*
import net.nemerosa.ontrack.test.TestUtils
import org.junit.Assert.*
import org.junit.Test

class ValidationRunIT : AbstractServiceTestSupport() {

    @Test
    fun validationRunWithDataType() {
        // Build & Branch
        val build = doCreateBuild()
        val branch = build.branch
        // Creates a validation stamp with an associated percentage data type w/ threshold
        val vs = asUser().with(branch, ValidationStampCreate::class.java).call {
            structureService.newValidationStamp(
                    ValidationStamp.of(
                            branch,
                            NameDescription.nd("VSPercent", "")
                    ).withDataType(
                            ServiceConfiguration(
                                    ThresholdPercentageValidationDataType::class.java.name,
                                    JsonUtils.format(
                                            ThresholdPercentageValidationDataTypeConfig(60)
                                    )
                            )
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
                    ).withData(
                            ServiceConfiguration(
                                    ThresholdPercentageValidationDataType::class.java.name,
                                    IntNode(80)
                            )
                    )
            )
        }

        // Loads the validation run
        val loadedRun = asUserWithView(branch).call { structureService.getValidationRun(run.id) }

        // Checks the data is still there
        val data = loadedRun.data
        assertNotNull("Data type is loaded", data)
        assertEquals(ThresholdPercentageValidationDataType::class.java.name, data.id)
        TestUtils.assertJsonEquals(
                IntNode(80),
                data.data
        )

        // Checks the status
        val status = loadedRun.lastStatus
        assertNotNull(status)
        assertEquals(ValidationRunStatusID.STATUS_PASSED, status.statusID)
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
        // Build & Branch
        val build = doCreateBuild()
        val branch = build.branch
        // Creates a validation stamp with an associated percentage data type w/ threshold
        val vs = asUser().with(branch, ValidationStampCreate::class.java).call {
            structureService.newValidationStamp(
                    ValidationStamp.of(
                            branch,
                            NameDescription.nd("VSPercent", "")
                    ).withDataType(
                            ServiceConfiguration(
                                    ThresholdPercentageValidationDataType::class.java.name,
                                    JsonUtils.format(
                                            ThresholdPercentageValidationDataTypeConfig(60)
                                    )
                            )
                    )
            )
        }
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
                                    ThresholdPercentageValidationDataType::class.java.name,
                                    IntNode(-1)
                            )
                    )
            )
        }
    }

    @Test(expected = ValidationRunDataInputException::class)
    fun validationRunWithInvalidDataFormat() {
        // Build & Branch
        val build = doCreateBuild()
        val branch = build.branch
        // Creates a validation stamp with an associated percentage data type w/ threshold
        val vs = asUser().with(branch, ValidationStampCreate::class.java).call {
            structureService.newValidationStamp(
                    ValidationStamp.of(
                            branch,
                            NameDescription.nd("VSPercent", "")
                    ).withDataType(
                            ServiceConfiguration(
                                    ThresholdPercentageValidationDataType::class.java.name,
                                    JsonUtils.format(
                                            ThresholdPercentageValidationDataTypeConfig(60)
                                    )
                            )
                    )
            )
        }
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
                                    ThresholdPercentageValidationDataType::class.java.name,
                                    TextNode("test")
                            )
                    )
            )
        }
    }

    @Test(expected = ValidationRunDataInputException::class)
    fun validationRunWithInvalidDataType() {
        // Build & Branch
        val build = doCreateBuild()
        val branch = build.branch
        // Creates a validation stamp with an associated percentage data type w/ threshold
        val vs = asUser().with(branch, ValidationStampCreate::class.java).call {
            structureService.newValidationStamp(
                    ValidationStamp.of(
                            branch,
                            NameDescription.nd("VSPercent", "")
                    ).withDataType(
                            ServiceConfiguration(
                                    ThresholdPercentageValidationDataType::class.java.name,
                                    JsonUtils.format(
                                            ThresholdPercentageValidationDataTypeConfig(60)
                                    )
                            )
                    )
            )
        }
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
                                    ThresholdNumberValidationDataType::class.java.name,
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
                                    ThresholdPercentageValidationDataType::class.java.name,
                                    IntNode(80)
                            )
                    )
            )
        }
    }

    @Test(expected = ValidationRunDataInputException::class)
    fun validationRunWithMissingData() {
        // Build & Branch
        val build = doCreateBuild()
        val branch = build.branch
        // Creates a validation stamp with an associated percentage data type w/ threshold
        val vs = asUser().with(branch, ValidationStampCreate::class.java).call {
            structureService.newValidationStamp(
                    ValidationStamp.of(
                            branch,
                            NameDescription.nd("VSPercent", "")
                    ).withDataType(
                            ServiceConfiguration(
                                    ThresholdPercentageValidationDataType::class.java.name,
                                    JsonUtils.format(
                                            ThresholdPercentageValidationDataTypeConfig(60)
                                    )
                            )
                    )
            )
        }
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