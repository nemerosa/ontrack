package net.nemerosa.ontrack.extension.general

import net.nemerosa.ontrack.json.asJson
import net.nemerosa.ontrack.json.parse
import org.junit.jupiter.api.Test
import kotlin.test.assertEquals

class AutoValidationStampPropertyTest {

    @Test
    fun `Backward compatible JSON`() {
        assertEquals(
                AutoValidationStampProperty(
                        isAutoCreate = true,
                        isAutoCreateIfNotPredefined = false,
                ),
                mapOf(
                        "autoCreate" to true,
                ).asJson().parse()
        )
    }

    @Test
    fun `New JSON format`() {
        assertEquals(
                AutoValidationStampProperty(true, true),
                mapOf(
                        "autoCreate" to true,
                        "autoCreateIfNotPredefined" to true,
                ).asJson().parse()
        )
    }

}
