package net.nemerosa.ontrack.extension.scm.catalog.sync

import net.nemerosa.ontrack.test.assertNotPresent
import net.nemerosa.ontrack.test.assertPresent
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import kotlin.test.assertEquals
import kotlin.test.assertNotNull

class SCMCatalogImportServiceIT : AbstractSCMCatalogSyncTestSupport() {

    @Autowired
    private lateinit var scmCatalogImportService: SCMCatalogImportService

    @Test
    fun `No sync when disabled`() {
        doTest { _, _, entryUnlinked, entryUnlinkedOther ->

            // Disabling the sync
            asAdmin {
                settingsManagerService.saveSettings(
                    SCMCatalogSyncSettings(syncEnabled = false)
                )

                // Running the import
                scmCatalogImportService.importCatalog(::println)

                // Checks that no project has been created
                assertNotPresent(structureService.findProjectByName(entryUnlinked.repository))
                assertNotPresent(structureService.findProjectByName(entryUnlinkedOther.repository))
            }
        }
    }

    @Test
    fun `Synching on all projects`() {
        doTest { _, _, entryUnlinked, entryUnlinkedOther ->

            // Disabling the sync
            asAdmin {
                settingsManagerService.saveSettings(
                    SCMCatalogSyncSettings(syncEnabled = true)
                )
                // Running the import
                scmCatalogImportService.importCatalog(::println)

                // Checks that the unlinked projects have been created
                assertPresent(
                    structureService.findProjectByName(entryUnlinked.repository),
                    "${entryUnlinked.repository} project must be created"
                )
                assertPresent(
                    structureService.findProjectByName(entryUnlinkedOther.repository),
                    "${entryUnlinkedOther.repository} project must be created"
                )
            }
        }
    }

    @Test
    fun `Synching on a subset of projects`() {
        doTest { _, _, entryUnlinked, entryUnlinkedOther ->

            // Disabling the sync
            asAdmin {
                settingsManagerService.saveSettings(
                    SCMCatalogSyncSettings(syncEnabled = true, repository = ".*other.*")
                )

                // Running the import
                scmCatalogImportService.importCatalog(::println)

                // Checks that the unlinked projects have been created
                assertNotPresent(structureService.findProjectByName(entryUnlinked.repository))
                assertPresent(structureService.findProjectByName(entryUnlinkedOther.repository)) { p ->
                    // Checks that the SCM catalog entries are linked to the projects
                    assertNotNull(catalogLinkService.getSCMCatalogEntry(p)) { entry ->
                        assertEquals(entryUnlinkedOther.repository, entry.repository)
                    }
                }

                // Check that existing projects are not overridden
                structureService.getProject(projectLinked.id).apply {
                    assertEquals("Not to be overridden", description)
                }

            }
        }
    }

}