package net.nemerosa.ontrack.extension.general.validation

import net.nemerosa.ontrack.model.structure.ValidationRunStatusID
import org.junit.Test
import kotlin.test.assertEquals

class ThresholdConfigTest {

    @Test
    fun computeStatus() {
        assertStatus(ValidationRunStatusID.STATUS_FAILED, true, 20, 10, 5)
        assertStatus(ValidationRunStatusID.STATUS_WARNING, true, 20, 10, 10)
        assertStatus(ValidationRunStatusID.STATUS_WARNING, true, 20, 10, 15)
        assertStatus(ValidationRunStatusID.STATUS_PASSED, true, 20, 10, 20)
        assertStatus(ValidationRunStatusID.STATUS_PASSED, true, 20, 10, 21)

        assertStatus(ValidationRunStatusID.STATUS_FAILED, true, null, 10, 9)
        assertStatus(ValidationRunStatusID.STATUS_PASSED, true, null, 10, 10)
        assertStatus(ValidationRunStatusID.STATUS_PASSED, true, null, 10, 11)

        assertStatus(ValidationRunStatusID.STATUS_WARNING, true, 10, null, 9)
        assertStatus(ValidationRunStatusID.STATUS_PASSED, true, 10, null, 10)
        assertStatus(ValidationRunStatusID.STATUS_PASSED, true, 10, null, 11)

        assertStatus(ValidationRunStatusID.STATUS_FAILED, false, 5, 15, 16)
        assertStatus(ValidationRunStatusID.STATUS_WARNING, false, 5, 15, 15)
        assertStatus(ValidationRunStatusID.STATUS_WARNING, false, 5, 15, 6)
        assertStatus(ValidationRunStatusID.STATUS_PASSED, false, 5, 15, 5)
        assertStatus(ValidationRunStatusID.STATUS_PASSED, false, 5, 15, 4)

        assertStatus(ValidationRunStatusID.STATUS_FAILED, false, null, 15, 16)
        assertStatus(ValidationRunStatusID.STATUS_PASSED, false, null, 15, 15)
        assertStatus(ValidationRunStatusID.STATUS_PASSED, false, null, 15, 14)

        assertStatus(ValidationRunStatusID.STATUS_WARNING, false, 15, null, 16)
        assertStatus(ValidationRunStatusID.STATUS_PASSED, false, 15, null, 15)
        assertStatus(ValidationRunStatusID.STATUS_PASSED, false, 15, null, 14)
    }

    private fun assertStatus(
            expected: ValidationRunStatusID,
            okIfGreater: Boolean,
            warningThreshold: Int?,
            failureThreshold: Int?,
            value: Int
    ) {
        val status = ThresholdConfig(warningThreshold, failureThreshold, okIfGreater).computeStatus(value)
        assertEquals(expected.id, status.id)
    }

}