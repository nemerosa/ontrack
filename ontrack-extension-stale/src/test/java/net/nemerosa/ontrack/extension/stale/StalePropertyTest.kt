package net.nemerosa.ontrack.extension.stale

import net.nemerosa.ontrack.json.asJson
import net.nemerosa.ontrack.json.parse
import org.junit.Test
import kotlin.test.assertEquals

class StalePropertyTest {
    @Test
    fun backward_compatibility_of_json() {
        val json = mapOf(
            "disablingDuration" to 30,
            "deletingDuration" to 0,
        ).asJson()
        assertEquals(
            StaleProperty(
                disablingDuration = 30,
                deletingDuration = 0,
                promotionsToKeep = null,
                includes = null,
                excludes = null,
            ),
            json.parse()
        )
    }

    @Test
    fun `JSON backward compatibility after adding the includes and excludes properties`() {
        val json = mapOf(
            "disablingDuration" to 30,
            "deletingDuration" to 90,
            "promotionsToKeep" to listOf("PLATINUM"),
        ).asJson()
        assertEquals(
            StaleProperty(
                disablingDuration = 30,
                deletingDuration = 90,
                promotionsToKeep = listOf("PLATINUM"),
                includes = null,
                excludes = null,
            ),
            json.parse()
        )
    }
}