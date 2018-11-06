package net.nemerosa.ontrack.extension.general.validation

import net.nemerosa.ontrack.extension.general.GeneralExtensionFeature
import net.nemerosa.ontrack.json.jsonOf
import org.junit.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotNull
import kotlin.test.assertNull

class ThresholdNumberValidationDataTypeTest {

    private val dataType = ThresholdNumberValidationDataType(GeneralExtensionFeature())

    @Test
    fun `Null config from JSON`() {
        assertNull(dataType.configFromJson(null))
    }

    @Test
    fun `Config from JSON`() {
        assertNotNull(dataType.configFromJson(
                jsonOf(
                        "warningThreshold" to 20,
                        "failureThreshold" to 50,
                        "okIfGreater" to false
                )
        ))
    }

    @Test
    fun `Config to JSON`() {
        val json = dataType.configToJson(
                ThresholdConfig(
                        20,
                        50,
                        false
                )
        )
        assertEquals(20, json["warningThreshold"].asInt())
        assertEquals(50, json["failureThreshold"].asInt())
        assertEquals(false, json["okIfGreater"].asBoolean())
    }

}