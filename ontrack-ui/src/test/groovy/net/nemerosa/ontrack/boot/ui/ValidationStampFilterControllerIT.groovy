package net.nemerosa.ontrack.boot.ui

import net.nemerosa.ontrack.model.security.ProjectConfig
import org.junit.Test
import org.springframework.beans.factory.annotation.Autowired

import static net.nemerosa.ontrack.model.structure.NameDescription.nd
import static net.nemerosa.ontrack.test.TestUtils.uid

class ValidationStampFilterControllerIT extends AbstractWebTestSupport {

    @Autowired
    private ValidationStampFilterController controller

    @Test
    void 'New filter contains all validation stamp filters'() {
        // Filter name
        def name = uid('F')
        // Creates a branch and some validation stamps
        def branch = doCreateBranch()
        (1..10).each {
            doCreateValidationStamp(branch, nd("VS${it}", ''))
        }
        // Creates a new validation stamp filter...
        def filter = asUser().with(branch, ProjectConfig).call {
            controller.createBranchValidationStampFilterForm(branch.id, nd(name, ''))
        }
        // Checks that all validation stamps names are included
        assert filter.vsNames == (1..10).collect { "VS${it}" as String }
    }
}