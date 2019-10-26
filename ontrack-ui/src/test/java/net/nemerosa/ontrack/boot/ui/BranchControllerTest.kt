package net.nemerosa.ontrack.boot.ui

import com.nhaarman.mockitokotlin2.mock
import com.nhaarman.mockitokotlin2.whenever
import net.nemerosa.ontrack.model.exceptions.BranchNotTemplateDefinitionException
import net.nemerosa.ontrack.model.structure.*
import org.junit.Before
import org.junit.Test
import java.util.*
import kotlin.test.assertEquals

class BranchControllerTest {

    private lateinit var controller: BranchController
    private lateinit var branchTemplateService: BranchTemplateService

    @Before
    fun before() {
        branchTemplateService = mock()
        controller = BranchController(
                mock(),
                branchTemplateService,
                mock(),
                mock(),
                mock(),
                mock(),
                mock(),
                mock()
        )
    }

    @Test(expected = BranchNotTemplateDefinitionException::class)
    fun `Single template instance form only when template definition`() {
        whenever(branchTemplateService.getTemplateDefinition(ID.of(1))).thenReturn(Optional.empty())
        controller.singleTemplateInstanceForm(ID.of(1))
    }

    @Test
    fun `Single template instance form no manual nor parameter field when no parameter`() {
        whenever(branchTemplateService.getTemplateDefinition(ID.of(1))).thenReturn(Optional.of(
                TemplateDefinition(
                        emptyList(),
                        null,
                        TemplateSynchronisationAbsencePolicy.DISABLE,
                        0
                )
        ))
        val form = controller.singleTemplateInstanceForm(ID.of(1))
        assertEquals(
                listOf("name"),
                form.fields.map { it.name }
        )
    }

    @Test
    fun `Single template instance form manual and parameter fields when parameters`() {
        whenever(branchTemplateService.getTemplateDefinition(ID.of(1))).thenReturn(Optional.of(
                TemplateDefinition(
                        listOf(
                                TemplateParameter(
                                        "ONE",
                                        "First parameter",
                                        "One"
                                ),
                                TemplateParameter(
                                        "TWO",
                                        "Second parameter",
                                        "Two"
                                )
                        ),
                        null,
                        TemplateSynchronisationAbsencePolicy.DISABLE,
                        0
                )
        ))
        val form = controller.singleTemplateInstanceForm(ID.of(1))
        assertEquals(
                listOf("name", "manual", "ONE", "TWO"),
                form.fields.map { it.name }
        )
    }


}
