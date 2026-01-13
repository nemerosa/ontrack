package net.nemerosa.ontrack.extension.scm.changelog

import net.nemerosa.ontrack.model.templating.TemplatingSourceConfig
import org.junit.jupiter.api.Test
import kotlin.test.assertEquals

class SemanticPromotionChangeLogTemplatingServiceConfigTest {

    @Test
    fun `No options`() {
        val config = TemplatingSourceConfig().parse<SemanticPromotionChangeLogTemplatingServiceConfig>()
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
        val config = TemplatingSourceConfig.fromMap(
            "issues" to "true"
        ).parse<SemanticPromotionChangeLogTemplatingServiceConfig>()
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
        val tsc = TemplatingSourceConfig(
            params = mapOf(
                "issues" to listOf("true"),
                "sections" to listOf("ci=Delivery", "chore=Other"),
            )
        )
        val config = tsc.parse<SemanticPromotionChangeLogTemplatingServiceConfig>()
        config.apply {
            assertEquals(emptyList(), dependencies)
            assertEquals(true, issues)
            assertEquals(
                listOf(
                    SemanticChangeLogSection("ci", "Delivery"),
                    SemanticChangeLogSection("chore", "Other"),
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