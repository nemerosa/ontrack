package net.nemerosa.ontrack.extension.general.validation

import net.nemerosa.ontrack.extension.general.GeneralExtensionFeature
import net.nemerosa.ontrack.json.toJson
import org.junit.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith
import kotlin.test.assertNotNull

class MetricsValidationDataTypeTest {

    private val dataType = MetricsValidationDataType(GeneralExtensionFeature())

    @Test
    fun `Parsing data from client`() {
        val data = dataType.fromForm(mapOf(
                "metrics" to listOf(
                        mapOf(
                                "name" to "js.bundle",
                                "value" to "1500.56"
                        ),
                        mapOf(
                                "name" to "js.error",
                                "value" to "150"
                        )
                )
        ).toJson())
        assertNotNull(data) {
            assertEquals(
                    mapOf(
                            "js.bundle" to 1500.56,
                            "js.error" to 150.0
                    ),
                    it.metrics
            )
        }
    }

    @Test
    fun `Parsing error from client`() {
        assertFailsWith<MetricsValidationDataNumberFormatException> {
            dataType.fromForm(mapOf(
                    "metrics" to listOf(
                            mapOf(
                                    "name" to "js.bundle",
                                    "value" to "1500. 56"
                            ),
                            mapOf(
                                    "name" to "js.error",
                                    "value" to "150"
                            )
                    )
            ).toJson())
        }
    }

}