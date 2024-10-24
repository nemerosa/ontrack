package net.nemerosa.ontrack.extension.environments.rules.core

import net.nemerosa.ontrack.json.asJson
import net.nemerosa.ontrack.json.parse
import org.junit.jupiter.api.Test
import kotlin.test.assertEquals

class EnvironmentSlotAdmissionRuleConfigTest {

    @Test
    fun `Parsing without a qualifier`() {
        val config = mapOf(
            "environmentName" to "staging",
        ).asJson().parse<EnvironmentSlotAdmissionRuleConfig>()
        assertEquals(
            EnvironmentSlotAdmissionRuleConfig(
                environmentName = "staging",
                qualifier = ""
            ),
            config
        )
    }

}