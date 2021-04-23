package net.nemerosa.ontrack.common

import org.junit.Test
import kotlin.test.assertEquals

class SyncForwardTest {

    @Test
    fun synchronization() {
        val inputs = listOf(
            Input("affligem", "Affligem"),
            Input("guinness", "Guinness"),
            Input("maredsous", "Maredsous")
        )
        var id = 1
        val outputs = mutableListOf(
            Output(id++, "affligem", "Affligem"),
            Output(id++, "heineken", "Heineken"),
            Output(id++, "maredsous", "Maredsous")
        )

        syncForward(from = inputs, to = outputs) {
            equality { a, b -> a.key == b.key }
            onCreation { a -> outputs += Output(id++, a.key, a.name) }
            onModification { a, existing ->
                outputs.remove(existing)
                outputs += Output(existing.id, a.key, a.name)
            }
            onDeletion { b ->
                outputs.remove(b)
            }
        }

        assertEquals(
            mutableSetOf(
                Output(1, "affligem", "Affligem"),
                Output(3, "maredsous", "Maredsous"),
                Output(4, "guinness", "Guinness")
            ),
            outputs.toSet()
        )
    }

    private data class Input(
        val key: String,
        val name: String,
    )

    private data class Output(
        val id: Int,
        val key: String,
        val name: String,
    )

}