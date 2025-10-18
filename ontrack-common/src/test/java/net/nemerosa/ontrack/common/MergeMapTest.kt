package net.nemerosa.ontrack.common

import org.junit.jupiter.api.Test
import kotlin.test.assertEquals

class MergeMapTest {

    @Test
    fun `Patching a map`() {
        val original = mapOf(
            "One" to listOf(1, 10),
            "Two" to listOf(2),
            "Three" to listOf(3),
        )
        val changes = mapOf(
            "Two" to listOf(20),
            "Four" to listOf(4),
        )

        val merged = mergeMap(
            target = original,
            changes = changes,
        ) { e, existing -> existing + e }

        assertEquals(
            mapOf(
                "One" to listOf(1, 10),
                "Two" to listOf(2, 20),
                "Three" to listOf(3),
                "Four" to listOf(4),
            ),
            merged,
        )
    }

}

