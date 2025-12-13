package net.nemerosa.ontrack.extension.indicators.portfolio

import net.nemerosa.ontrack.extension.indicators.AbstractIndicatorsTestSupport
import net.nemerosa.ontrack.extension.indicators.acl.IndicatorViewManagement
import net.nemerosa.ontrack.test.TestUtils.uid
import org.junit.jupiter.api.Test
import org.springframework.security.access.AccessDeniedException
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith
import kotlin.test.assertNotNull
import kotlin.test.assertNull

class IndicatorViewServiceIT : AbstractIndicatorsTestSupport() {

    @Test
    fun `Creating an indicator view`() {
        val categories = (1..3).map { category() }
        val name = uid("V")
        asAdmin {
            indicatorViewService.saveIndicatorView(IndicatorView("", name, categories.map { it.id }))
            assertNotNull(
                indicatorViewService.getIndicatorViews().find { it.name == name },
                "View has been created"
            ) { view ->
                assertEquals(name, view.name)
                assertEquals(categories.map { it.id }, view.categories)
            }
            assertNotNull(indicatorViewService.findIndicatorViewByName(name), "View has been created") { view ->
                assertEquals(name, view.name)
                assertEquals(categories.map { it.id }, view.categories)
            }
        }
    }

    @Test
    fun `Updating an indicator view`() {
        val categories = (1..3).map { category() }
        val subset = categories.take(2)
        val name = uid("V")
        asAdmin {
            val id = indicatorViewService.saveIndicatorView(IndicatorView("", name, categories.map { it.id })).id
            indicatorViewService.saveIndicatorView(IndicatorView(id, name, subset.map { it.id }))
            assertNotNull(indicatorViewService.findIndicatorViewByName(name), "View has been updated") { view ->
                assertEquals(name, view.name)
                assertEquals(subset.map { it.id }, view.categories)
            }
        }
    }

    @Test
    fun `Deleting an indicator view`() {
        val categories = (1..3).map { category() }
        val name = uid("V")
        asAdmin {
            val id = indicatorViewService.saveIndicatorView(IndicatorView("", name, categories.map { it.id })).id
            assertNotNull(indicatorViewService.findIndicatorViewByName(name), "View has been created")
            indicatorViewService.deleteIndicatorView(id)
            assertNull(indicatorViewService.findIndicatorViewByName(name), "View has been deleted")
        }
    }

    @Test
    fun `List of views is available to everybody`() {
        val categories = (1..3).map { category() }
        val name = uid("V")
        asAdmin {
            indicatorViewService.saveIndicatorView(IndicatorView("", name, categories.map { it.id }))
        }
        asUser().call {
            assertNotNull(indicatorViewService.getIndicatorViews().find { it.name == name }, "View has been created")
            assertNotNull(indicatorViewService.findIndicatorViewByName(name), "View has been created")
        }
    }

    @Test
    fun `Saving a view is restricted`() {
        val categories = (1..3).map { category() }
        val name = uid("V")
        asUserWith<IndicatorViewManagement> {
            indicatorViewService.saveIndicatorView(IndicatorView("", name, categories.map { it.id }))
        }
        asUser().call {
            assertFailsWith<AccessDeniedException> {
                indicatorViewService.saveIndicatorView(IndicatorView("", name, categories.map { it.id }))
            }
        }
    }

    @Test
    fun `Existing view on creation`() {
        val categories = (1..3).map { category() }
        val name = uid("V")
        asAdmin {
            indicatorViewService.saveIndicatorView(IndicatorView("", name, categories.map { it.id }))
            assertFailsWith<IndicatorViewNameAlreadyExistsException> {
                indicatorViewService.saveIndicatorView(IndicatorView("", name, categories.map { it.id }))
            }
        }
    }

    @Test
    fun `Updating a view`() {
        val categories = (1..3).map { category() }
        val name = uid("V")
        asAdmin {
            val id =
                indicatorViewService.saveIndicatorView(IndicatorView("", name, categories.take(1).map { it.id })).id
            indicatorViewService.saveIndicatorView(IndicatorView(id, name, categories.map { it.id }))
        }
    }

    @Test
    fun `Updating a view with a different name`() {
        val categories = (1..3).map { category() }
        val name = uid("V")
        asAdmin {
            val id =
                indicatorViewService.saveIndicatorView(IndicatorView("", name, categories.take(1).map { it.id })).id
            val newName = uid("V")
            val newId = indicatorViewService.saveIndicatorView(IndicatorView(id, newName, categories.map { it.id })).id
            assertEquals(id, newId, "Update")
            assertNotNull(indicatorViewService.findIndicatorViewById(id)) {
                assertEquals(newName, it.name)
            }
        }
    }

    @Test
    fun `Updating a view with a different existing name`() {
        val categories = (1..3).map { category() }
        val name = uid("V")
        asAdmin {
            val newName = uid("V")
            val id =
                indicatorViewService.saveIndicatorView(IndicatorView("", name, categories.take(1).map { it.id })).id
            indicatorViewService.saveIndicatorView(IndicatorView("", newName, categories.map { it.id })).id
            assertFailsWith<IndicatorViewNameAlreadyExistsException> {
                indicatorViewService.saveIndicatorView(IndicatorView(id, newName, categories.map { it.id })).id
            }
        }
    }
}