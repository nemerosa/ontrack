package net.nemerosa.ontrack.service

import net.nemerosa.ontrack.it.AbstractDSLTestSupport
import net.nemerosa.ontrack.it.AsAdminTest
import org.junit.jupiter.api.Test
import kotlin.test.assertEquals

@AsAdminTest
class BuildServiceIT : AbstractDSLTestSupport() {

    @Test
    fun `Auto re-enabling a branch when a build is created`() {
        project {
            branch {
                // Disabling the branch
                val branch = structureService.disableBranch(this)
                // Checking the branch is disabled
                assertEquals(true, structureService.getBranch(id).isDisabled, "Branch is disabled")
                // Creating a build
                branch.build()
                // Checking the branch is enabled
                assertEquals(false, structureService.getBranch(id).isDisabled, "Branch is enabled")
            }
        }
    }

}