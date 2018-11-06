package net.nemerosa.ontrack.extension.general

import net.nemerosa.ontrack.extension.general.validation.FractionValidationData
import net.nemerosa.ontrack.extension.general.validation.FractionValidationDataType
import net.nemerosa.ontrack.extension.general.validation.ThresholdConfig
import net.nemerosa.ontrack.it.AbstractDSLTestSupport
import net.nemerosa.ontrack.model.settings.PredefinedValidationStampService
import net.nemerosa.ontrack.model.structure.*
import net.nemerosa.ontrack.test.TestUtils
import net.nemerosa.ontrack.test.assertIs
import org.junit.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Component
import kotlin.test.assertEquals
import kotlin.test.assertNotNull

@Component
class ValidationRunWithAutoStampIT : AbstractDSLTestSupport() {

    @Autowired
    private lateinit var predefinedValidationStampService: PredefinedValidationStampService

    @Autowired
    private lateinit var fractionValidationDataType: FractionValidationDataType

    @Test
    fun `Validation run with data and predefined validation stamp without data type`() {
        // Creates a predefined validation stamp
        val psName = TestUtils.uid("VS")
        asAdmin().execute {
            predefinedValidationStampService.newPredefinedValidationStamp(
                    PredefinedValidationStamp.of(
                            NameDescription.nd(psName, "")
                    )
            )
        }

        // Project...
        project {
            setProperty(
                    this,
                    AutoValidationStampPropertyType::class.java,
                    AutoValidationStampProperty(true, false)
            )
            branch {
                // Creates a build
                build("1.0.0") {
                    // Validates with data
                    val run = validateWithData(
                            validationStampName = psName,
                            // Status is required since no validation can be performed
                            validationRunStatusID = ValidationRunStatusID.STATUS_PASSED,
                            validationDataTypeId = fractionValidationDataType.descriptor.id,
                            validationRunData = FractionValidationData(80, 100)
                    )
                    // Checks the run status
                    assertEquals(
                            ValidationRunStatusID.STATUS_PASSED.id,
                            run.lastStatus.statusID.id
                    )
                    // Checks its data
                    val runData: ValidationRunData<*>? = run.data
                    assertNotNull(runData) {
                        assertIs<FractionValidationData>(it.data) {
                            assertEquals(80, it.numerator)
                            assertEquals(100, it.denominator)
                        }
                    }
                }
            }
        }
    }

    @Test
    fun `Validation run with data and predefined validation stamp with data type`() {
        // Creates a predefined validation stamp with data
        val psName = TestUtils.uid("VS")
        asAdmin().execute {
            predefinedValidationStampService.newPredefinedValidationStamp(
                    PredefinedValidationStamp.of(
                            NameDescription.nd(psName, "")
                    ).withDataType(
                            fractionValidationDataType.config(
                                    ThresholdConfig(100, 90)
                            )
                    )
            )
        }

        project {
            // Enables auto creation of validation stamps
            asAdmin().execute {
                setProperty(
                        this,
                        AutoValidationStampPropertyType::class.java,
                        AutoValidationStampProperty(true, false)
                )
            }
            // Creates a build
            branch {
                build("1.0.0") {
                    // Validation with data
                    val run = validateWithData(
                            validationStampName = psName,
                            validationDataTypeId = fractionValidationDataType.descriptor.id,
                            validationRunData = FractionValidationData(80, 100)
                    )
                    // Checks the status (computed)
                    assertEquals(
                            ValidationRunStatusID.STATUS_FAILED.id,
                            run.lastStatus.statusID.id
                    )
                    // Checks the data
                    assertNotNull(run.data) {
                        assertIs<FractionValidationData>(it.data) {
                            assertEquals(80, it.numerator)
                            assertEquals(100, it.denominator)
                        }
                    }
                }
            }
        }

    }

}