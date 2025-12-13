package net.nemerosa.ontrack.service

import net.nemerosa.ontrack.it.AbstractDSLTestSupport
import net.nemerosa.ontrack.it.AsAdminTest
import net.nemerosa.ontrack.model.structure.BranchDisplayNameService
import net.nemerosa.ontrack.model.structure.BranchNamePolicy
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import kotlin.test.assertEquals

@AsAdminTest
class BranchDisplayNameServiceIT : AbstractDSLTestSupport() {

    @Autowired
    private lateinit var branchDisplayNameService: BranchDisplayNameService

    @Test
    fun `Branch name by default`() {
        project {
            branch("release-1.0") {
                assertEquals(
                    "release-1.0",
                    branchDisplayNameService.getBranchDisplayName(this, BranchNamePolicy.NAME_ONLY)
                )
            }
        }
    }

}