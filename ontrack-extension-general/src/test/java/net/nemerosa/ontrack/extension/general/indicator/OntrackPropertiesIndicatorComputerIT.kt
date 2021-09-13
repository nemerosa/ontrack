package net.nemerosa.ontrack.extension.general.indicator

import net.nemerosa.ontrack.extension.indicators.computing.ConfigurableIndicatorService
import net.nemerosa.ontrack.extension.indicators.computing.ConfigurableIndicatorState
import net.nemerosa.ontrack.it.AbstractDSLTestSupport
import org.junit.Test
import org.springframework.beans.factory.annotation.Autowired
import kotlin.test.assertEquals

class OntrackPropertiesIndicatorComputerIT: AbstractDSLTestSupport() {

    @Autowired
    private lateinit var ontrackPropertiesIndicatorComputer: OntrackPropertiesIndicatorComputer

    @Autowired
    private lateinit var configurableIndicatorService: ConfigurableIndicatorService

    @Test
    fun `Empty project`() {
        withIndicatorEnabled {
            project {
                val indicators = ontrackPropertiesIndicatorComputer.computeIndicators(this)
                assertEquals(1, indicators.size)
                val indicator = indicators.first()
                assertEquals(false, indicator.value)
            }
        }
    }

    @Test
    fun `Not empty project`() {
        withIndicatorEnabled {
            project {
                branch {
                    build()
                }
                val indicators = ontrackPropertiesIndicatorComputer.computeIndicators(this)
                assertEquals(1, indicators.size)
                val indicator = indicators.first()
                assertEquals(true, indicator.value)
            }
        }
    }

    private fun withIndicatorEnabled(code: () -> Unit) {
        val type = ontrackPropertiesIndicatorComputer.configurableIndicators.first()
        configurableIndicatorService.saveConfigurableIndicator(
            type = type,
            state = ConfigurableIndicatorState(
                enabled = true,
                link = null,
                values = ConfigurableIndicatorState.toAttributeList(
                    type,
                    mapOf(
                        "required" to "true"
                    )
                )
            )
        )
    }

}