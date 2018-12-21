package net.nemerosa.ontrack.extension.git.branching

import net.nemerosa.ontrack.extension.git.AbstractGitTestSupport
import org.junit.Test
import org.springframework.beans.factory.annotation.Autowired

class BranchingModelServiceIT: AbstractGitTestSupport() {

    @Autowired
    private lateinit var branchingModelService: BranchingModelService

    @Test
    fun `No property`() {
        project {
            branchingModelService.getBranchingModel(project)
        }
    }

}