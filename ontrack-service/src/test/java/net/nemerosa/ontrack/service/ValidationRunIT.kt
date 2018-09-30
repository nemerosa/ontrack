package net.nemerosa.ontrack.service

import net.nemerosa.ontrack.extension.api.support.TestNumberValidationDataType
import net.nemerosa.ontrack.extension.api.support.TestValidationData
import net.nemerosa.ontrack.extension.api.support.TestValidationDataType
import net.nemerosa.ontrack.it.AbstractDSLTestSupport
import net.nemerosa.ontrack.model.exceptions.ValidationRunDataInputException
import net.nemerosa.ontrack.model.exceptions.ValidationRunDataStatusRequiredException
import net.nemerosa.ontrack.model.security.ValidationRunStatusChange
import net.nemerosa.ontrack.model.structure.*
import org.junit.Test
import org.springframework.beans.factory.annotation.Autowired
import kotlin.test.assertEquals
import kotlin.test.assertNotNull
import kotlin.test.assertNull

class ValidationRunIT : AbstractDSLTestSupport() {

    @Autowired
    private lateinit var testValidationDataType: TestValidationDataType
    @Autowired
    private lateinit var testNumberValidationDataType: TestNumberValidationDataType

    @Test
    fun validationRunWithData() {
        project {
            branch {
                val vs = validationStamp(
                        "VS",
                        testValidationDataType.config(null)
                )
                // Build
                build("1.0.0") {
                    // Creates a validation run with data
                    val run = validateWithData(
                            validationStamp = vs,
                            validationDataTypeId = testValidationDataType.descriptor.id,
                            validationRunData = TestValidationData(2, 4, 8)
                    )
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
                    assertEquals(ValidationRunStatusID.STATUS_FAILED.id, status.statusID.id)
                }
            }
        }
    }

    @Test
    fun validationRunWithDataAndForcedStatus() {
        project {
            branch {
                val vs = validationStamp(
                        "VS",
                        testValidationDataType.config(null)
                )
                // Build
                build("1.0.0") {
                    // Creates a validation run with data
                    val run = validateWithData(
                            validationStamp = vs,
                            validationRunStatusID = ValidationRunStatusID.STATUS_FAILED,
                            validationRunData = TestValidationData(0, 0, 8)
                    )
                    // Checks the status
                    assertEquals(ValidationRunStatusID.STATUS_FAILED.id, run.lastStatus.statusID.id)
                }
            }
        }
    }

    @Test
    fun validationRunWithDataAndStatusUpdate() {
        project {
            branch {
                val vs = validationStamp("VS", testValidationDataType.config(null))
                build("1.0.0") {
                    // Creates a validation run with data
                    val run = validateWithData(
                            validationStamp = vs,
                            validationDataTypeId = testValidationDataType.descriptor.id,
                            validationRunStatusID = ValidationRunStatusID.STATUS_FAILED,
                            validationRunData = TestValidationData(0, 0, 10)
                    )
                    // Checks the initial status
                    assertEquals(ValidationRunStatusID.STATUS_FAILED.id, run.lastStatus.statusID.id)
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
                    assertEquals(ValidationRunStatusID.STATUS_DEFECTIVE.id, newRun.lastStatus.statusID.id)
                }
            }
        }
    }

    @Test
    fun validationRunWithoutData() {
        project {
            branch {
                // Creates a validation stamp with no required data
                val vs = validationStamp("VS")
                // Creates a validation run without data
                build("1.0.0") {
                    val run = validate(vs)
                    // Loads the validation run
                    val loadedRun = asUserWithView(branch).call { structureService.getValidationRun(run.id) }
                    // Checks the data
                    val data = loadedRun.data
                    assertNull(data, "No data is loaded")
                    // Checks the status
                    val status = loadedRun.lastStatus
                    assertNotNull(status)
                    assertEquals(ValidationRunStatusID.STATUS_PASSED.id, status.statusID.id)
                }
            }
        }
    }

    @Test
    fun validationRunWithDataAndNoDataTypeOnValidationStamp() {
        project {
            branch {
                // Validation data without data
                val vs = validationStamp("VS")
                // Build
                build("1.0.0") {
                    val run = validateWithData(
                            vs,
                            ValidationRunStatusID.STATUS_PASSED,
                            testValidationDataType.descriptor.id,
                            TestValidationData(0, 10, 100)
                    )
                    assertEquals(
                            ValidationRunStatusID.PASSED,
                            run.lastStatus.statusID.id
                    )
                }
            }
        }

    }

    @Test(expected = ValidationRunDataInputException::class)
    fun validationRunWithInvalidData() {
        project {
            branch {
                val vs = validationStamp(
                        "VS",
                        testValidationDataType.config(null)
                )
                build("1.0.0") {
                    validateWithData(
                            validationStamp = vs,
                            validationDataTypeId = testValidationDataType.descriptor.id,
                            validationRunData = TestValidationData(-1, 0, 0)
                    )
                }
            }
        }
    }

