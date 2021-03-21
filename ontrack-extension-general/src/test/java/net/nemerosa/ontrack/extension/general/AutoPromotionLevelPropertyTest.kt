package net.nemerosa.ontrack.extension.general

import net.nemerosa.ontrack.json.asJson
import org.junit.Test
import kotlin.test.assertEquals

class AutoPromotionLevelPropertyTest {

    @Test
    fun `JSON representation`() {
        assertEquals(
            mapOf("autoCreate" to true).asJson(),
            AutoPromotionLevelProperty(isAutoCreate = true).asJson()
        )
    }

}