package net.nemerosa.ontrack.extension.scm.changelog

import net.nemerosa.ontrack.json.asJson
import net.nemerosa.ontrack.json.jsonOf
import net.nemerosa.ontrack.json.parse
import org.junit.jupiter.api.Test
import kotlin.test.assertEquals

class SemanticChangeLogSectionTest {

    @Test
    fun `Parsing from JSON object`() {
        val json = jsonOf(
            "type" to "feat",
            "title" to "Features"
        )
        val section = json.parse<SemanticChangeLogSection>()
        assertEquals("feat", section.type)
        assertEquals("Features", section.title)
    }

    @Test
    fun `Parsing from string`() {
        val json = "feat=Features".asJson()
        val section = json.parse<SemanticChangeLogSection>()
        assertEquals("feat", section.type)
        assertEquals("Features", section.title)
    }

    @Test
    fun `Parsing from list of mixed formats`() {
        val json = jsonOf(
            "sections" to listOf(
                "feat=Features",
                jsonOf(
                    "type" to "fix",
                    "title" to "Bug fixes"
                )
            )
        )
        val config = json.parse<SemanticChangeLogTemplatingServiceConfig>()
        assertEquals(2, config.sections.size)
        
        assertEquals("feat", config.sections[0].type)
        assertEquals("Features", config.sections[0].title)
        
        assertEquals("fix", config.sections[1].type)
        assertEquals("Bug fixes", config.sections[1].title)
    }
}
