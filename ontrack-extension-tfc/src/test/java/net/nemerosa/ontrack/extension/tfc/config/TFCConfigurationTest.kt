package net.nemerosa.ontrack.extension.tfc.config

import net.nemerosa.ontrack.json.asJson
import net.nemerosa.ontrack.json.parse
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertNull

class TFCConfigurationTest {

    @Test
    fun `Parsing when no token`() {
        val config = mapOf(
            "name" to "My configuration",
            "url" to "https://app.terraform.io",
        ).asJson().parse<TFCConfiguration>()
        assertNull(config.token, "Token is null")
    }

}