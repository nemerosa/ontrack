package net.nemerosa.ontrack.extension.indicators.values

import net.nemerosa.ontrack.extension.indicators.IndicatorsExtensionFeature
import net.nemerosa.ontrack.json.asJson
import org.junit.Test
import kotlin.test.assertEquals

class PercentageIndicatorValueTypeTest {

    private val type = PercentageIndicatorValueType(IndicatorsExtensionFeature())

    @Test
    fun `Parsing of client config form`() {
        val config = type.fromConfigForm(
                mapOf(
                        "threshold" to 80,
                        "higherIsBetter" to true
                ).asJson()
        )
        assertEquals(80, config.threshold.value)
        assertEquals(true, config.higherIsBetter)
    }

}