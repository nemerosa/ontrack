package net.nemerosa.ontrack.common

import org.junit.jupiter.api.Test
import kotlin.test.assertEquals

class MergeListTest {

    @Test
    fun `Patching a list`() {
        val original = listOf(
            Item("One", 1),
            Item("Two", 2),
            Item("Three", 3),
        )
        val changes = listOf(
            Item("Two", 20),
            Item("Four", 4),
        )

        val merged = mergeList(
            target = original,
            changes = changes,
            idFn = Item::name,
        ) { e, _ -> e }

        assertEquals(
            listOf(
                Item("One", 1),
                Item("Two", 20),
                Item("Three", 3),
                Item("Four", 4),
            ),
            merged,
        )
    }

    data class Item(
        val name: String,
        val value: Int,
    )

}