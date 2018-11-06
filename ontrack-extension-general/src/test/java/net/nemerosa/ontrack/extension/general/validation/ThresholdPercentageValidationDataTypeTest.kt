package net.nemerosa.ontrack.extension.general.validation

import net.nemerosa.ontrack.extension.general.GeneralExtensionFeature
import net.nemerosa.ontrack.model.structure.ValidationRunStatusID
import org.junit.Test
import kotlin.test.assertEquals

class ThresholdPercentageValidationDataTypeTest {

    private val dataType = ThresholdPercentageValidationDataType(GeneralExtensionFeature())

    @Test
    fun `Threshold of 0% with success being 0%`() {
        val config = ThresholdConfig(0, 50, false)
        assertEquals(ValidationRunStatusID.STATUS_PASSED.id, dataType.computeStatus(config, 0)?.id)
        assertEquals(ValidationRunStatusID.STATUS_WARNING.id, dataType.computeStatus(config, 1)?.id)
        assertEquals(ValidationRunStatusID.STATUS_WARNING.id, dataType.computeStatus(config, 49)?.id)
        assertEquals(ValidationRunStatusID.STATUS_WARNING.id, dataType.computeStatus(config, 50)?.id)
        assertEquals(ValidationRunStatusID.STATUS_FAILED.id, dataType.computeStatus(config, 51)?.id)
    }

}