package net.nemerosa.ontrack.extension.general.validation

import net.nemerosa.ontrack.extension.general.GeneralExtensionFeature
import net.nemerosa.ontrack.json.toJson
import net.nemerosa.ontrack.model.structure.ValidationRunStatusID
import org.junit.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotNull

class CHMLValidationDataTypeTest {

    private val dataType = CHMLValidationDataType(GeneralExtensionFeature())

    private val config = CHMLValidationDataTypeConfig(
            CHMLLevel(CHML.HIGH, 1),
            CHMLLevel(CHML.CRITICAL, 1)
    )

    @Test
    fun `Status passed`() {
        assertEquals(
                ValidationRunStatusID.PASSED,
                dataType.computeStatus(
                        config,
                        CHMLValidationDataTypeData(
                                mapOf(
                                        CHML.MEDIUM to 1
                                )
                        )
                )?.id
        )
    }

    @Test
    fun `Status warning`() {
        assertEquals(
                ValidationRunStatusID.WARNING,
                dataType.computeStatus(
                        config,
                        CHMLValidationDataTypeData(
                                mapOf(
                                        CHML.HIGH to 1,
                                        CHML.MEDIUM to 1
                                )
                        )
                )?.id
        )
    }

    @Test
    fun `Status failed`() {
        assertEquals(
                ValidationRunStatusID.FAILED,
                dataType.computeStatus(
                        config,
                        CHMLValidationDataTypeData(
                                mapOf(
                                        CHML.CRITICAL to 1,
                                        CHML.HIGH to 1,
                                        CHML.MEDIUM to 1
                                )
                        )
                )?.id
        )
    }

    @Test
    fun `Parsing data from client`() {
        val data = dataType.fromForm(mapOf(
                "CRITICAL" to 1,
                "HIGH" to 2,
                "MEDIUM" to 4,
                "LOW" to 8
        ).toJson())
        assertNotNull(data) {
            assertEquals(1, it.levels[CHML.CRITICAL])
            assertEquals(2, it.levels[CHML.HIGH])
            assertEquals(4, it.levels[CHML.MEDIUM])
            assertEquals(8, it.levels[CHML.LOW])
        }
    }

}