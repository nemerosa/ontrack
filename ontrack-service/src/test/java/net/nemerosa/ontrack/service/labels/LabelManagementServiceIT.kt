package net.nemerosa.ontrack.service.labels

import net.nemerosa.ontrack.it.AbstractDSLTestSupport
import net.nemerosa.ontrack.model.labels.LabelForm
import net.nemerosa.ontrack.model.labels.LabelManagement
import net.nemerosa.ontrack.test.TestUtils.uid
import org.junit.Test
import org.springframework.security.access.AccessDeniedException
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith
import kotlin.test.assertNull
import kotlin.test.assertTrue

class LabelManagementServiceIT : AbstractDSLTestSupport() {

    @Test
    fun `Creating, updating and deleting a label`() {
        val category = uid("C")
        val name = uid("N")
        asUser().with(LabelManagement::class.java).execute {
            val label = labelManagementService.newLabel(
                    LabelForm(
                            category = category,
                            name = name,
                            description = null,
                            color = "#FFFFFF"
                    )
            )
            assertTrue(label.id > 0)
            assertEquals(category, label.category)
            assertEquals(name, label.name)
            assertNull(label.description)
            assertEquals("#FFFFFF", label.color)
            assertNull(label.computedBy)
            // Updating the description
            val updatedLabel = labelManagementService.updateLabel(
                    label.id,
                    LabelForm(
                            category = category,
                            name = name,
                            description = "New description",
                            color = "#FFFFFF"
                    )
            )
            assertEquals(label.id, updatedLabel.id)
            assertEquals(category, updatedLabel.category)
            assertEquals(name, updatedLabel.name)
            assertEquals("New description", updatedLabel.description)
            assertEquals("#FFFFFF", updatedLabel.color)
            assertNull(updatedLabel.computedBy)
            // Deleting the label
            labelManagementService.deleteLabel(label.id)
            val deletedLabel = labelManagementService.labels.find { it.id == label.id }
            assertNull(deletedLabel)
        }
    }

    @Test
    fun `Null category is allowed`() {
        val label = label(category = null)
        assertNull(label.category)
    }

    @Test
    fun `Cannot create a label if not authorized`() {
        val category = uid("C")
        val name = uid("N")
        assertFailsWith(AccessDeniedException::class) {
            labelManagementService.newLabel(
                    LabelForm(
                            category = category,
                            name = name,
                            description = null,
                            color = "#FFFFFF"
                    )
            )
        }
    }

    @Test
    fun `Cannot update a label if not authorized`() {
        val label = label()
        assertFailsWith(AccessDeniedException::class) {
            labelManagementService.updateLabel(
                    label.id,
                    LabelForm(
                            category = label.category,
                            name = label.name,
                            description = "New description",
                            color = label.color
                    )
            )
        }
    }

    @Test
    fun `Cannot delete a label if not authorized`() {
        val label = label()
        assertFailsWith(AccessDeniedException::class) {
            labelManagementService.deleteLabel(label.id)
        }
    }

}