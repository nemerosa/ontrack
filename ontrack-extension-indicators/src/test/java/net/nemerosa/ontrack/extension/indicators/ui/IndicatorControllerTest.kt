package net.nemerosa.ontrack.extension.indicators.ui

import com.nhaarman.mockitokotlin2.mock
import com.nhaarman.mockitokotlin2.verify
import com.nhaarman.mockitokotlin2.whenever
import net.nemerosa.ontrack.extension.indicators.model.IndicatorCategory
import net.nemerosa.ontrack.extension.indicators.model.IndicatorCompliance
import net.nemerosa.ontrack.json.asJson
import net.nemerosa.ontrack.model.form.Form
import net.nemerosa.ontrack.model.structure.ID
import net.nemerosa.ontrack.model.structure.NameDescription.nd
import net.nemerosa.ontrack.model.structure.Project
import net.nemerosa.ontrack.model.structure.Signature
import org.junit.Before
import org.junit.Test
import kotlin.test.assertSame

class IndicatorControllerTest {

    private lateinit var projectIndicatorService: ProjectIndicatorService

    private lateinit var controller: IndicatorController

    @Before
    fun before() {
        projectIndicatorService = mock()
        controller = IndicatorController(projectIndicatorService)
    }

    @Test
    fun getUpdateFormForIndicator() {
        val form = Form.create()
        whenever(projectIndicatorService.getUpdateFormForIndicator(ID.of(1), "type")).thenReturn(form)
        val returnedForm = controller.getUpdateFormForIndicator(ID.of(1), "type")
        assertSame(form, returnedForm)
    }

    @Test
    fun updateIndicator() {
        val value = ProjectIndicator(
                project = Project.of(nd("P", "")).withId(ID.of(1)),
                type = ProjectIndicatorType(
                        id = "type",
                        name = "Type",
                        link = null,
                        category = IndicatorCategory("category", "Category", null),
                        source = null,
                        computed = false,
                        deprecated = null
                ),
                value = mapOf("value" to "true").asJson(),
                compliance = IndicatorCompliance(100),
                comment = null,
                signature = Signature.anonymous()
        )
        val input = mapOf("value" to "true").asJson()
        whenever(projectIndicatorService.updateIndicator(ID.of(1), "type", input)).thenReturn(value)
        val returned = controller.updateIndicator(ID.of(1), "type", input)
        assertSame(value, returned)
    }

    @Test
    fun deleteIndicator() {
        controller.deleteIndicator(ID.of(1), "type")
        verify(projectIndicatorService).deleteIndicator(ID.of(1), "type")
    }

}