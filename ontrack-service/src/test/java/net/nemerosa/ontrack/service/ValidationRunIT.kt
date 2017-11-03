package net.nemerosa.ontrack.service

import net.nemerosa.ontrack.extension.api.support.TestNumberValidationDataType
import net.nemerosa.ontrack.extension.api.support.TestValidationData
import net.nemerosa.ontrack.extension.api.support.TestValidationDataType
import net.nemerosa.ontrack.it.AbstractServiceTestSupport
import net.nemerosa.ontrack.model.exceptions.ValidationRunDataInputException
import net.nemerosa.ontrack.model.security.ValidationRunCreate
import net.nemerosa.ontrack.model.security.ValidationRunStatusChange
import net.nemerosa.ontrack.model.security.ValidationStampCreate
import net.nemerosa.ontrack.model.structure.*
import org.junit.Before
import org.junit.Test
import org.springframework.beans.factory.annotation.Autowired
import kotlin.test.assertEquals
import kotlin.test.assertNotNull
import kotlin.test.assertNull

class ValidationRunIT : AbstractServiceTestSupport() {

    private lateinit var branch: Branch
    private lateinit var build: Build
    private lateinit var vs: ValidationStamp

    @Autowired
    private lateinit var testValidationDataType: TestValidationDataType
    @Autowired
    private lateinit var testNumberValidationDataType: TestNumberValidationDataType

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
                    ).withDataType(testValidationDataType.config(null))
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
        @Suppress("UNCHECKED_CAST")
        val data: ValidationRunData<TestValidationData> = loadedRun.data as ValidationRunData<TestValidationData>
        assertNotNull(data, "Data type is loaded")
        assertEquals(TestValidationDataType::class.java.name, data.descriptor.id)
        assertEquals(2, data.data.critical)
        assertEquals(4, data.data.high)
        assertEquals(8, data.data.medium)

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
                            testValidationDataType.data(
                                    TestValidationData(
                                            critical = 2,
                                            high = 4,
                                            medium = 8
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
        assertNull(data, "No data is loaded")

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
                            testValidationDataType.data(
                                    TestValidationData(-1, 0, 0)
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
                            testNumberValidationDataType.data(80)
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

    @Test
    fun `Introducing a data type on existing validation runs`() {
        // Creates a basic stamp, with no data type
        val vs = doCreateValidationStamp()
        // Creates a build
        val build = doCreateBuild(vs.branch, NameDescription.nd("1", ""))
        // ... and validates it
        val runId = doValidateBuild(build, vs, ValidationRunStatusID.STATUS_PASSED).id

        // Now, changes the data type for the validation stamp
        asAdmin().execute {
            structureService.saveValidationStamp(
                    vs.withDataType(
                            testNumberValidationDataType.config(50)
                    )
            )
        }

        // Gets the validation run back
        val run = structureService.getValidationRun(runId)

        // Checks it has still no data
        assertNull(run.data, "No data associated with validation run after migration")
    }

    @Test
    fun `Removing a data type from existing validation runs`() {
        // Creates a basic stamp, with some data type
        val vs = doCreateValidationStamp(
                testNumberValidationDataType.config(50)
        )
        // Creates a build
        val build = doCreateBuild(vs.branch, NameDescription.nd("1", ""))
        // ... and validates it with some data
        val runId = doValidateBuild(build, vs, ValidationRunStatusID.STATUS_PASSED,
                testNumberValidationDataType.data(40)
        ).id

        // Now, changes the data type for the validation stamp
        asAdmin().execute {
            structureService.saveValidationStamp(
                    vs.withDataType(null)
            )
        }

        // Gets the validation run back
        val run = structureService.getValidationRun(runId)

        // Checks it has still some data
        assertNotNull(run.data, "Data still associated with validation run after migration") {
            assertEquals(TestNumberValidationDataType::class.qualifiedName, it.descriptor.id)
            assertEquals(40, it.data as Int)
        }
    }

    @Test
    fun `Changing a data type for existing validation runs`() {
        // Creates a basic stamp, with some data type
        val vs = doCreateValidationStamp(
                testNumberValidationDataType.config(50)
        )
        // Creates a build
        val build = doCreateBuild(vs.branch, NameDescription.nd("1", ""))
        // ... and validates it with some data
        val runId = doValidateBuild(build, vs, ValidationRunStatusID.STATUS_PASSED,
                testNumberValidationDataType.data(40)
        ).id

        // Now, changes the data type for the validation stamp
        asAdmin().execute {
            structureService.saveValidationStamp(
                    vs.withDataType(
                            testValidationDataType.config(null)
                    )
            )
        }

        // Gets the validation run back
        val run = structureService.getValidationRun(runId)

        // Checks it has still some data
        assertNotNull(run.data, "Data still associated with validation run after migration") {
            assertEquals(TestNumberValidationDataType::class.qualifiedName, it.descriptor.id)
            assertEquals(40, it.data as Int)
        }
    }

}