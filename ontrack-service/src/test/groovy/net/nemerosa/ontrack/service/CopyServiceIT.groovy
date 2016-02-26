package net.nemerosa.ontrack.service

import net.nemerosa.ontrack.it.AbstractServiceTestSupport
import net.nemerosa.ontrack.model.Ack
import net.nemerosa.ontrack.model.security.ProjectEdit
import net.nemerosa.ontrack.model.structure.Branch
import net.nemerosa.ontrack.model.structure.BranchCloneRequest
import net.nemerosa.ontrack.model.structure.CopyService
import net.nemerosa.ontrack.model.structure.PropertyService
import net.nemerosa.ontrack.extension.api.support.TestProperty
import net.nemerosa.ontrack.extension.api.support.TestPropertyType
import net.nemerosa.ontrack.test.TestUtils
import org.junit.Test
import org.springframework.beans.factory.annotation.Autowired

class CopyServiceIT extends AbstractServiceTestSupport {

    @Autowired
    private PropertyService propertyService

    @Autowired
    private CopyService copyService

    @Test
    void 'Branch cloning: properties are also cloned'() {
        // Creates a branch
        Branch branch = doCreateBranch()

        // Sets a property on this branch
        Ack ack = asUser().with(branch, ProjectEdit).call {
            propertyService.editProperty(
                    branch,
                    TestPropertyType,
                    TestProperty.of("Test")
            )
        }
        assert ack.success

        // Clones the branch
        String clonedBranchName = TestUtils.uid("B")
        Branch clonedBranch = asUser().with(branch, ProjectEdit).call {
            copyService.cloneBranch(
                    branch,
                    new BranchCloneRequest(
                            clonedBranchName,
                            []
                    )
            )
        }
        assert clonedBranch.name == clonedBranchName

        // Gets the property for the cloned branch
        def property = propertyService.getProperty(clonedBranch, TestPropertyType)
        assert property != null
        assert !property.empty
        assert property.value != null
        assert property.value.value == 'Test'

    }

}
