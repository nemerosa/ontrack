package net.nemerosa.ontrack.service

import net.nemerosa.ontrack.it.AbstractServiceTestSupport
import net.nemerosa.ontrack.json.JsonUtils
import net.nemerosa.ontrack.model.security.BranchTemplateMgt
import net.nemerosa.ontrack.model.structure.*
import org.junit.Test
import org.springframework.beans.factory.annotation.Autowired

class BranchTemplateServiceIT extends AbstractServiceTestSupport {

    @Autowired
    private BranchTemplateService templateService

    @Test
    void 'Making a branch a template'() {
        // Creates a branch
        Branch branch = doCreateBranch()
        // Normal branch
        assert branch.type == BranchType.CLASSIC

        // Template definition
        TemplateDefinition templateDefinition = new TemplateDefinition(
                [],
                'test',
                JsonUtils.object().end(),
                TemplateSynchronisationAbsencePolicy.DELETE,
                10
        )
        // Saves the template
        Branch savedBranch = asUser().with(branch, BranchTemplateMgt).call({
            templateService.setTemplateDefinition(branch.id, templateDefinition)
        })
        // Checks
        assert savedBranch.id == branch.id
        assert savedBranch.type == BranchType.TEMPLATE_DEFINITION
    }

}
