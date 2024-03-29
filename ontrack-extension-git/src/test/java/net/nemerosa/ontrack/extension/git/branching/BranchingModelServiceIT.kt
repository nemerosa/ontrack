package net.nemerosa.ontrack.extension.git.branching

import net.nemerosa.ontrack.extension.git.AbstractGitTestJUnit4Support
import net.nemerosa.ontrack.model.support.NameValue
import org.junit.Test
import org.springframework.beans.factory.annotation.Autowired
import kotlin.test.assertEquals

class BranchingModelServiceIT : AbstractGitTestJUnit4Support() {

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
                    BranchingModelProperty(listOf(
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