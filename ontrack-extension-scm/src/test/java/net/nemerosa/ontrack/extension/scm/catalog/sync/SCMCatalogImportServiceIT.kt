package net.nemerosa.ontrack.extension.scm.catalog.sync

import net.nemerosa.ontrack.extension.scm.catalog.CatalogFixtures
import net.nemerosa.ontrack.extension.scm.catalog.CatalogLinkService
import net.nemerosa.ontrack.extension.scm.catalog.SCMCatalog
import net.nemerosa.ontrack.extension.scm.catalog.SCMCatalogEntry
import net.nemerosa.ontrack.extension.scm.catalog.mock.MockSCMCatalogProvider
import net.nemerosa.ontrack.it.AbstractDSLTestJUnit4Support
import net.nemerosa.ontrack.model.structure.Project
import net.nemerosa.ontrack.test.TestUtils.uid
import net.nemerosa.ontrack.test.assertNotPresent
import net.nemerosa.ontrack.test.assertPresent
import org.junit.Before
import org.junit.Test
import org.springframework.beans.factory.annotation.Autowired
import kotlin.test.assertEquals
import kotlin.test.assertNotNull

class SCMCatalogImportServiceIT : AbstractDSLTestJUnit4Support() {

    @Autowired
    private lateinit var scmCatalogImportService: SCMCatalogImportService

    @Autowired
    private lateinit var scmCatalogProvider: MockSCMCatalogProvider

    @Autowired
    private lateinit var scmCatalog: SCMCatalog

    @Autowired
    private lateinit var catalogLinkService: CatalogLinkService

    private lateinit var projectLinked: Project
    private lateinit var projectLinkedOther: Project
    private lateinit var projectOrphan: Project

    @Before
    fun projects() {
        projectLinked = project {
            structureService.saveProject(
                withDescription("Not to be overridden")
            )
        }
        projectLinkedOther = project {}
        projectOrphan = project {}
    }

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


    private fun doTest(
        code: (
            entryLinked: SCMCatalogEntry,
            entryLinkedOther: SCMCatalogEntry,
            entryUnlinked: SCMCatalogEntry,
            entryUnlinkedOther: SCMCatalogEntry,
        ) -> Unit
    ) {
        withSettings<SCMCatalogSyncSettings> {

            // Creating some entries
            scmCatalogProvider.clear()
            val entryLinked = CatalogFixtures.entry(scm = "mocking", repository = uid("linked"), config = "config-1")
            val entryLinkedOther =
                CatalogFixtures.entry(scm = "mocking", repository = uid("other"), config = "config-2")
            val entryUnlinked =
                CatalogFixtures.entry(scm = "mocking", repository = uid("unlinked"), config = "config-3")
            val entryUnlinkedOther =
                CatalogFixtures.entry(scm = "mocking", repository = uid("unlinked-other"), config = "config-3")

            // Mock data
            scmCatalogProvider.storeEntry(entryLinked)
            scmCatalogProvider.storeEntry(entryLinkedOther)
            scmCatalogProvider.storeEntry(entryUnlinked)
            scmCatalogProvider.storeEntry(entryUnlinkedOther)
            // Mock links
            scmCatalogProvider.linkEntry(entryLinked, projectLinked)
            scmCatalogProvider.linkEntry(entryLinkedOther, projectLinkedOther)

            asAdmin {
                // Collection of entries
                scmCatalog.collectSCMCatalog { println(it) }
                // Collection of catalog links
                catalogLinkService.computeCatalogLinks()
            }

            // Running the test
            code(entryLinked, entryLinkedOther, entryUnlinked, entryUnlinkedOther)
        }
    }

}