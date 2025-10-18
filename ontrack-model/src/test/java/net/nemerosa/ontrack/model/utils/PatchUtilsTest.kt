package net.nemerosa.ontrack.model.utils

import net.nemerosa.ontrack.json.asJson
import org.junit.jupiter.api.Test
import kotlin.test.assertEquals

class PatchUtilsTest {

    @Test
    fun `Patching a list`() {
        val parent = Parent(
            items = listOf(
                Item("One", 1),
                Item("Two", 2),
                Item("Three", 3),
            )
        )
        val changes = mapOf(
            "items" to listOf(
                mapOf(
                    "name" to "Two",
                    "value" to 20,
                ),
                mapOf(
                    "name" to "Four",
                    "value" to 4,
                ),
            )
        ).asJson()
        val patches = patchList(changes, parent::items) { it.name }
        assertEquals(
            listOf(
                Item("One", 1),
                Item("Two", 20),
                Item("Three", 3),
                Item("Four", 4),
            ),
            patches,
        )
    }

}

data class Parent(
    val items: List<Item>,
)

data class Item(
    val name: String,
    val value: Int,
)
