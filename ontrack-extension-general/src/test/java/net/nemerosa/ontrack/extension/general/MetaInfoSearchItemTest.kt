package net.nemerosa.ontrack.extension.general

import net.nemerosa.ontrack.model.structure.ProjectEntityType
import org.junit.Test
import kotlin.test.assertEquals

class MetaInfoSearchItemTest {

    @Test
    fun `Map representation`() {
        val item = MetaInfoSearchItem(
                items = mapOf("name" to "some-value"),
                entityType = ProjectEntityType.BRANCH,
                entityId = 10
        )
        assertEquals("BRANCH::10", item.id)
        val actual = item.fields
        assertEquals(
                mapOf(
                        "items" to mapOf(
                                "name" to "some-value"
                        ),
                        "keys" to listOf(
                                "name:some-value"
                        ),
                        "entityType" to ProjectEntityType.BRANCH,
                        "entityId" to 10
                ),
                actual
        )
    }

}