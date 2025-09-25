package net.nemerosa.ontrack.extension.git.branching

import net.nemerosa.ontrack.extension.git.AbstractGitTestSupport
import net.nemerosa.ontrack.extension.scm.branching.BranchingModel
import net.nemerosa.ontrack.extension.scm.branching.BranchingModelProperty
import net.nemerosa.ontrack.extension.scm.branching.BranchingModelPropertyType
import net.nemerosa.ontrack.extension.scm.branching.BranchingModelService
import net.nemerosa.ontrack.model.support.NameValue
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import kotlin.test.assertEquals

class BranchingModelServiceIT : AbstractGitTestSupport() {

    @Autowired
    private lateinit var branchingModelService: BranchingModelService

    @Test
    fun `No property`() {
        project {
            val model = branchingModelService.getBranchingModel(project)
            assertEquals(
                    BranchingModel.DEFAULT.patterns,
                    model.patterns
            )
        }
    }

    @Test
    fun `Specific property`() {
        project {
            setProperty(this, BranchingModelPropertyType::class.java,
                BranchingModelProperty(
                    listOf(
                        NameValue("Development", "main|gatekeeper"),
                        NameValue("Maintenance", "maintenance/.*"),
                        NameValue("Release", "release/.*")
                    )
                )
            )
            val model = branchingModelService.getBranchingModel(project)
            assertEquals(
                    mapOf(
                            "Development" to "main|gatekeeper",
                            "Maintenance" to "maintenance/.*",
                            "Release" to "release/.*"
                    ),
                    model.patterns
            )
        }
    }

}