package net.nemerosa.ontrack.extension.config.ci.conditions

import io.mockk.mockk
import net.nemerosa.ontrack.extension.config.ci.engine.CIEngine
import net.nemerosa.ontrack.json.asJson
import org.junit.jupiter.api.Test
import kotlin.test.assertFalse
import kotlin.test.assertTrue

class EnvironmentRegexConditionTest {

    private val condition = EnvironmentRegexCondition()

    @Test
    fun `Missing environment variable does not match`() {
        val ciEngine = mockk<CIEngine>()
        val config = EnvironmentRegexConditionConfig(
            name = "VERSION",
            regex = "\\d+\\.\\d+\\.\\d+"
        )
        assertFalse(
            condition.matches(
                conditionRegistry = mockk(),
                ciEngine,
                config.asJson(),
                emptyMap()
            ),
            "Missing environment variable"
        )
    }

    @Test
    fun `Non matching environment variable`() {
        val ciEngine = mockk<CIEngine>()
        val config = EnvironmentRegexConditionConfig(
            name = "VERSION",
            regex = "\\d+\\.\\d+\\.\\d+"
        )
        assertFalse(
            condition.matches(
                conditionRegistry = mockk(),
                ciEngine,
                config.asJson(),
                mapOf("VERSION" to "5.0-beta.2")
            ),
            "Non matching environment variable"
        )
    }

    @Test
    fun `Matching environment variable`() {
        val ciEngine = mockk<CIEngine>()
        val config = EnvironmentRegexConditionConfig(
            name = "VERSION",
            regex = "\\d+\\.\\d+\\.\\d+"
        )
        assertTrue(
            condition.matches(
                conditionRegistry = mockk(),
                ciEngine,
                config.asJson(),
                mapOf("VERSION" to "5.0.2")
            ),
            "Matching environment variable"
        )
    }

}