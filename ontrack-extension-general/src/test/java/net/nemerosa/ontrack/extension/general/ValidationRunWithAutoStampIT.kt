package net.nemerosa.ontrack.extension.general

import net.nemerosa.ontrack.extension.general.validation.FractionValidationData
import net.nemerosa.ontrack.extension.general.validation.FractionValidationDataType
import net.nemerosa.ontrack.extension.general.validation.ThresholdConfig
import net.nemerosa.ontrack.it.AbstractServiceTestSupport
import net.nemerosa.ontrack.model.exceptions.ValidationRunDataUnexpectedException
import net.nemerosa.ontrack.model.security.ProjectEdit
import net.nemerosa.ontrack.model.settings.PredefinedValidationStampService
import net.nemerosa.ontrack.model.structure.*
import net.nemerosa.ontrack.test.TestUtils
import net.nemerosa.ontrack.test.assertIs
import org.junit.Ignore
import org.junit.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Component
import kotlin.test.assertEquals
import kotlin.test.assertNotNull

@Component
class ValidationRunWithAutoStampIT : AbstractServiceTestSupport() {

    @Autowired
    private lateinit var predefinedValidationStampService: PredefinedValidationStampService

    @Autowired
    private lateinit var fractionValidationDataType: FractionValidationDataType

    @Test(expected = ValidationRunDataUnexpectedException::class)
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
        // Creates a build
        val build = doCreateBuild()
        // Enables auto creation of validation stamps
        asAdmin().execute {
            setProperty(
                    build.project,
                    AutoValidationStampPropertyType::class.java,
                    AutoValidationStampProperty(true, false)
            )
        }
        // Auto creation of the validation stamp
        val vs = asUser().with(build.branch, ProjectEdit::class.java).call {
            structureService.getOrCreateValidationStamp(build.branch, null, psName)
        }
        // Validation with data
        doValidateBuild(
                build,
                vs,
                null,
                fractionValidationDataType.data(
                        FractionValidationData(80, 100)
                )
        )
    }

    @Test
    @Ignore
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
        // Creates a build
        val build = doCreateBuild()
        // Enables auto creation of validation stamps
        asAdmin().execute {
            setProperty(
                    build.project,
                    AutoValidationStampPropertyType::class.java,
                    AutoValidationStampProperty(true, false)
            )
        }
        // Auto creation of the validation stamp
        val vs = asUser().with(build.branch, ProjectEdit::class.java).call {
            structureService.getOrCreateValidationStamp(build.branch, null, psName)
        }
        // Validation with data
        val run = doValidateBuild(
                build,
                vs,
                null,
                fractionValidationDataType.data(
                        FractionValidationData(80, 100)
                )
        )
        assertEquals(
                ValidationRunStatusID.STATUS_FAILED.id,
                run.lastStatus.statusID.id
        )
        assertNotNull(run.data) {
            assertIs<FractionValidationData>(it.data) {
                assertEquals(80, it.numerator)
                assertEquals(100, it.denominator)
            }
        }
    }

}