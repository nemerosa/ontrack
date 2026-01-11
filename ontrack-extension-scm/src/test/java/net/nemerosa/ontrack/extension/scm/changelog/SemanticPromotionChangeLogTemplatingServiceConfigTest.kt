package net.nemerosa.ontrack.extension.scm.changelog

import net.nemerosa.ontrack.json.asJson
import net.nemerosa.ontrack.json.parse
import net.nemerosa.ontrack.model.structure.NameDescription
import org.junit.jupiter.api.Test
import kotlin.test.assertEquals

class SemanticPromotionChangeLogTemplatingServiceConfigTest {

    @Test
    fun `No options`() {
        val config = emptyMap<String, String>().asJson().parse<SemanticPromotionChangeLogTemplatingServiceConfig>()
        config.apply {
            assertEquals(emptyList(), dependencies)
            assertEquals(false, issues)
            assertEquals(emptyList(), sections)
            assertEquals(emptyList(), exclude)
            assertEquals(false, allQualifiers)
            assertEquals(false, defaultQualifierFallback)
            assertEquals(true, acrossBranches)
        }
    }

    @Test
    fun `With issues`() {
        val config = mapOf(
            "issues" to "true"
        ).asJson().parse<SemanticPromotionChangeLogTemplatingServiceConfig>()
        config.apply {
            assertEquals(emptyList(), dependencies)
            assertEquals(true, issues)
            assertEquals(emptyList(), sections)
            assertEquals(emptyList(), exclude)
            assertEquals(false, allQualifiers)
            assertEquals(false, defaultQualifierFallback)
            assertEquals(true, acrossBranches)
        }
    }

    @Test
    fun `With sections and issues`() {
        val configJson = mapOf(
            "issues" to "true",
            "sections" to "ci=Delivery",
            "sections" to "chore=Other",
        ).asJson()
        val config = configJson.parse<SemanticPromotionChangeLogTemplatingServiceConfig>()
        config.apply {
            assertEquals(emptyList(), dependencies)
            assertEquals(true, issues)
            assertEquals(
                listOf(
                    NameDescription("ci", "Delivery"),
                    NameDescription("chore", "Other"),
                ),
                sections
            )
            assertEquals(emptyList(), exclude)
            assertEquals(false, allQualifiers)
            assertEquals(false, defaultQualifierFallback)
            assertEquals(true, acrossBranches)
        }
    }

}