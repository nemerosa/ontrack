package net.nemerosa.ontrack.model.deliverymap

import net.nemerosa.ontrack.json.parse
import org.junit.jupiter.api.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotEquals
import kotlin.test.assertNull

class DeliveryMapCheckpointTest {

    @Test
    fun `An unresolved checkpoint carries the name the configuration asked for`() {
        val checkpoint = DeliveryMapCheckpoint.unresolved(DeliveryMapCheckpointTypes.PROMOTION_LEVEL, "GOLD")
        assertEquals("GOLD", checkpoint.name)
        assertEquals(DeliveryMapCheckpointTypes.UNRESOLVED, checkpoint.type)
        assertEquals(
            DeliveryMapCheckpointTypes.PROMOTION_LEVEL,
            checkpoint.data.parse<UnresolvedCheckpointData>().reference,
        )
    }

    @Test
    fun `Nothing ever arrives at an unresolved checkpoint`() {
        assertNull(DeliveryMapCheckpoint.unresolved(DeliveryMapCheckpointTypes.PROMOTION_LEVEL, "GOLD").arrival)
    }

    @Test
    fun `The id of an unresolved checkpoint is derived from the name the rule asked for`() {
        // Deterministic, so that #1707 can keep the node where the user dragged it across a refresh
        assertEquals(
            DeliveryMapCheckpoint.unresolved(DeliveryMapCheckpointTypes.PROMOTION_LEVEL, "GOLD").id,
            DeliveryMapCheckpoint.unresolved(DeliveryMapCheckpointTypes.PROMOTION_LEVEL, "GOLD").id,
        )
    }

    @Test
    fun `Two rules asking for the same missing thing land on one checkpoint`() {
        assertEquals(
            "unresolved:promotion-level:GOLD",
            DeliveryMapCheckpoint.unresolved(DeliveryMapCheckpointTypes.PROMOTION_LEVEL, "GOLD").id,
        )
    }

    @Test
    fun `A missing promotion level and a missing slot of one name are not the same checkpoint`() {
        assertNotEquals(
            DeliveryMapCheckpoint.unresolved(DeliveryMapCheckpointTypes.PROMOTION_LEVEL, "staging").id,
            DeliveryMapCheckpoint.unresolved("slot", "staging").id,
        )
    }

}
