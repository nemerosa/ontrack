package net.nemerosa.ontrack.extension.general

import net.nemerosa.ontrack.model.structure.ProjectEntityType
import org.junit.Test
import kotlin.test.assertEquals

class MetaInfoSearchItemTest {

    @Test
    fun `Map representation`() {
        val item = MetaInfoSearchItem(
                name = "name",
                value = "some-value",
                link = null,
                category = null,
                entityType = ProjectEntityType.BRANCH,
                entityId = 10
        )
        val actual = item.fields
        assertEquals(
                mapOf(
                        "name" to "name",
                        "value" to "some-value",
                        "link" to null,
                        "category" to null,
                        "entityType" to ProjectEntityType.BRANCH,
                        "entityId" to 10,
                        "key" to "name:some-value",
                        "id" to "BRANCH::10"
                ),
                actual
        )
    }

}