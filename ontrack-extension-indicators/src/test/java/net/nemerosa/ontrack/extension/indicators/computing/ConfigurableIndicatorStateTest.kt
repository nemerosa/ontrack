package net.nemerosa.ontrack.extension.indicators.computing

import net.nemerosa.ontrack.extension.indicators.IndicatorsTestFixtures
import net.nemerosa.ontrack.extension.indicators.values.BooleanIndicatorValueType
import net.nemerosa.ontrack.extension.indicators.values.BooleanIndicatorValueTypeConfig
import org.junit.jupiter.api.Test
import kotlin.test.assertEquals

class ConfigurableIndicatorStateTest {

    @Test
    fun `Required attribute`() {
        assertEquals(
            false,
            state(requiredValue = null).getRequiredAttribute()
        )
        assertEquals(
            false,
            state(requiredValue = "").getRequiredAttribute()
        )
        assertEquals(
            false,
            state(requiredValue = "null").getRequiredAttribute()
        )
        assertEquals(
            false,
            state(requiredValue = "false").getRequiredAttribute()
        )
        assertEquals(
            true,
            state(requiredValue = "true").getRequiredAttribute()
        )
    }

    private fun state(
        requiredValue: String?
    ) = ConfigurableIndicatorState(
        enabled = true,
        link = null,
        values = ConfigurableIndicatorState.toAttributeList(
            type = ConfigurableIndicatorType(
                category = IndicatorComputedCategory("test", "Testing"),
                id = "test",
                name = "This is a test",
                valueType = BooleanIndicatorValueType(IndicatorsTestFixtures.indicatorsExtensionFeature()),
                valueConfig = { _, state -> BooleanIndicatorValueTypeConfig(required = state.getRequiredAttribute()) },
                attributes = listOf(
                    ConfigurableIndicatorAttribute.requiredFlag
                ),
                computing = { _, _ -> true }
            ),
            values = mapOf("required" to requiredValue)
        )
    )

}