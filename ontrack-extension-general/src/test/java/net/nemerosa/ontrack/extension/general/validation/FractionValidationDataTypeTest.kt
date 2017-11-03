package net.nemerosa.ontrack.extension.general.validation

import net.nemerosa.ontrack.extension.general.GeneralExtensionFeature
import net.nemerosa.ontrack.json.jsonOf
import net.nemerosa.ontrack.model.exceptions.ValidationRunDataInputException
import net.nemerosa.ontrack.model.structure.ValidationRunStatusID
import org.junit.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotNull
import kotlin.test.assertNull

class FractionValidationDataTypeTest {

    private val dataType = FractionValidationDataType(GeneralExtensionFeature())

    @Test
    fun toJson() {
        val json = dataType.toJson(
                FractionValidationData(12, 50)
        )
        assertEquals(12, json["numerator"].asInt())
        assertEquals(50, json["denominator"].asInt())
    }

    @Test
    fun fromJson() {
        val data = dataType.fromJson(
                jsonOf(
                        "numerator" to 12,
                        "denominator" to 50
                )
        )
        assertEquals(12, data?.numerator)
        assertEquals(50, data?.denominator)
    }

    @Test
    fun getForm() {
        val form = dataType.getForm(null)
        assertNotNull(form.getField("numerator"))
        assertNotNull(form.getField("denominator"))
    }

    @Test
    fun fromFormNull() {
        assertNull(dataType.fromForm(null))
    }

    @Test
    fun fromForm() {
        val data = dataType.fromForm(
                jsonOf(
                        "numerator" to 12,
                        "denominator" to 50
                )
        )
        assertEquals(12, data?.numerator)
        assertEquals(50, data?.denominator)
    }

    @Test(expected = ValidationRunDataInputException::class)
    fun validateDataNull() {
        dataType.validateData(ThresholdConfig(50, 75, false), null)
    }

    @Test(expected = ValidationRunDataInputException::class)
    fun validateDataNumeratorNegative() {
        dataType.validateData(ThresholdConfig(50, 75, false), FractionValidationData(-1, 24))
    }

    @Test(expected = ValidationRunDataInputException::class)
    fun validateDataDenominatorZero() {
        dataType.validateData(ThresholdConfig(50, 75, false), FractionValidationData(1, 0))
    }

    @Test(expected = ValidationRunDataInputException::class)
    fun validateDataDenominatorNegative() {
        dataType.validateData(ThresholdConfig(50, 75, false), FractionValidationData(1, -1))
    }

    @Test(expected = ValidationRunDataInputException::class)
    fun validateDataGreatherThan1() {
        dataType.validateData(ThresholdConfig(50, 75, false), FractionValidationData(13, 12))
    }

    @Test
    fun computeStatus() {
        val config = ThresholdConfig(50, 25, true)
        assertEquals(ValidationRunStatusID.STATUS_PASSED.id, dataType.computeStatus(config, FractionValidationData(24, 24))?.id)
        assertEquals(ValidationRunStatusID.STATUS_PASSED.id, dataType.computeStatus(config, FractionValidationData(13, 24))?.id)
        assertEquals(ValidationRunStatusID.STATUS_WARNING.id, dataType.computeStatus(config, FractionValidationData(12, 24))?.id)
        assertEquals(ValidationRunStatusID.STATUS_WARNING.id, dataType.computeStatus(config, FractionValidationData(11, 24))?.id)
        assertEquals(ValidationRunStatusID.STATUS_FAILED.id, dataType.computeStatus(config, FractionValidationData(6, 24))?.id)
        assertEquals(ValidationRunStatusID.STATUS_FAILED.id, dataType.computeStatus(config, FractionValidationData(5, 24))?.id)
        assertEquals(ValidationRunStatusID.STATUS_FAILED.id, dataType.computeStatus(config, FractionValidationData(0, 24))?.id)
    }

}