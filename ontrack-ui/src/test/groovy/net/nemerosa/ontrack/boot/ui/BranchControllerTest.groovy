package net.nemerosa.ontrack.boot.ui

import net.nemerosa.ontrack.extension.api.ExtensionManager
import net.nemerosa.ontrack.model.buildfilter.BuildFilterService
import net.nemerosa.ontrack.model.exceptions.BranchNotTemplateDefinitionException
import net.nemerosa.ontrack.model.security.SecurityService
import net.nemerosa.ontrack.model.structure.*
import org.junit.Before
import org.junit.Test

import static org.mockito.Mockito.mock
import static org.mockito.Mockito.when

class BranchControllerTest {

    BranchController controller
    BranchTemplateService branchTemplateService

    @Before
    void before() {
        branchTemplateService = mock(BranchTemplateService)
        controller = new BranchController(
                mock(StructureService),
                branchTemplateService,
                mock(TemplateSynchronisationService),
                mock(CopyService),
                mock(BuildFilterService),
                mock(ExtensionManager),
                mock(SecurityService),
                mock(BranchFavouriteService)
        )
    }

    @Test(expected = BranchNotTemplateDefinitionException)
    void 'Single template instance form: only when template definition'() {
        when(branchTemplateService.getTemplateDefinition(ID.of(1))).thenReturn(Optional.empty())
        controller.singleTemplateInstanceForm(ID.of(1))
    }

    @Test
    void 'Single template instance form: no manual nor parameter field when no parameter'() {
        when(branchTemplateService.getTemplateDefinition(ID.of(1))).thenReturn(Optional.of(
                new TemplateDefinition(
                        [],
                        null,
                        TemplateSynchronisationAbsencePolicy.DISABLE,
                        0
                )
        ))
        def form = controller.singleTemplateInstanceForm(ID.of(1))
        assert form.fields.collect { it.name } == ['name']
    }

    @Test
    void 'Single template instance form: manual and parameter fields when parameters'() {
        when(branchTemplateService.getTemplateDefinition(ID.of(1))).thenReturn(Optional.of(
                new TemplateDefinition(
                        [
                                new TemplateParameter(
                                        'ONE',
                                        'First parameter',
                                        'One'
                                ),
                                new TemplateParameter(
                                        'TWO',
                                        'Second parameter',
                                        'Two'
                                )
                        ],
                        null,
                        TemplateSynchronisationAbsencePolicy.DISABLE,
                        0
                )
        ))
        def form = controller.singleTemplateInstanceForm(ID.of(1))
        assert form.fields.collect { it.name } == ['name', 'manual', 'ONE', 'TWO']
    }


}
