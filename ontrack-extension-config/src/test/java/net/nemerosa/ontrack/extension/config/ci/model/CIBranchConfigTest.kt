package net.nemerosa.ontrack.extension.config.ci.model

import net.nemerosa.ontrack.json.asJson
import net.nemerosa.ontrack.json.parse
import org.junit.jupiter.api.Test
import kotlin.test.assertEquals

class CIBranchConfigTest {

    @Test
    fun `Raw parsing of extensions`() {
        val json = mapOf(
            "promotions" to mapOf(
                "BRONZE" to emptyMap<String, Any>(),
                "RELEASE" to emptyMap<String, Any>(),
            ),
            "notificationsConfig" to mapOf(
                "notifications" to listOf(
                    mapOf(
                        "name" to "On BRONZE"
                    )
                ),
            )
        ).asJson()
        val config = json.parse<CIBranchConfig>()
        assertEquals(
            CIBranchConfig(
                promotions = mapOf(
                    "BRONZE" to CIPromotionConfig(),
                    "RELEASE" to CIPromotionConfig(),
                ),
                extensions = mapOf(
                    "notificationsConfig" to mapOf(
                        "notifications" to listOf(
                            mapOf(
                                "name" to "On BRONZE"
                            )
                        )
                    ).asJson()
                )
            ),
            config
        )
    }

}