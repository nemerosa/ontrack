package net.nemerosa.ontrack.extension.notifications.subscriptions

import io.mockk.mockk
import net.nemerosa.ontrack.extension.casc.schema.CascArray
import net.nemerosa.ontrack.extension.casc.schema.CascObject
import net.nemerosa.ontrack.test.assertIs
import org.junit.jupiter.api.Test
import kotlin.test.assertNotNull
import kotlin.test.assertNull

class EntitySubscriptionsCascContextTest {

    @Test
    fun `Ignoring the storage key when generating the Casc model for EntitySubscriptionData`() {
        val context = EntitySubscriptionsCascContext(
            eventSubscriptionService = mockk(),
            storageService = mockk(),
            structureService = mockk(),
        )
        // Generating the Casc type
        val type = context.type
        assertIs<CascArray>(type) { root ->
            assertIs<CascObject>(root.type) { arrayType ->
                assertNotNull(arrayType.findFieldByName("entity")) { entityField ->
                    assertIs<CascObject>(entityField.type) { entityType ->
                        assertNotNull(entityType.findFieldByName("project"), "project field is present")
                        assertNull(entityType.findFieldByName("storageKey"), "No storageKey field")
                    }
                }
            }
        }
    }

}