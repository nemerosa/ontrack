package net.nemerosa.ontrack.extension.scm.catalog.sync

import net.nemerosa.ontrack.common.getOrNull
import net.nemerosa.ontrack.it.AsAdminTest
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import kotlin.test.assertFalse
import kotlin.test.assertNotNull
import kotlin.test.assertTrue

@AsAdminTest
class SCMOrphanDisablingServiceIT : AbstractSCMCatalogSyncTestSupport() {

    @Autowired
    private lateinit var scmOrphanDisablingService: SCMOrphanDisablingService

    @Test
    fun `Disabling the orphan projects`() {
        doTest { entryLinked, entryLinkedOther, entryUnlinked, entryUnlinkedOther ->
            asAdmin {
                // Checks the orphan project is not disabled
                assertNotNull(structureService.findProjectByName(projectOrphan.name).getOrNull()) { orphan ->
                    assertFalse(orphan.isDisabled, "Orphan project is not disabled yet")
                }
                // Running the disabling
                scmOrphanDisablingService.disableOrphanProjects()
                // Checks the orphan project is now disabled
                assertNotNull(structureService.findProjectByName(projectOrphan.name).getOrNull()) { orphan ->
                    assertTrue(orphan.isDisabled, "Orphan project is now disabled")
                }
                // Checks that the linked entries are still OK
                assertNotNull(structureService.findProjectByName(projectLinked.name).getOrNull()) { project ->
                    assertFalse(project.isDisabled, "Linked project is not disabled")
                }
                assertNotNull(structureService.findProjectByName(projectLinkedOther.name).getOrNull()) { project ->
                    assertFalse(project.isDisabled, "Linked project is not disabled")
                }
            }
        }
    }

}