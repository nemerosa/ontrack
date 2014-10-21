package net.nemerosa.ontrack.service.support.template

import net.nemerosa.ontrack.json.JsonUtils
import net.nemerosa.ontrack.model.exceptions.BranchTemplateHasBuildException
import net.nemerosa.ontrack.model.exceptions.BranchTemplateInstanceException
import net.nemerosa.ontrack.model.security.SecurityService
import net.nemerosa.ontrack.model.structure.*
import net.nemerosa.ontrack.repository.BranchTemplateRepository
import org.junit.Before
import org.junit.Test

import static org.mockito.Mockito.mock
import static org.mockito.Mockito.when

class BranchTemplateServiceImplTest {

    private BranchTemplateService service
    private StructureService structureService

    @Before
    void before() {
        structureService = mock(StructureService)
        SecurityService securityService = mock(SecurityService)
        BranchTemplateRepository branchTemplateRepository = mock(BranchTemplateRepository)
        ExpressionEngine expressionEngine = mock(ExpressionEngine)
        CopyService copyService = mock(CopyService)
        TemplateSynchronisationService templateSynchronisationService = mock(TemplateSynchronisationService)
        this.service = new BranchTemplateServiceImpl(
                structureService,
                securityService,
                branchTemplateRepository,
                expressionEngine,
                copyService, eventPostService, eventFactory,
                templateSynchronisationService
        )
    }

    @Test(expected = BranchTemplateInstanceException)
    void 'Cannot make a template instance into a definition'() {
        Branch branch = Branch.of(
                Project.of(NameDescription.nd('P', "Project")),
                NameDescription.nd('B', "Branch")
        ).withType(BranchType.TEMPLATE_INSTANCE)
        when(structureService.getBranch(ID.of(1))).thenReturn(branch)

        service.setTemplateDefinition(ID.of(1), new TemplateDefinition(
                [],
                new ServiceConfiguration(
                        '',
                        JsonUtils.object().end()
                ),
                TemplateSynchronisationAbsencePolicy.DISABLE,
                0
        ))
    }

    @Test(expected = BranchTemplateHasBuildException)
    void 'Cannot make a template definition when builds are present'() {
        Branch branch = Branch.of(
                Project.of(NameDescription.nd('P', "Project")),
                NameDescription.nd('B', "Branch")
        )
        when(structureService.getBranch(ID.of(1))).thenReturn(branch)
        when(structureService.getBuildCount(branch)).thenReturn(1)

        service.setTemplateDefinition(ID.of(1), new TemplateDefinition(
                [],
                new ServiceConfiguration(
                        '',
                        JsonUtils.object().end()
                ),
                TemplateSynchronisationAbsencePolicy.DISABLE,
                0
        ))
    }

}