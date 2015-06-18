package net.nemerosa.ontrack.boot.ui

import net.nemerosa.ontrack.boot.BranchSearchProvider
import net.nemerosa.ontrack.model.security.ProjectView
import net.nemerosa.ontrack.model.security.SecurityService
import net.nemerosa.ontrack.model.structure.Branch
import net.nemerosa.ontrack.model.structure.NameDescription
import org.junit.Test
import org.springframework.beans.factory.annotation.Autowired

class BranchSearchProviderIT extends AbstractWebTestSupport {

    @Autowired
    private BranchSearchProvider provider

    @Autowired
    private SecurityService securityService

    @Test
    void 'Branch search results'() {
        // Creates a project and branch
        Branch branch1 = doCreateBranch()
        String branchName = branch1.getName()
        // Creates a project...
        def project2 = doCreateProject()
        // ... and a branch with the same name
        Branch branch2 = doCreateBranch(project2, NameDescription.nd(branchName, ''))
        // Searches for the branch name
        def results = asUser().with(branch1, ProjectView).with(branch2, ProjectView).call { provider.search(branchName) }
        // Checks the results
        assert results.size() == 2
        assert results[0].title == branch1.entityDisplayName
        assert results[1].title == branch2.entityDisplayName
    }
}