    // FIXME This is not valid
    @Test(expected = ValidationRunDataInputException::class)
    fun validationRunWithUnrequestedData() {
        project {
            branch {
                // Creates a "normal" validation stamp
                val vs = validationStamp("VS")
                // Build
                build("1.0.0") {
                    // Creates a validation run with data
                    validateWithData(
                            validationStamp = vs,
                            validationRunStatusID = ValidationRunStatusID.STATUS_PASSED,
                            validationDataTypeId = testNumberValidationDataType.descriptor.id,
                            validationRunData = 80
                    )
                }
            }
        }
    }

    @Test
    fun validationRunWithMissingDataOKWithStatus() {
        project {
            branch {
                val vs = validationStamp("VS", testNumberValidationDataType.config(50))
                build("1.0.0") {
                    val run = validateWithData<Any>(
                            validationStamp = vs,
                            validationRunStatusID = ValidationRunStatusID.STATUS_PASSED
                    )
                    assertEquals(
                            ValidationRunStatusID.STATUS_PASSED.id,
                            run.lastStatus.statusID.id
                    )
                }
            }
        }
    }

    @Test(expected = ValidationRunDataStatusRequiredException::class)
    fun validationRunWithMissingDataNotOKWithoutStatus() {
        project {
            branch {
                val vs = validationStamp("VS", testNumberValidationDataType.config(50))
                build("1.0.0") {
                    validateWithData<Any>(vs)
                }
            }
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
        val run = asUser().withView(vs).call { structureService.getValidationRun(runId) }

        // Checks it has still no data
        assertNull(run.data, "No data associated with validation run after migration")
    }

    @Test
    fun `Removing a data type from existing validation runs`() {
        project {
            branch {
                // Creates a basic stamp, with some data type
                val vs = validationStamp(
                        "VS",
                        testNumberValidationDataType.config(50)
                )
                // Creates a build
                build("1.0.0") {
                    // ... and validates it with some data
                    val runId: ID = validateWithData(
                            validationStamp = vs,
                            validationRunStatusID = ValidationRunStatusID.STATUS_PASSED,
                            validationDataTypeId = testNumberValidationDataType.descriptor.id,
                            validationRunData = 40
                    ).id
                    // Now, changes the data type for the validation stamp
                    asAdmin().execute {
                        structureService.saveValidationStamp(
                                vs.withDataType(null)
                        )
                    }
                    // Gets the validation run back
                    val run = asUser().withView(vs).call { structureService.getValidationRun(runId) }

                    // Checks it has still some data
                    assertNotNull(run.data, "Data still associated with validation run after migration") {
                        assertEquals(TestNumberValidationDataType::class.qualifiedName, it.descriptor.id)
                        assertEquals(40, it.data as Int)
                    }
                }
            }
        }

    }

    @Test
    fun `Changing a data type for existing validation runs`() {
        project {
            branch {
                // Creates a basic stamp, with some data type
                val vs = validationStamp("VS", testNumberValidationDataType.config(50))
                // Creates a build
                build("1.0.0") {
                    // ... and validates it with some data
                    val runId = validateWithData(
                            validationStamp = vs,
                            validationRunStatusID = ValidationRunStatusID.STATUS_PASSED,
                            validationRunData = 40
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
                    val run = asUser().withView(vs).call { structureService.getValidationRun(runId) }

                    // Checks it has still some data
                    assertNotNull(run.data, "Data still associated with validation run after migration") {
                        assertEquals(TestNumberValidationDataType::class.qualifiedName, it.descriptor.id)
                        assertEquals(40, it.data as Int)
                    }
                }
            }
        }
    }

    @Test
    fun `Validation data still present when validation stamp has no longer a validation data type`() {
        project {
            branch {
                // Creates a basic stamp, with some data type
                val vs = validationStamp("VS", testNumberValidationDataType.config(50))
                // Creates a build
                build("1.0.0") {
                    // ... and validates it with some data
                    val runId = validateWithData(
                            validationStamp = vs,
                            validationRunStatusID = ValidationRunStatusID.STATUS_PASSED,
                            validationDataTypeId = testNumberValidationDataType.descriptor.id,
                            validationRunData = 40
                    ).id
                    // Now, changes the data type for the validation stamp to null
                    asAdmin().execute {
                        structureService.saveValidationStamp(
                                vs.withDataType(null)
                        )
                    }
                    // Gets the validation run back
                    val run = asUser().withView(vs).call { structureService.getValidationRun(runId) }

                    // Checks it has still some data
                    assertNotNull(run.data, "Data still associated with validation run after migration") {
                        assertEquals(TestNumberValidationDataType::class.qualifiedName, it.descriptor.id)
                        assertEquals(40, it.data as Int)
                    }

                }
            }
        }

    }

}